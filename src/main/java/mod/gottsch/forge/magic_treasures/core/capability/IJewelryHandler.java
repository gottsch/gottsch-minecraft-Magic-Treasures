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
package mod.gottsch.forge.magic_treasures.core.capability;

import mod.gottsch.forge.magic_treasures.core.item.IJewelrySizeTier;
import mod.gottsch.forge.magic_treasures.core.item.IJewelryType;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryMaterial;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryStoneTier;
import mod.gottsch.forge.magic_treasures.core.spell.SpellEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Created by Mark Gottschling on 6/1/2023
 */
public interface IJewelryHandler {
    JewelryMaterial getMaterial();

    public IJewelrySizeTier getJewelrySizeTier();
    public IJewelryType getJewelryType();

    void appendSpecialHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag);

    double modifySpellCost(double cost);

    double getSpellCostFactor();

    double modifyEffectAmount(double amount);

    int modifyDuration(int duration);

    long modifyCooldown(long cooldown);

    long modifyFrequency(long frequency);

    double modifyRange(double range);

    public Tag serializeNBT();
    public void deserializeNBT(Tag tag);

    int getMaxUses();

    void setMaxUses(int maxUses);

    public int getUses();
    public void setUses(int uses);

    public double getMaxMana();
    public void setMaxMana(double maxMana);

    public double getMana();
    public void setMana(double mana);

    public int getMaxRepairs();
    public void setMaxRepairs(int repairs);

    public int getRepairs();
    public void setRepairs(int repairs);

    public int getMaxLevel();

    // TODO maxStones

    void setMaxLevel(int maxLevel);

    ResourceLocation getStone();

    void setStone(ResourceLocation stone);

    boolean hasStone();

    List<SpellEntity> getSpells();

    void setSpells(List<SpellEntity> spells);

    void setInfinite();

    boolean isInfinite();

    // convenience method
    JewelryStoneTier getStoneTier();

    boolean isUpgradable();

    void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag);

    int getRecharges();

    void setRecharges(int recharges);

    int getMaxRecharges();

    void setMaxRecharges(int maxRecharges);

    String getBaseName();

    void setBaseName(String baseName);

    boolean acceptsAffixer(ItemStack stack);

    double getSpellRangeFactor();

    void setSpellRangeFactor(double spellRangeFactor);

    double getSpellCooldownFactor();

    void setSpellCooldownFactor(double spellCooldownFactor);

    double getSpellDurationFactor();

    void setSpellDurationFactor(double spellDurationFactor);

    double getSpellFrequencyFactor();

    void setSpellFrequencyFactor(double spellFrequencyFactor);

    double getSpellEffectAmountFactor();

    void setSpellEffectAmountFactor(double spellEffectAmountFactor);

    void setSpellCostFactor(double spellCostFactor);
}
