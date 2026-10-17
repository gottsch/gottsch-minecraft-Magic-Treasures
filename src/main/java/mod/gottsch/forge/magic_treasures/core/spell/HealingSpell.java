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
package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import mod.gottsch.forge.magic_treasures.core.util.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 *
 */
public class HealingSpell extends Spell {
    public static String HEALING_TYPE = "healing";
    private static final Class<?> REGISTERED_EVENT = LivingEvent.LivingTickEvent.class;

    /**
     *
     * @param builder
     */
    public HealingSpell(Builder builder) {
        super(builder);
    }

    public static class Builder extends Spell.Builder {
        public Builder(ResourceLocation name, int level, IRarity rarity) {
            super(name, HEALING_TYPE, level, rarity);
        }

        @Override
        public ISpell build() {
            return new HealingSpell(this);
        }
    }

    @Override
    public Class<?> getRegisteredEvent() {
        return REGISTERED_EVENT;
    }

    /**
     * NOTE: it is assumed that only the allowable events are calling this action.
     */

    @Override
    public boolean serverUpdate(Level level, Random random, ICoords coords, Event event, ICastSpellContext context) {
        boolean result = false;
        IJewelryHandler handler = context.getJewelry().getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);
        if (level.getGameTime() % handler.modifyFrequency(getFrequency()) == 0) {
            if (handler.getMana() > 0 && context.getPlayer().getHealth() < context.getPlayer().getMaxHealth() && context.getPlayer().isAlive()) {

                // determine the actual amount of health (0.0 -> getAmount())
                float amount = Math.min((float)handler.modifyEffectAmount(getEffectAmount()), context.getPlayer().getMaxHealth() - context.getPlayer().getHealth());
                context.getPlayer().setHealth(Mth.clamp(context.getPlayer().getHealth() + amount, 0.0F, context.getPlayer().getMaxHealth()));
                applyCost(level, random, coords, context, handler.modifySpellCost(getSpellCost()));
                result = true;
            }
        }
        return result;
    }

    @Override
    public Component getSpellDesc() {
        return Component.translatable(LangUtil.tooltip("spell.healing.rate"),
                MathUtil.r1d(getEffectAmount()),
                MathUtil.r1d(getFrequency()/20.0));
    }

    @Override
    public Component getSpellDesc(ItemStack jewelry) {
        return Component.translatable(LangUtil.tooltip("spell.healing.rate"),
                MathUtil.r1d(modifyEffectAmount(jewelry)),
                MathUtil.r1d(modifyFrequency(jewelry)/20.0));
    }

    @Override
    public ChatFormatting getSpellLabelColor() {
        return ChatFormatting.DARK_RED;
    }

}
