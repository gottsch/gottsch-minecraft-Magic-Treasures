package mod.gottsch.neo.magic_treasures.core.capability;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.client.tooltip.TooltipMarkers;
import mod.gottsch.neo.magic_treasures.core.registry.StoneRegistry;
import mod.gottsch.neo.magic_treasures.core.spell.SpellEntity;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * What every jewelry handler shares: the tooltip, the subtitle and the spell modifiers, all written against the
 * {@link IJewelryHandler} getters. {@link JewelryHandler} views a 1.x item's JewelryData;
 * {@link GenericJewelryHandler} views a 2.0 generic item's identity + state.
 */
public abstract class AbstractJewelryHandler implements IJewelryHandler {
    private static final int MANA_BAR_RGB = 0x5555FF;
    private static final int DURABILITY_BAR_RGB = 0x55AA55;
    private static final int INFINITE_BAR_RGB = 0x777777;
    private static final String SUBTITLE_SEPARATOR = " \u00B7 ";


    protected final ItemStack stack;

    protected AbstractJewelryHandler(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    /** the material id shown in the advanced tooltip */
    protected abstract ResourceLocation materialId();

    @Override
    public boolean isUpgradable() {
        return getStone() == null || hasStone();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // mana and durability bars
        tooltip.add(TooltipMarkers.bar(MagicTreasures.MOD_ID,
                Component.translatable(LangUtil.tooltip("jewelry.bar.mana")).withStyle(ChatFormatting.BLUE),
                Component.literal(Math.round(getMana()) + "/" + (long) Math.ceil(getMaxMana())),
                getMaxMana() > 0 ? getMana() / getMaxMana() : 0, MANA_BAR_RGB));
        Component durabilityLabel = Component.translatable(LangUtil.tooltip("jewelry.bar.durability")).withStyle(ChatFormatting.GRAY);
        if (isInfinite()) {
            tooltip.add(TooltipMarkers.bar(MagicTreasures.MOD_ID, durabilityLabel, Component.literal("\u221E"), 1.0, INFINITE_BAR_RGB));
        } else {
            tooltip.add(TooltipMarkers.bar(MagicTreasures.MOD_ID, durabilityLabel,
                    Component.literal(getUses() + "/" + getMaxUses()),
                    getMaxUses() > 0 ? (double) getUses() / getMaxUses() : 0, DURABILITY_BAR_RGB));
        }

        // spells: name, flavor line and stat chips each, with a blank line between spells
        List<SpellEntity> spells = getSpells();
        if (!spells.isEmpty()) {
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
            tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.material"), ChatFormatting.GREEN + WordUtils.capitalizeFully(materialId().getPath())));
            // stones
            if (hasStone()) {
                tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.stone"), ChatFormatting.YELLOW + WordUtils.capitalizeFully(getStone().getPath().replace("_", " "))));
            }
            if (!isInfinite()) {
                tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.durability.repairs"), ChatFormatting.GRAY + String.valueOf(getRepairs())));
            }
            tooltip.add(Component.translatable(LangUtil.tooltip("jewelry.mana.recharges"), ChatFormatting.BLUE + String.valueOf(getRecharges())));
            appendSpecialHoverText(stack, context, tooltip, flag);
        });
    }

    /** The header's second line: "Ring · Ruby · Lvl 4". */
    public Component getSubtitle() {
        MutableComponent subtitle = Component.translatable(LangUtil.tooltip("jewelry.type." + getJewelryType().getValue()));
        if (hasStone()) {
            StoneRegistry.get(getStone()).ifPresent(stone -> subtitle.append(SUBTITLE_SEPARATOR).append(stone.getDescription()));
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

    @Override
    public void appendSpecialHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // TODO this might be moot as this can't be anonymously set because a Handler class is instantiated by a Builder.
    }

    private String formatStat(double value) {
        if (value < 1.0) {
            return LangUtil.negativePercent(value);
        } else if (value > 1.0) {
            return LangUtil.positivePercent(value);
        }
        return "";
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

    @Override
    public void setInfinite() {
        setUses(Integer.MAX_VALUE);
    }

    @Override
    public boolean isInfinite() {
        return getUses() == Integer.MAX_VALUE;
    }

}
