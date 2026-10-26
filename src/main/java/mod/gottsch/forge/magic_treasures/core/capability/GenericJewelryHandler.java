package mod.gottsch.forge.magic_treasures.core.capability;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import net.minecraft.world.level.Level;
import mod.gottsch.forge.magic_treasures.core.client.tooltip.TooltipMarkers;
import mod.gottsch.forge.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.forge.magic_treasures.core.component.JewelryState;
import mod.gottsch.forge.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.forge.gottschcore.nbt.StackData;
import mod.gottsch.forge.magic_treasures.core.component.SpellData;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.GemstoneDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryMaterialDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryPresetDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryRules;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryStats;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.SpellFactors;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.forge.magic_treasures.core.spell.SpellEntity;
import mod.gottsch.forge.magic_treasures.core.spell.SpellRegistry;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * A write-through view of a 2.0 jewelry stack: its {@link JewelryIdentity} (what it is) and {@link JewelryState}
 * (uses, mana, repairs, recharges, spells). Max values, level and factors come from {@link JewelryStats}, computed
 * from the identity and the datapack registries, and are never stored.
 */
public class GenericJewelryHandler implements IJewelryHandler {
    private static final int MANA_BAR_RGB = 0x5555FF;
    private static final int DURABILITY_BAR_RGB = 0x55AA55;
    private static final int INFINITE_BAR_RGB = 0x777777;
    private static final String SUBTITLE_SEPARATOR = " · ";

    private final ItemStack stack;

    private GenericJewelryHandler(ItemStack stack) {
        this.stack = stack;
    }

    /**
     * A handler for a stack with a jewelry identity, or empty. A stack without state (made by /give, or a loot
     * function that set only the identity) gets full uses, mana, repairs and recharges here, the first time it's read.
     * That waits until its stats can be computed, i.e. until the data is loaded and the material is known.
     */
    public static Optional<IJewelryHandler> get(ItemStack stack) {
        JewelryIdentity identity = StackData.get(stack, MagicTreasuresDataComponents.JEWELRY_IDENTITY);
        if (identity == null) {
            return Optional.empty();
        }
        if (!StackData.has(stack, MagicTreasuresDataComponents.JEWELRY_STATE)) {
            JewelryStats stats = JewelryStats.of(identity);
            if (stats.known()) {
                StackData.set(stack, MagicTreasuresDataComponents.JEWELRY_STATE, fullState(identity, stats));
            }
        }
        return Optional.of(new GenericJewelryHandler(stack));
    }

    /** full uses, mana, repairs and recharges, and a preset's spells (unknown spell ids are skipped) */
    public static JewelryState fullState(JewelryIdentity identity, JewelryStats stats) {
        List<SpellData> spells = identity.preset().flatMap(JewelryDataIndex::preset)
                .map(preset -> preset.value().spells().stream()
                        .flatMap(id -> SpellRegistry.get(id).stream())
                        .map(spell -> spell.entity().toData())
                        .toList())
                .orElse(List.of());
        return new JewelryState(stats.maxUses(), stats.maxMana(), stats.maxRepairs(), stats.maxRecharges(), spells);
    }

