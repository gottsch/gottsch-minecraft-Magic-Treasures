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
package mod.gottsch.forge.magic_treasures.core.loot;

import mod.gottsch.forge.magic_treasures.core.loot.function.ImbueRandomly;
import mod.gottsch.forge.magic_treasures.core.loot.function.RandomGemstone;
import mod.gottsch.forge.magic_treasures.core.loot.function.RandomJewelry;
import mod.gottsch.forge.magic_treasures.core.loot.function.RandomSpell;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

/**
 * 
 * @author Mark Gottschling on May 21, 2024
 *
 */
public class MagicTreasuresLootFunctions {

	public static LootItemFunctionType RANDOM_GEMSTONE;
	public static LootItemFunctionType RANDOM_JEWELRY;
	public static LootItemFunctionType RANDOM_SPELL;
	public static LootItemFunctionType IMBUE_RANDOMLY;

	/**
	 * 
	 */
	public static void register() {
		RANDOM_GEMSTONE = register("random_gemstone", new LootItemFunctionType(new RandomGemstone.Serializer()));
		RANDOM_JEWELRY = register("random_jewelry", new LootItemFunctionType(new RandomJewelry.Serializer()));
		RANDOM_SPELL = register("random_spell", new LootItemFunctionType(new RandomSpell.Serializer()));
		IMBUE_RANDOMLY = register("imbue_randomly", new LootItemFunctionType(new ImbueRandomly.Serializer()));
	}
	
	/**
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public static LootItemFunctionType register(String name, LootItemFunctionType type) {
		return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, ModUtil.asLocation(name), type);
	}
}
