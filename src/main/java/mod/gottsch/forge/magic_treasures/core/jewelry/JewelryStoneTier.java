/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2024 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.magic_treasures.core.jewelry;

import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Mark Gottschling May 9, 2024
 *
 */
public class JewelryStoneTier {
    private final ResourceLocation name;
    private final int mana;
    private final int recharges;
    private final Predicate<ItemStack> canAffix;

    private double spellCostFactor;
    private double spellEffectAmountFactor;
    private double spellFrequencyFactor;
    private double spellDurationFactor;
    private double spellCooldownFactor;
    private double spellRangeFactor;

    @Deprecated
    public JewelryStoneTier(ResourceLocation name, int mana, int recharges, Predicate<ItemStack> canAffix) {
        this.name = name;
        this.mana = mana;
        this.recharges = recharges;
        this.canAffix = canAffix;
    }

    public JewelryStoneTier(Builder builder) {
        this.name = builder.name;
        this.mana = builder.mana;
        this.recharges = builder.recharges;
        this.canAffix = builder.canAffix;

        // spell factors
        this.spellCooldownFactor = builder.spellCooldownFactor;
        this.spellCostFactor = builder.spellCostFactor;
        this.spellDurationFactor = builder.spellDurationFactor;
        this.spellEffectAmountFactor = builder.spellEffectAmountFactor;
        this.spellFrequencyFactor = builder.spellFrequencyFactor;
        this.spellRangeFactor = builder.spellRangeFactor;
    }

    /**
     *
     * @param stack
     * @return
     */
    public boolean canAffix(ItemStack stack) {
        return canAffix.test(stack);
    }

    public static class Builder {
        private final ResourceLocation name;
        private final int mana;
        private final int recharges;
        public Predicate<ItemStack> canAffix = p -> true;;

        public double spellCostFactor = 1.0;
        public double spellEffectAmountFactor = 1.0;
        public double spellFrequencyFactor = 1.0;
        public double spellDurationFactor = 1.0;
        public double spellCooldownFactor = 1.0;
        public double spellRangeFactor = 1.0;

        public Builder(String name, int mana, int recharges) {
            this(ModUtil.asLocation(name), mana, recharges);
        }

        public Builder(ResourceLocation name, int mana, int recharges) {
            this.name = name;
            this.mana = mana;
            this.recharges = recharges;
        }

        public Builder with(Consumer<Builder> builder) {
            builder.accept(this);
            return this;
        }

        public JewelryStoneTier build() {
            return new JewelryStoneTier(this);
        }
    }

    public ResourceLocation getName() {
        return name;
    }

    public int getMana() {
        return mana;
    }

    public int getRecharges() {
        return recharges;
    }

    public Predicate<ItemStack> getAcceptsAffixer() {
        return canAffix;
    }

    public double getSpellCostFactor() {
        return spellCostFactor;
    }

    public void setSpellCostFactor(double spellCostFactor) {
        this.spellCostFactor = spellCostFactor;
    }

    public double getSpellEffectAmountFactor() {
        return spellEffectAmountFactor;
    }

    public void setSpellEffectAmountFactor(double spellEffectAmountFactor) {
        this.spellEffectAmountFactor = spellEffectAmountFactor;
    }

    public double getSpellFrequencyFactor() {
        return spellFrequencyFactor;
    }

    public void setSpellFrequencyFactor(double spellFrequencyFactor) {
        this.spellFrequencyFactor = spellFrequencyFactor;
    }

    public double getSpellDurationFactor() {
        return spellDurationFactor;
    }

    public void setSpellDurationFactor(double spellDurationFactor) {
        this.spellDurationFactor = spellDurationFactor;
    }

    public double getSpellCooldownFactor() {
        return spellCooldownFactor;
    }

    public void setSpellCooldownFactor(double spellCooldownFactor) {
        this.spellCooldownFactor = spellCooldownFactor;
    }

    public double getSpellRangeFactor() {
        return spellRangeFactor;
    }

    public void setSpellRangeFactor(double spellRangeFactor) {
        this.spellRangeFactor = spellRangeFactor;
    }

    @Override
    public String toString() {
        return "JewelryStoneTier{" +
                "name=" + name +
                ", mana=" + mana +
                ", recharges=" + recharges +
                ", canAffix=" + canAffix +
                '}';
    }
}
