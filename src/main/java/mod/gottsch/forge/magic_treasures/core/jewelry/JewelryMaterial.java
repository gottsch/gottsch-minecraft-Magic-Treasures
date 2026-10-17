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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Mark Gottschling May 8, 2024
 *
 */
public class JewelryMaterial {
    private final ResourceLocation name;
    private final int uses;
    private final int repairs;
    private final int recharges;
    private final int mana;
    private final int maxLevel;
    private final int spells;
    // TODO this still doesn't help with Castle Rings only having Ruby/Sapphire.
    /*
     * TODO could abstract affixer out to a registry and reference it here.
     *  Also have an affixer reference in JeweleryHandler which would override
     *  this one if provided, like manMana, etc does.
     *  Is a registry overkill? as there might only be 1 jewelry set that would use it
     *  ex. Castle Rings. Could simply have the JewelryHandler have an affixer that
     *  overrides. Don't need to save the affixer as it is part of the initCap() and is
     *  recreated on item creation. (its a Predicate - there is no state).
     *  Another option is to make a special material(s) called "silver_castle",
     *  which then could have an affixer. this however, could lead to many
     *  special materials - "gold_castle" etc.
     *  Another option is to make a subType for "castle", "signet", "amulet" etc
     *  and all categories have an acceptsAffixer and they would all be evaluated
     *  when affixing a stone.
     */
    public final Predicate<ItemStack> acceptsAffixer;

    private double spellCostFactor;
    private double spellEffectAmountFactor;
    private double spellFrequencyFactor;
    private double spellDurationFactor;
    private double spellCooldownFactor;
    private double spellRangeFactor;

    @Deprecated
    public JewelryMaterial(ResourceLocation name, int uses, int repairs, int maxLevel, int mana, int recharges, int spells, Predicate<ItemStack> acceptsAffixer) {
        this.name = name;
        this.uses = uses;
        this.repairs = repairs;
        this.maxLevel = maxLevel;
        this.mana = mana;
        this.recharges = recharges;
        this.spells = spells;
        this.acceptsAffixer = acceptsAffixer;
    }
    
    public JewelryMaterial(Builder builder) {
        this.name = builder.name;
        this.uses = builder.uses;
        this.repairs = builder.repairs;
        this.maxLevel = builder.maxLevel;
        this.mana = builder.mana;
        this.recharges = builder.recharges;
        this.spells = builder.spells;
        this.acceptsAffixer = builder.acceptsAffixer;

        // spell factors
        this.spellCooldownFactor = builder.spellCooldownFactor;
        this.spellCostFactor = builder.spellCostFactor;
        this.spellDurationFactor = builder.spellDurationFactor;
        this.spellEffectAmountFactor = builder.spellEffectAmountFactor;
        this.spellFrequencyFactor = builder.spellFrequencyFactor;
        this.spellRangeFactor = builder.spellRangeFactor;
   }

   public String getName() {
        return name.getPath();
   }
   public ResourceLocation getRegistryName() { return name;}

    public static class Builder {
        public final ResourceLocation name;
        public int uses;
        public int repairs = 0;
        public int maxLevel = 1;
        public final int mana;
        public int recharges = 0;
        public int spells = 1;
        public Predicate<ItemStack> acceptsAffixer = p -> true;

        public double spellCostFactor = 1.0;
        public double spellEffectAmountFactor = 1.0;
        public double spellFrequencyFactor = 1.0;
        public double spellDurationFactor = 1.0;
        public double spellCooldownFactor = 1.0;
        public double spellRangeFactor = 1.0;

        public Builder(String name, int uses, int mana) {
            this.name = ModUtil.asLocation(name);
            this.uses = uses;
            this.mana = mana;
        }

        public Builder(ResourceLocation name, int uses, int mana) {
            this.name = name;
            this.uses = uses;
            this.mana = mana;
        }

        public Builder with(Consumer<Builder> builder) {
            builder.accept(this);
            return this;
        }

        public Builder setInfinite() {
            this.uses = Integer.MAX_VALUE;
            return this;
        }

        public JewelryMaterial build() {
            return new JewelryMaterial(this);
        }
    }

    public ResourceLocation getId() {
        return name;
    }

    public int getUses() {
        return uses;
    }

    public int getRepairs() {
        return repairs;
    }

    public int getRecharges() {
        return recharges;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getSpells() {
        return spells;
    }

    public Predicate<ItemStack> getAcceptsAffixer() {
        return acceptsAffixer;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JewelryMaterial that = (JewelryMaterial) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "JewelryMaterial{" +
                "name=" + name +
                ", uses=" + uses +
                ", repairs=" + repairs +
                ", recharges=" + recharges +
                ", mana=" + mana +
                ", maxLevel=" + maxLevel +
                ", spells=" + spells +
                ", acceptsAffixer=" + acceptsAffixer +
                '}';
    }
}
