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
package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.network.SpellUpdateS2C;
import mod.gottsch.forge.magic_treasures.core.spell.cost.ICostEvaluator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

/**
 * Created by Mark Gottschling on 5/3/2023
 */
public interface ISpell {

    SpellEntity entity();

    boolean serverUpdate(Level level, Random random, ICoords coords, Event event, ICastSpellContext context);

    default public boolean clientUpdate(ItemStack jewelry, SpellEntity entity, SpellUpdateS2C message) {
        IJewelryHandler handler = jewelry.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);
        handler.setMana(message.getMana());
        return true;
    }

    @SuppressWarnings("deprecation")
    void addInformation(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn, SpellEntity entity);

    Component getSpellDesc();

    Component getSpellDesc(ItemStack jewelry);

    ChatFormatting getSpellLabelColor();

    ChatFormatting getSpellDescColor();

    // final properties
    public ResourceLocation getName();
    public String getType();
    public int getLevel();
    public IRarity getRarity();

    // mutator properties
    double getSpellCost();
    void setSpellCost(double spellCost);

    int getDuration();
    void setDuration(int duration);

    long getFrequency();
    void setFrequency(long frequency);

    double getRange();
    void setRange(double range);

    long getCooldown();
    void setCooldown(long cooldown);

    double getEffectAmount();
    void setEffectAmount(double amount);

    ICostEvaluator getCostEvaluator();
    void setCostEvaluator(ICostEvaluator costEvaluator);

    boolean isExclusive();
    void setExclusive(boolean exclusive);

    public Class<?> getRegisteredEvent();

    boolean isEffectStackable();

    void setEffectStackable(boolean effectStackable);

    int getPriority();

    void setPriority(int priority);
}
