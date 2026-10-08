/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * Magic Treasures is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Magic Treasures is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Magic Treasures.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neo.magic_treasures.core.capability;

import mod.gottsch.neo.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.neo.magic_treasures.core.component.JewelryData;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.component.SpellData;
import mod.gottsch.neo.magic_treasures.core.item.IJewelrySizeTier;
import mod.gottsch.neo.magic_treasures.core.item.IJewelryType;
import mod.gottsch.neo.magic_treasures.core.item.Jewelry;
import mod.gottsch.neo.magic_treasures.core.item.JewelryType;
import mod.gottsch.neo.magic_treasures.core.jewelry.*;
import mod.gottsch.neo.magic_treasures.core.registry.StoneRegistry;
import mod.gottsch.neo.magic_treasures.core.spell.SpellEntity;
import mod.gottsch.neo.magic_treasures.core.tag.MagicTreasuresTags;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A write-through view of a jewelry stack's JewelryData component.
 * Getters read the stack's current component; setters write a new component back to the stack.
 * The stack is the only source of truth, so any number of handlers for the same stack stay consistent.
 * <p>
 * Created by Mark Gottschling on 6/1/2023
 */
public class JewelryHandler implements IJewelryHandler {

    private final ItemStack stack;

    private JewelryHandler(ItemStack stack) {
        this.stack = stack;
    }

