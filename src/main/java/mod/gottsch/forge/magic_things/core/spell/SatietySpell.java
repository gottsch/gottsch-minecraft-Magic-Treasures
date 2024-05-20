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
package mod.gottsch.forge.magic_things.core.spell;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.magic_things.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_things.core.capability.MagicThingsCapabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 *
 * @author Mark Gottschling on May 2, 2020
 *
 */
public class SatietySpell extends Spell {
	public static final int MAX_FOOD_LEVEL = 20;
	public static final String SATIETY_TYPE = "satiety";

	private static final Class<?> REGISTERED_EVENT = LivingUpdateEvent.class;

	/**
	 *
	 * @param builder
	 */
	SatietySpell(Builder builder) {
		super(builder);
	}

	@Override
	public Class<?> getRegisteredEvent() {
		return REGISTERED_EVENT;
	}

	@Override
	public boolean serverUpdate(Level world, Random random, ICoords coords, Event event, ICastSpellContext context) {
		boolean result = false;
		ItemStack jewelry = context.getJewelry();
		Player player = context.getPlayer();
		IJewelryHandler handler = jewelry.getCapability(MagicThingsCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);

		if (world.getGameTime() % modifyFrequency(jewelry) == 0) {
			if (player.isAlive() && handler.getMana() > 0 && player.getFoodData().getFoodLevel() < MAX_FOOD_LEVEL) {
				double amount = modifyEffectAmount(jewelry);
				player.getFoodData().eat((int)amount, (float) amount);
				applyCost(world, random, coords, context, modifySpellCost(jewelry));
				result = true;
			}
		}
		return result;
	}

	@SuppressWarnings("deprecation")
//	@Override
//	public Component getCharmDesc(SpellEntity entity) {
//		return new TranslatableComponent("tooltip.charm.rate.satiety", (int)(getFrequency()/TICKS_PER_SECOND));
//	}

	public static class Builder extends Spell.Builder {

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, SATIETY_TYPE, level, rarity);
		}

		@Override
		public Spell build() {
			return  new SatietySpell(this);
		}
	}
}
