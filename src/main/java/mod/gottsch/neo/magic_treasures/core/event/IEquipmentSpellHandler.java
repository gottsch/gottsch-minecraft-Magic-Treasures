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
package mod.gottsch.neo.magic_treasures.core.event;

import mod.gottsch.neo.magic_treasures.core.spell.SpellContext;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 *
 *
 */
public interface IEquipmentSpellHandler {

	public List<SpellContext> handleEquipmentSpells(Event event, ServerPlayer player);

	/**
	 * Whether the player is wearing jewelry in every main jewelry slot (ring, necklace and bracelet) at once.
	 * Only equipment mods with those slots can answer yes.
	 */
	default boolean isWearingFullSet(ServerPlayer player) {
		return false;
	}

	/** Mana wells the player is wearing in this equipment mod's slots. */
	default List<ItemStack> getManaWells(ServerPlayer player) {
		return List.of();
	}
}