    /** write one spell's persisted state back to the stack (used by bound SpellEntity instances) */
    static void updateSpell(ItemStack stack, int index, SpellData spell) {
        JewelryState state = StackData.get(stack, MagicTreasuresDataComponents.JEWELRY_STATE);
        if (state != null && index >= 0 && index < state.spells().size()) {
            StackData.set(stack, MagicTreasuresDataComponents.JEWELRY_STATE, state.withSpell(index, spell));
        }
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    /*
     * identity, state and stats
     */
    @Override
    public JewelryIdentity getIdentity() {
        return StackData.get(stack, MagicTreasuresDataComponents.JEWELRY_IDENTITY);
    }

    @Override
    public String getType() {
        return getIdentity().type();
    }

    @Override
    public ResourceLocation getMaterial() {
        return getIdentity().material();
    }

    @Override
    public ResourceLocation getStyle() {
        return getIdentity().style();
    }

    @Override
    public Optional<ResourceLocation> getGem() {
        return getIdentity().gem();
    }

    @Override
    public Optional<ResourceLocation> getPreset() {
        return getIdentity().preset();
    }

    @Override
    public JewelryStats getStats() {
        return JewelryStats.of(getIdentity());
    }

    @Override
    public MagicTreasuresRarity getRarity() {
        return getStats().rarity();
    }

    private JewelryState state() {
        return StackData.getOrDefault(stack, MagicTreasuresDataComponents.JEWELRY_STATE, JewelryState.EMPTY);
    }

    private void updateState(UnaryOperator<JewelryState> operator) {
        StackData.set(stack, MagicTreasuresDataComponents.JEWELRY_STATE, operator.apply(state()));
    }

    private Optional<Holder.Reference<GemstoneDef>> gemstone() {
        return getIdentity().gem().flatMap(JewelryDataIndex::gemstone);
    }

    private Optional<Holder.Reference<JewelryPresetDef>> preset() {
        return getIdentity().preset().flatMap(JewelryDataIndex::preset);
    }

    /*
     * computed values
     */
    @Override
    public int getMaxUses() {
        return getStats().maxUses();
    }

    @Override
    public double getMaxMana() {
        return getStats().maxMana();
    }

    @Override
    public int getMaxRepairs() {
        return getStats().maxRepairs();
    }

    @Override
    public int getMaxRecharges() {
        return getStats().maxRecharges();
    }

    @Override
    public int getMaxLevel() {
        return getStats().maxLevel();
    }

    private SpellFactors factors() {
        return getStats().factors();
    }

    @Override
    public double getSpellCostFactor() {
        return factors().cost();
    }

    @Override
    public double getSpellEffectAmountFactor() {
        return factors().effect();
    }

    @Override
    public double getSpellDurationFactor() {
        return factors().duration();
    }

    @Override
    public double getSpellCooldownFactor() {
        return factors().cooldown();
    }

    @Override
    public double getSpellRangeFactor() {
        return factors().range();
    }

    @Override
    public double modifySpellCost(double cost) {
        return cost * getSpellCostFactor();
    }

    @Override
    public double modifyEffectAmount(double amount) {
        return amount * getSpellEffectAmountFactor();
    }

    @Override
    public int modifyDuration(int duration) {
        return (int)(duration * getSpellDurationFactor());
    }

    @Override
    public long modifyCooldown(long cooldown) {
        return (long)(cooldown * getSpellCooldownFactor());
    }

    @Override
    public double modifyRange(double range) {
        return range * getSpellRangeFactor();
    }

    /*
     * state. Reads are capped at the current max, so a datapack that lowers a material's mana takes effect on
     * existing stacks; infinite uses stay infinite.
     */
    @Override
    public int getUses() {
        int uses = state().uses();
        return uses == Integer.MAX_VALUE ? uses : Math.min(uses, getMaxUses());
    }

    @Override
    public void setUses(int uses) {
        updateState(s -> s.withUses(uses));
    }

    @Override
    public boolean isInfinite() {
        return getUses() == Integer.MAX_VALUE;
    }

    @Override
    public double getMana() {
        return Math.min(state().mana(), getMaxMana());
    }

    @Override
    public void setMana(double mana) {
        updateState(s -> s.withMana(mana));
    }

    @Override
    public int getRepairs() {
        return Math.min(state().repairs(), getMaxRepairs());
    }

    @Override
    public void setRepairs(int repairs) {
        updateState(s -> s.withRepairs(repairs));
    }

    @Override
    public int getRecharges() {
        return Math.min(state().recharges(), getMaxRecharges());
    }

    @Override
    public void setRecharges(int recharges) {
        updateState(s -> s.withRecharges(recharges));
    }

    @Override
    public List<SpellEntity> getSpells() {
        List<SpellData> spells = state().spells();
        List<SpellEntity> entities = new ArrayList<>(spells.size());
        for (int i = 0; i < spells.size(); i++) {
            final int index = i;
            SpellEntity.fromData(spells.get(i)).ifPresent(entity -> entities.add(entity.bind(stack, index)));
        }
        return Collections.unmodifiableList(entities);
    }

    @Override
    public void setSpells(List<SpellEntity> spells) {
        updateState(s -> s.withSpells(spells.stream().map(SpellEntity::toData).toList()));
    }

    @Override
    public void addSpell(SpellEntity spell) {
        updateState(s -> s.addSpell(spell.toData()));
    }

    /*
     * the gem. Callers pass and expect the gem's *item* id; the identity stores the gemstone id. They are equal for
     * built-in gems, not for a pack's (mypack:amber is minecraft:honeycomb in the test pack).
     */
    @Override
    public ResourceLocation getStone() {
        return getIdentity().gem().map(id -> gemstone().map(gem -> gem.value().item()).orElse(id)).orElse(null);
    }

    @Override
    public void setStone(ResourceLocation item) {
        Optional<ResourceLocation> gem = Optional.empty();
        // null or minecraft:air removes the gem
        if (item != null && !item.equals(BuiltInRegistries.ITEM.getKey(Items.AIR))) {
            gem = BuiltInRegistries.ITEM.getOptional(item).flatMap(JewelryDataIndex::gemstone).map(h -> h.key().location());
            if (gem.isEmpty()) {
                MagicTreasures.LOGGER.warn("setStone: {} is not a gemstone; the gem is unchanged", item);
                return;
            }
        }
        JewelryIdentity identity = getIdentity();
        StackData.set(stack, MagicTreasuresDataComponents.JEWELRY_IDENTITY,
                new JewelryIdentity(identity.type(), identity.material(), gem, identity.style(), identity.preset()));
    }

    @Override
    public boolean hasStone() {
        return gemstone().isPresent();
    }

    /** a gem may be set if the rules allow it and a preset doesn't forbid gems; anything else is accepted */
    @Override
    public boolean acceptsAffixer(ItemStack affixer) {
        Optional<Holder.Reference<GemstoneDef>> gem = JewelryDataIndex.gemstone(affixer.getItem());
        if (gem.isEmpty()) {
            return true;
        }
        Optional<Holder.Reference<JewelryMaterialDef>> material = JewelryDataIndex.material(getIdentity().material());
        return material.isPresent() && JewelryRules.allowsGem(material.get(), gem.get(), preset());
    }

    @Override
    public boolean presetAcceptsGems() {
        return preset().map(p -> p.value().acceptsGems()).orElse(true);
    }

    @Override
    public boolean gemExtractable() {
        return preset().map(p -> p.value().extractable()).orElse(true);
    }

    /*
     * tooltip
     */
    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flag) {
        // mana and durability bars
        tooltip.add(TooltipMarkers.bar(MagicTreasures.MOD_ID,
                Component.translatable(LangUtil.tooltip("jewelry.bar.mana")).withStyle(ChatFormatting.BLUE),
                Component.literal(Math.round(getMana()) + "/" + (long) Math.ceil(getMaxMana())),
                getMaxMana() > 0 ? getMana() / getMaxMana() : 0, MANA_BAR_RGB));
        Component durabilityLabel = Component.translatable(LangUtil.tooltip("jewelry.bar.durability")).withStyle(ChatFormatting.GRAY);
        if (isInfinite()) {
            tooltip.add(TooltipMarkers.bar(MagicTreasures.MOD_ID, durabilityLabel, Component.literal("∞"), 1.0, INFINITE_BAR_RGB));
        } else {
            tooltip.add(TooltipMarkers.bar(MagicTreasures.MOD_ID, durabilityLabel,
                    Component.literal(getUses() + "/" + getMaxUses()),
                    getMaxUses() > 0 ? (double) getUses() / getMaxUses() : 0, DURABILITY_BAR_RGB));
        }

        if (StackData.has(stack, MagicTreasuresDataComponents.TEMPERED)) {
            tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.tempered")).withStyle(ChatFormatting.GOLD));
        }

        // spells: name, flavor line and stat chips each, with a blank line between spells. Holding SHIFT shows the
        // details and the full set block instead; with the spells too, the tooltip is taller than the screen
        List<SpellEntity> spells = getSpells();
        if (!spells.isEmpty() && !Screen.hasShiftDown()) {
            tooltip.add(TooltipMarkers.divider(MagicTreasures.MOD_ID));
            for (int i = 0; i < spells.size(); i++) {
                if (i > 0) {
                    tooltip.add(Component.literal(LangUtil.NEWLINE));
                }
                spells.get(i).getSpell().addInformation(stack, context, tooltip, flag, spells.get(i));
            }
        }

        // modifiers, two per row; only the ones this jewelry has
        List<Component> modifiers = new ArrayList<>(5);
        addModifier(modifiers, "cost", getSpellCostFactor());
        addModifier(modifiers, "cooldown", getSpellCooldownFactor());
        addModifier(modifiers, "effect", getSpellEffectAmountFactor());
        addModifier(modifiers, "duration", getSpellDurationFactor());
        addModifier(modifiers, "range", getSpellRangeFactor());
        if (!modifiers.isEmpty()) {
            tooltip.add(TooltipMarkers.divider(MagicTreasures.MOD_ID));
            tooltip.add(TooltipMarkers.grid(MagicTreasures.MOD_ID, 2, modifiers));
        }

        // advanced tooltip (hold shift)
        LangUtil.appendAdvancedHoverText(tooltip, tt -> {
            tooltip.add(Component.literal(LangUtil.NEWLINE));
            tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.usage")).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
            // material
            tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.material"), ChatFormatting.GREEN + WordUtils.capitalizeFully(getMaterial().getPath())));
            // stones
            if (hasStone()) {
                tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.stone"), ChatFormatting.YELLOW + WordUtils.capitalizeFully(getStone().getPath().replace("_", " "))));
            }
            if (!isInfinite()) {
                tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.durability.repairs"), ChatFormatting.GRAY + String.valueOf(getRepairs())));
            }
            tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.mana.recharges"), ChatFormatting.BLUE + String.valueOf(getRecharges())));
        });
    }

    /** The header's second line: "Ring · Ruby · Lvl 4". */
    @Override
    public Component getSubtitle() {
        MutableComponent subtitle = Component.translatable(LangUtil.tooltip("jewelry.type." + getType()));
        if (hasStone()) {
            BuiltInRegistries.ITEM.getOptional(getStone()).ifPresent(stone -> subtitle.append(SUBTITLE_SEPARATOR).append(stone.getDescription()));
        }
        subtitle.append(SUBTITLE_SEPARATOR).append(Component.translatable(LangUtil.tooltip("jewelry.level"),
                Component.literal(String.valueOf(getMaxLevel())).withStyle(ChatFormatting.GOLD)));
        return subtitle.withStyle(ChatFormatting.GRAY);
    }

    private void addModifier(List<Component> modifiers, String stat, double factor) {
        if (factor != 1.0) {
            modifiers.add(Component.translatable(LangUtil.tooltip("jewelry.modifier." + stat),
                    Component.literal(formatStat(factor)).withStyle(ChatFormatting.AQUA)));
        }
    }

    private String formatStat(double value) {
        if (value < 1.0) {
            return LangUtil.negativePercent(value);
        } else if (value > 1.0) {
            return LangUtil.positivePercent(value);
        }
        return "";
    }
}
