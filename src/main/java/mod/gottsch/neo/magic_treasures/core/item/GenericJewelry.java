package mod.gottsch.neo.magic_treasures.core.item;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.JewelryState;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryRules;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryStats;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;
import java.util.Optional;

/**
 * 2.0 generic jewelry: {@code magictreasures:ring}, {@code necklace} and {@code bracelet}. One item per type; what a
 * piece is lives in its {@link JewelryIdentity} component, and its stats are computed from the datapack registries
 * ({@link mod.gottsch.neo.magic_treasures.core.capability.GenericJewelryHandler}).
 * <p>
 * A stack with no identity (a bare /give) is not jewelry: no handler, no tooltip stats, a plain gray model.
 */
public class GenericJewelry extends Jewelry {
    public static final String NAME_KEY = "jewelry." + MagicTreasures.MOD_ID + ".name";

    private final String type;

    public GenericJewelry(String type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public String getJewelryType() {
        return type;
    }

    /** the generic item for a jewelry type ({@code "ring"}, {@code "necklace"}, {@code "bracelet"}), if there is one */
    public static Optional<GenericJewelry> forType(String type) {
        return MagicTreasuresItems.GENERIC_JEWELRY.stream().map(item -> (GenericJewelry) item.get())
                .filter(item -> item.getJewelryType().equals(type)).findFirst();
    }

    /** a stack of this item with the given identity; its state is filled the first time a handler reads it */
    public ItemStack stack(JewelryIdentity identity) {
        ItemStack stack = new ItemStack(this);
        stack.set(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get(), identity);
        return stack;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        JewelryState state = stack.get(MagicTreasuresDataComponents.JEWELRY_STATE.get());
        return state != null && !state.spells().isEmpty();
    }

    /** a preset with vanishing_curse gets Curse of Vanishing when first carried (as 1.x jewelry, see Jewelry) */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide()) {
            return;
        }
        JewelryIdentity identity = stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
        if (identity != null && identity.preset().flatMap(JewelryDataIndex::preset).map(p -> p.value().vanishingCurse()).orElse(false)) {
            Holder<Enchantment> curse = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.VANISHING_CURSE);
            if (stack.getEnchantments().getLevel(curse) == 0) {
                stack.enchant(curse, 1);
            }
        }
    }

    @Override
    public boolean acceptsAffixer(ItemStack affixer) {
        // the generic handler decides from the identity and the rules
        return true;
    }

    @Override
    public boolean isNamed() {
        return false;
    }

    /*
     * names
     */
    @Override
    public Component getName(ItemStack stack) {
        JewelryIdentity identity = stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
        if (identity == null) {
            return super.getName(stack);
        }
        Optional<String> presetName = identity.preset().flatMap(JewelryDataIndex::preset).flatMap(p -> p.value().name());
        if (presetName.isPresent()) {
            // a datapack can ship without its resource pack's lang file: read the preset's path, not the raw key
            return Component.translatableWithFallback(presetName.get(), words(identity.preset().get().getPath()))
                    .withStyle(ChatFormatting.YELLOW);
        }
        return name(identity);
    }

    /**
     * "[Style] Material [Gem] Type". A combination that was a 1.x item keeps that item's name key, so it reads exactly
     * as before in every language ("Gran anillo de plata con zafiro"). Any other combination is composed from
     * {@code jewelry.magictreasures.name[.style][.gem]} patterns, which translators can reorder.
     */
    public static Component name(JewelryIdentity identity) {
        Optional<String> legacy = legacyNameKey(identity);
        if (legacy.isPresent()) {
            return Component.translatable(legacy.get());
        }
        Optional<Component> style = Optional.of(identity.style()).filter(id -> !JewelryRules.REGULAR.equals(id))
                .map(GenericJewelry::styleName);
        Optional<Component> gem = identity.gem().map(GenericJewelry::gemName);
        Component material = Component.translatableWithFallback("material." + identity.material().getNamespace() + "."
                + identity.material().getPath(), words(identity.material().getPath()));
        Component type = Component.translatable(LangUtil.tooltip("jewelry.type." + identity.type()));

        String key = NAME_KEY + style.map(s -> ".style").orElse("") + gem.map(g -> ".gem").orElse("");
        // arguments are always (style, material, gem, type); a pattern uses the ones it has
        return Component.translatable(key, style.orElse(Component.empty()), material, gem.orElse(Component.empty()), type);
    }

    /** item.magictreasures.[affix_]material_[gem_]type, when the current language has it */
    private static Optional<String> legacyNameKey(JewelryIdentity identity) {
        if (!identity.material().getNamespace().equals(MagicTreasures.MOD_ID)
                || !identity.style().getNamespace().equals(MagicTreasures.MOD_ID)) {
            return Optional.empty();
        }
        String affix = JewelryDataIndex.style(identity.style()).map(s -> s.value().affix(identity.style()))
                .orElse(identity.style().getPath());
        String key = "item." + MagicTreasures.MOD_ID + "." + (affix.isEmpty() ? "" : affix + "_")
                + identity.material().getPath() + "_" + identity.gem().map(g -> g.getPath() + "_").orElse("") + baseName(identity);
        return Language.getInstance().has(key) ? Optional.of(key) : Optional.empty();
    }

    /** the type, or a preset's base_name (castle_ring, hawk_ring), as the last part of 1.x names and textures */
    public static String baseName(JewelryIdentity identity) {
        return identity.preset().flatMap(JewelryDataIndex::preset).flatMap(p -> p.value().baseName()).orElse(identity.type());
    }

    private static Component styleName(ResourceLocation id) {
        Optional<String> key = JewelryDataIndex.style(id).flatMap(style -> style.value().name());
        return key.<Component>map(k -> Component.translatableWithFallback(k, words(id.getPath())))
                .orElseGet(() -> Component.literal(words(id.getPath())));
    }

    private static Component gemName(ResourceLocation id) {
        return JewelryDataIndex.gemstone(id)
                .flatMap(gem -> BuiltInRegistries.ITEM.getOptional(gem.value().item()))
                .<Component>map(item -> item.getDescription())
                .orElseGet(() -> Component.literal(words(id.getPath())));
    }

    private static String words(String path) {
        return WordUtils.capitalizeFully(path.replace('_', ' '));
    }

    /*
     * tooltip
     */
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        JewelryIdentity identity = stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
        if (identity != null) {
            JewelryStats stats = JewelryStats.of(identity);
            if (!stats.known()) {
                tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.unknown"),
                        identity.material() + ", " + identity.style()).withStyle(ChatFormatting.RED));
            } else if (!stats.gemKnown()) {
                tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.unknown_gem"),
                        identity.gem().map(ResourceLocation::toString).orElse("")).withStyle(ChatFormatting.RED));
            }
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    /** the preset's lore, if it has one */
    @Override
    public void appendLoreHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        presetLore(stack).ifPresent(key -> {
            for (String line : Component.translatable(key).getString().split("~")) {
                tooltip.add(Component.literal(line).withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC));
            }
            tooltip.add(Component.literal(LangUtil.NEWLINE));
        });
    }

    @Override
    protected boolean hasLore(ItemStack stack) {
        return presetLore(stack).isPresent();
    }

    /** the preset's lore key, if the current language has it (a lore line with no translation is left out) */
    private static Optional<String> presetLore(ItemStack stack) {
        JewelryIdentity identity = stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
        return identity == null ? Optional.empty()
                : identity.preset().flatMap(JewelryDataIndex::preset).flatMap(p -> p.value().lore())
                        .filter(key -> Language.getInstance().has(key));
    }
}