    /**
     * Returns a handler for a jewelry stack, or empty if the stack isn't jewelry.
     * <p>
     * A jewelry stack without the component gets its item's default data on first access. The default
     * can't be a static item default: it depends on stone tiers, which come from item tags that aren't
     * loaded at startup. (This is the same moment the 1.20.1 capability was created.)
     */
    public static Optional<IJewelryHandler> get(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        if (!stack.has(MagicTreasuresDataComponents.JEWELRY)) {
            if (stack.getItem() instanceof Jewelry jewelry) {
                stack.set(MagicTreasuresDataComponents.JEWELRY, jewelry.jewelryDefaults().build());
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(new JewelryHandler(stack));
    }

    /**
     * Write one spell's persisted state back to the stack (used by bound SpellEntity instances).
     */
    public static void updateSpell(ItemStack stack, int index, SpellData spell) {
        JewelryData data = stack.get(MagicTreasuresDataComponents.JEWELRY);
        if (data != null && index >= 0 && index < data.spells().size()) {
            stack.set(MagicTreasuresDataComponents.JEWELRY, data.withSpell(index, spell));
        }
    }

    private JewelryData data() {
        return Objects.requireNonNull(stack.get(MagicTreasuresDataComponents.JEWELRY), "jewelry stack has no jewelry component");
    }

    private void update(UnaryOperator<JewelryData> operator) {
        stack.set(MagicTreasuresDataComponents.JEWELRY, operator.apply(data()));
    }

    private void updateStats(UnaryOperator<JewelryData.Stats> operator) {
        update(d -> d.withStats(operator.apply(d.stats())));
    }

    private void updateFactors(UnaryOperator<JewelryData.Factors> operator) {
        update(d -> d.withFactors(operator.apply(d.factors())));
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    /*
     * Builds the default JewelryData for a jewelry item. Values left unset (-1) are calculated from
     * the material, size and stone tier.
     */
    public static class Builder {
        public final IJewelryType type;
        public JewelryMaterial material;
        public IJewelrySizeTier sizeTier;
        public int maxUses = -1;
        public int uses;
        public int maxLevel = -1;
        public double maxMana = -1;
        public double mana;
        public int maxRepairs = -1;
        public int maxRecharges = -1;

        public ResourceLocation stone;
        public List<SpellEntity> spells = new ArrayList<>();
        public String baseName;
        // NOTE affixer is not persisted. Jewelry items read it from their builder.
        public Predicate<ItemStack> acceptsAffixer = p -> true;
        // NOTE not persisted. Jewelry applies Curse of Vanishing on its first inventory tick (needs a level's registries).
        public boolean vanishingCurse;

        public double spellCostFactor = -1.0;
        public double spellEffectAmountFactor = -1.0;
        public double spellFrequencyFactor = -1.0;
        public double spellDurationFactor = -1.0;
        public double spellCooldownFactor = -1.0;
        public double spellRangeFactor = -1.0;

        /**
         * TODO use this constructor
         * @param type
         * @param material
         */
        public Builder(IJewelryType type, JewelryMaterial material) {
            this.type = type;
            this.material = material;
            this.sizeTier = JewelrySizeTier.REGULAR;
        }

        public Builder(IJewelryType type, JewelryMaterial material, IJewelrySizeTier size) {
            this.type = type;
            this.material = material;
            this.sizeTier = size;
        }

        public Builder(IJewelryType type, JewelryMaterial material, ResourceLocation stone, IJewelrySizeTier sizeTier) {
            this.type = type;
            this.material = material;
            this.stone = stone;
            this.sizeTier = sizeTier;
        }

        public Builder with(Consumer<Builder> builder) {
            builder.accept(this);
            return this;
        }

        public Builder withStone(ResourceLocation stone) {
            this.stone = stone;
            return this;

        }

        public Builder withSize(JewelrySizeTier size) {
            this.sizeTier = size;
            return this;
        }

        public Builder setInfinite() {
            this.maxUses = Integer.MAX_VALUE;
            return this;
        }

        /*
         * getters for datagen, which reads an item's defaults directly: data runs don't fire
         * FMLCommonSetupEvent, so the API registries a JewelryHandler resolves names through are empty.
         */
        public IJewelryType getJewelryType() { return type; }
        public JewelryMaterial getMaterial() { return material; }
        public IJewelrySizeTier getJewelrySizeTier() { return sizeTier; }
        public ResourceLocation getStone() { return stone; }

        /**
         * NOTE requires stone tiers, ie the item tags must be loaded.
         */
        public JewelryData build() {
            // get the stone and stone tier
            Item stoneItem = StoneRegistry.get(this.stone).orElse(Items.AIR);
            // determine the tier
            Optional<JewelryStoneTier> stoneTier = StoneRegistry.getStoneTier(stoneItem);
            JewelryStoneTier tier = stoneTier.orElse(JewelryStoneTiers.NONE);

            int maxUses = this.maxUses <= 0 ? Math.round(material.getUses() * sizeTier.getUsesMultiplier()) : this.maxUses;
            int maxLevel = this.maxLevel <= 0 ? material.getMaxLevel() + sizeTier.getCode() : this.maxLevel;
            double maxMana;
            if (this.maxMana <= 0) {
                int mana = stoneTier.map(JewelryStoneTier::getMana).orElse(0);
                maxMana = Math.round((material.getMana() + mana) * sizeTier.getManaMultiplier());
            } else {
                maxMana = this.maxMana;
            }
            int maxRepairs = this.maxRepairs < 0 ? material.getRepairs() + sizeTier.getRepairs() : this.maxRepairs;
            int maxRecharges = this.maxRecharges < 0
                    ? material.getRecharges() + stoneTier.map(JewelryStoneTier::getRecharges).orElse(0)
                    : this.maxRecharges;

            // full uses, mana, repairs and recharges
            JewelryData.Stats stats = new JewelryData.Stats(maxUses, maxUses, maxLevel, maxMana, maxMana,
                    maxRepairs, maxRepairs, maxRecharges, maxRecharges);

            // spell factor calculations
            JewelryData.Factors factors = new JewelryData.Factors(
                    spellCostFactor < 0 ? material.getSpellCostFactor() * tier.getSpellCostFactor() : spellCostFactor,
                    spellEffectAmountFactor < 0 ? material.getSpellEffectAmountFactor() * tier.getSpellEffectAmountFactor() : spellEffectAmountFactor,
                    spellFrequencyFactor < 0 ? material.getSpellFrequencyFactor() * tier.getSpellFrequencyFactor() : spellFrequencyFactor,
                    spellDurationFactor < 0 ? material.getSpellDurationFactor() * tier.getSpellDurationFactor() : spellDurationFactor,
                    spellCooldownFactor < 0 ? material.getSpellCooldownFactor() * tier.getSpellCooldownFactor() : spellCooldownFactor,
                    spellRangeFactor < 0 ? material.getSpellRangeFactor() * tier.getSpellRangeFactor() : spellRangeFactor);

            return new JewelryData(
                    type.getName(),
                    material.getId(),
                    sizeTier.getName(),
                    Optional.ofNullable(stone),
                    Optional.ofNullable(StringUtils.isNotBlank(baseName) ? baseName : null),
                    stats,
                    factors,
                    spells.stream().map(SpellEntity::toData).toList());
        }
    }

    @Override
    // convenience method
    public JewelryStoneTier getStoneTier() {
        // get the stone and stone tier
        Item stone = StoneRegistry.get(getStone()).orElse(Items.AIR);
        // determine the tier
        return StoneRegistry.getStoneTier(stone).orElseGet(() -> JewelryStoneTiers.NONE);
    }

    @Override
    public boolean isUpgradable() {
        return getStone() == null || hasStone();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        // spell max level
        tooltip.add(Component.translatable(LangUtil.INDENT2).append(Component.translatable(LangUtil.tooltip("jewelry.max_level"),
                ChatFormatting.GOLD + String.valueOf(getMaxLevel()))));

        // durability
        if (isInfinite()) {
            tooltip.add(Component.translatable(LangUtil.INDENT2)
                    .append(Component.translatable(LangUtil.tooltip("jewelry.durability.infinite"), Component.translatable(LangUtil.tooltip("infinite")).withStyle(ChatFormatting.GRAY))));
        } else {
            tooltip.add(Component.translatable(LangUtil.INDENT2)
                    .append(Component.translatable(LangUtil.tooltip("jewelry.durability.amount"),
                    ChatFormatting.GRAY + String.valueOf(getUses()),
                    ChatFormatting.GRAY + String.valueOf(getMaxUses()))));
        }

        // mana
        tooltip.add(Component.translatable(LangUtil.INDENT2)
                .append(Component.translatable(LangUtil.tooltip("jewelry.mana"),
                        ChatFormatting.BLUE + String.valueOf(Math.toIntExact(Math.round(getMana()))),
                        ChatFormatting.BLUE + String.valueOf(Math.toIntExact((long)Math.ceil(getMaxMana()))))));

        List<SpellEntity> spells = getSpells();
        if (!spells.isEmpty()) {
            tooltip.add(Component.translatable(LangUtil.NEWLINE));
            tooltip.add(Component.translatable(LangUtil.INDENT2)
                    .append(Component.translatable(LangUtil.tooltip("divider")).withStyle(ChatFormatting.GRAY)));

            // add spells
            for (SpellEntity entity : spells) {
                entity.getSpell().addInformation(stack, context, tooltip, flag, entity);
            }
        }

        // -----------
        MutableComponent component = Component.translatable(LangUtil.INDENT2);
        Optional<MutableComponent> c = Optional.empty();
        if (getSpellCostFactor() != 1.0) {
            c = Optional.of(component);
            component.append(Component.translatable(LangUtil.tooltip("jewelry.stats.cost_factor"), ChatFormatting.AQUA + formatStat(getSpellCostFactor())))
                    .append(" ");
        }
        if (getSpellCooldownFactor() != 1.0) {
            c = c.isEmpty() ? Optional.of(component) : c;
            component.append(Component.translatable(LangUtil.tooltip("jewelry.stats.cooldown_factor"), ChatFormatting.AQUA + formatStat(getSpellCooldownFactor())))
                    .append(" ");
        }
        if (getSpellEffectAmountFactor() != 1.0) {
            c = c.isEmpty() ? Optional.of(component) : c;
            component.append(Component.translatable(LangUtil.tooltip("jewelry.stats.effect_amount_factor"), ChatFormatting.AQUA + formatStat(getSpellEffectAmountFactor())))
                    .append(" ");
        }
        if (getSpellFrequencyFactor() != 1.0) {
            c = c.isEmpty() ? Optional.of(component) : c;
            component.append(Component.translatable(LangUtil.tooltip("jewelry.stats.frequency_factor"), ChatFormatting.AQUA + formatStat(1.0 + (1.0 - getSpellFrequencyFactor()))))
                    .append(" ");
        }
        if (getSpellRangeFactor() != 1.0) {
            c = c.isEmpty() ? Optional.of(component) : c;
            component.append(Component.translatable(LangUtil.tooltip("jewelry.stats.range_factor"), ChatFormatting.AQUA + formatStat(getSpellRangeFactor())))
                    .append(" ");
        }
        c.ifPresent(x -> {
            tooltip.add(Component.translatable(LangUtil.NEWLINE));
            tooltip.add(Component.translatable(LangUtil.INDENT2).append(Component.translatable(LangUtil.tooltip("divider")).withStyle(ChatFormatting.GRAY)));
            tooltip.add(component);
        });
        // ------------

        // advanced tooltip (hold shift)
        LangUtil.appendAdvancedHoverText(tooltip, tt -> {
            tooltip.add(Component.translatable(LangUtil.NEWLINE));
            // material
            tooltip.add(Component.translatable(LangUtil.INDENT2).append(Component.translatable(LangUtil.tooltip("jewelry.material"), ChatFormatting.GREEN + WordUtils.capitalizeFully(getMaterial().getId().getPath()))));
            // stones
            if (hasStone()) {
                tooltip.add(Component.translatable(LangUtil.INDENT2).append(Component.translatable(LangUtil.tooltip("jewelry.stone"), ChatFormatting.YELLOW + WordUtils.capitalizeFully(getStone().getPath().replace("_", " ")))));
            }
            if (!isInfinite()) {
                tooltip.add(Component.translatable(LangUtil.INDENT2).append(Component.translatable(LangUtil.tooltip("jewelry.durability.repairs"), ChatFormatting.GRAY + String.valueOf(getRepairs()))));
            } // TODO add else ? to display 0 repairs?
            tooltip.add(Component.translatable(LangUtil.INDENT2).append(Component.translatable(LangUtil.tooltip("jewelry.mana.recharges"), ChatFormatting.BLUE + String.valueOf(getRecharges()))));
            tooltip.add(Component.translatable(LangUtil.NEWLINE));

            appendSpecialHoverText(stack, context, tooltip, flag);
        });
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
    public long modifyFrequency(long frequency) {
        return (long)(frequency * getSpellFrequencyFactor());
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

    @Override
    public JewelryMaterial getMaterial() {
        return MagicTreasuresApi.getJewelryMaterial(data().material()).orElse(JewelryMaterials.NONE);
    }

    @Override
    public IJewelrySizeTier getJewelrySizeTier() {
        return MagicTreasuresApi.getJewelrySize(data().sizeTier()).orElse(JewelrySizeTier.UNKNOWN);
    }

    @Override
    public IJewelryType getJewelryType() {
        return MagicTreasuresApi.getJewelryType(data().type()).orElse(JewelryType.UNKNOWN);
    }

    @Override
    public int getMaxUses() {
        return data().stats().maxUses();
    }

    @Override
    public void setMaxUses(int maxUses) {
        updateStats(s -> s.withMaxUses(maxUses));
    }

    @Override
    public int getUses() {
        return data().stats().uses();
    }

    @Override
    public void setUses(int uses) {
        updateStats(s -> s.withUses(uses));
    }

    @Override
    public double getMaxMana() {
        return data().stats().maxMana();
    }

    @Override
    public void setMaxMana(double maxMana) {
        updateStats(s -> s.withMaxMana(maxMana));
    }

    @Override
    public double getMana() {
        return data().stats().mana();
    }

    @Override
    public void setMana(double mana) {
        updateStats(s -> s.withMana(mana));
    }

    @Override
    public int getMaxRepairs() {
        return data().stats().maxRepairs();
    }

    @Override
    public void setMaxRepairs(int repairs) {
        updateStats(s -> s.withMaxRepairs(repairs));
    }

    @Override
    public int getRepairs() {
        return data().stats().repairs();
    }

    @Override
    public void setRepairs(int repairs) {
        updateStats(s -> s.withRepairs(repairs));
    }

    @Override
    public int getMaxLevel() {
        return data().stats().maxLevel();
    }

    @Override
    public void setMaxLevel(int maxLevel) {
        updateStats(s -> s.withMaxLevel(maxLevel));
    }

    @Override
    public ResourceLocation getStone() {
        return data().stone().orElse(null);
    }

    @Override
    public void setStone(ResourceLocation stone) {
        update(d -> d.withStone(Optional.ofNullable(stone)));
    }

    @Override
    public boolean hasStone() {
        ResourceLocation stone = getStone();
        if (stone != null) {
            Item stoneItem = BuiltInRegistries.ITEM.get(stone);
            // TODO could check the StoneRegistry instead.
            return stoneItem != Items.AIR
                    && stoneItem.builtInRegistryHolder().is(MagicTreasuresTags.Items.STONES);
        }
        return false;
    }

    @Override
    public List<SpellEntity> getSpells() {
        List<SpellData> spells = data().spells();
        List<SpellEntity> entities = new ArrayList<>(spells.size());
        for (int i = 0; i < spells.size(); i++) {
            final int index = i;
            SpellEntity.fromData(spells.get(i)).ifPresent(entity -> entities.add(entity.bind(stack, index)));
        }
        return Collections.unmodifiableList(entities);
    }

    @Override
    public void setSpells(List<SpellEntity> spells) {
        update(d -> d.withSpells(spells.stream().map(SpellEntity::toData).toList()));
    }

    @Override
    public void addSpell(SpellEntity spell) {
        update(d -> d.addSpell(spell.toData()));
    }

    @Override
    public int getRecharges() {
        return data().stats().recharges();
    }

    @Override
    public void setRecharges(int recharges) {
        updateStats(s -> s.withRecharges(recharges));
    }

    @Override
    public int getMaxRecharges() {
        return data().stats().maxRecharges();
    }

    @Override
    public void setMaxRecharges(int maxRecharges) {
        updateStats(s -> s.withMaxRecharges(maxRecharges));
    }

    @Override
    public String getBaseName() {
        return data().baseName().orElse(null);
    }

    @Override
    public void setBaseName(String baseName) {
        update(d -> d.withBaseName(Optional.ofNullable(StringUtils.isNotBlank(baseName) ? baseName : null)));
    }

    @Override
    public boolean acceptsAffixer(ItemStack affixer) {
        return !(stack.getItem() instanceof Jewelry jewelry) || jewelry.acceptsAffixer(affixer);
    }

    @Override
    public double getSpellCostFactor() {
        return data().factors().spellCost();
    }

    @Override
    public void setSpellCostFactor(double spellCostFactor) {
        updateFactors(f -> f.withSpellCost(spellCostFactor));
    }

    @Override
    public double getSpellEffectAmountFactor() {
        return data().factors().spellEffectAmount();
    }

    @Override
    public void setSpellEffectAmountFactor(double spellEffectAmountFactor) {
        updateFactors(f -> f.withSpellEffectAmount(spellEffectAmountFactor));
    }

    @Override
    public double getSpellFrequencyFactor() {
        return data().factors().spellFrequency();
    }

    @Override
    public void setSpellFrequencyFactor(double spellFrequencyFactor) {
        updateFactors(f -> f.withSpellFrequency(spellFrequencyFactor));
    }

    @Override
    public double getSpellDurationFactor() {
        return data().factors().spellDuration();
    }

    @Override
    public void setSpellDurationFactor(double spellDurationFactor) {
        updateFactors(f -> f.withSpellDuration(spellDurationFactor));
    }

    @Override
    public double getSpellCooldownFactor() {
        return data().factors().spellCooldown();
    }

    @Override
    public void setSpellCooldownFactor(double spellCooldownFactor) {
        updateFactors(f -> f.withSpellCooldown(spellCooldownFactor));
    }

    @Override
    public double getSpellRangeFactor() {
        return data().factors().spellRange();
    }

    @Override
    public void setSpellRangeFactor(double spellRangeFactor) {
        updateFactors(f -> f.withSpellRange(spellRangeFactor));
    }
}
