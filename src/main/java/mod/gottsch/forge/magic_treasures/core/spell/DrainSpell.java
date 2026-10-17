/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.magic_treasures.core.spell;

import com.google.common.util.concurrent.AtomicDouble;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.network.SpellUpdateS2C;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import mod.gottsch.forge.magic_treasures.core.util.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEvent. LivingTickEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

/**
 * 
 * @author Mark Gottschling on Dec 28, 2020
 *
 */
public class DrainSpell extends Spell {
	public static final String DRAIN_TYPE = "drain";
	private static final Class<?> REGISTERED_EVENT = LivingTickEvent.class;

	/**
	 *
	 * @param builder
	 */
	DrainSpell(Builder builder) {
		super(builder);
	}

	@Override
	public boolean clientUpdate(ItemStack jewelry, SpellEntity entity, SpellUpdateS2C message) {
		// TODO
		return super.clientUpdate(jewelry, entity, message);
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
		ItemStack jewelry = context.getJewelry();
		Player player = context.getPlayer();
		IJewelryHandler handler = jewelry.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);
		if (level.getGameTime() % handler.modifyFrequency(getFrequency()) == 0) {
			if (handler.getMana() > 0 && player.getHealth() < player.getMaxHealth() && player.isAlive()) {
				// get player position
				double px = player.getX();
				double py = player.getY();
				double pz = player.getZ();

				// calculate the new amount
				double range = getRange();
				AtomicDouble drainedHealth = new AtomicDouble(0);
				List<Monster> mobs = level.getEntitiesOfClass(Monster.class, new AABB(px - range, py - range, pz - range, px + range, py + range, pz + range));
				if (mobs.isEmpty()) {
					return result;
				}
				double effectAmount = handler.modifyEffectAmount(getEffectAmount());
				mobs.forEach(mob -> {
//					boolean flag = mob.attackEntityFrom(DamageSource.GENERIC, (float)getAmount());
					boolean flag = mob.hurt(level.damageSources().generic(), (float)effectAmount);
					MagicTreasures.LOGGER.debug("health drained from mob -> {} was successful -> {}", mob.getName(), flag);
					if (flag) {
						drainedHealth.addAndGet(effectAmount);
					}
				});

				if (drainedHealth.get() > 0.0) {
					player.setHealth(Mth.clamp(player.getHealth() + (float)drainedHealth.get(), 0.0F, player.getMaxHealth()));
					//					entity.setMana(MathHelper.clamp(entity.getMana() - 1D,  0D, entity.getMana()));
					applyCost(level, random, coords, context, handler.modifySpellCost(getSpellCost()));
					result = true;
				}                
			}

		}
		return result;
	}

	@Override
	public Component getSpellDesc() {
		return Component.translatable(LangUtil.tooltip("spell.drain.rate"),
				MathUtil.r1d(getEffectAmount()),
				MathUtil.r1d(getRange()),
				MathUtil.r1d(getFrequency()/20.0));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		return Component.translatable(LangUtil.tooltip("spell.drain.rate"),
				MathUtil.r1d(modifyEffectAmount(jewelry)),
				MathUtil.r1d(modifyRange(jewelry)),
				MathUtil.r1d(modifyFrequency(jewelry)/20.0));
	}

	@Override
	public ChatFormatting getSpellLabelColor() {
		return ChatFormatting.DARK_BLUE;
	}

	/*
	 * 
	 */
	public static class Builder extends Spell.Builder {

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, DRAIN_TYPE, level, rarity);
		}

		@Override
		public ISpell build() {
			return  new DrainSpell(this);
		}
	}
}
