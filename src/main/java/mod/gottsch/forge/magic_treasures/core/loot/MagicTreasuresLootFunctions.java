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

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.loot.function.ImbueRandomly;
import mod.gottsch.forge.magic_treasures.core.loot.function.RandomGemstone;
import mod.gottsch.forge.magic_treasures.core.loot.function.RandomJewelry;
import mod.gottsch.forge.magic_treasures.core.loot.function.RandomSpell;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 
 * @author Mark Gottschling on May 21, 2024
 *
 */
public class MagicTreasuresLootFunctions {
	public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS =
			DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MagicTreasures.MOD_ID);

	public static final RegistryObject<LootItemFunctionType> RANDOM_GEMSTONE =
			LOOT_FUNCTIONS.register("random_gemstone", () -> new LootItemFunctionType(new RandomGemstone.Serializer()));
	public static final RegistryObject<LootItemFunctionType> RANDOM_JEWELRY =
			LOOT_FUNCTIONS.register("random_jewelry", () -> new LootItemFunctionType(new RandomJewelry.Serializer()));
	public static final RegistryObject<LootItemFunctionType> RANDOM_SPELL =
			LOOT_FUNCTIONS.register("random_spell", () -> new LootItemFunctionType(new RandomSpell.Serializer()));
	public static final RegistryObject<LootItemFunctionType> IMBUE_RANDOMLY =
			LOOT_FUNCTIONS.register("imbue_randomly", () -> new LootItemFunctionType(new ImbueRandomly.Serializer()));

	public static void register(IEventBus bus) {
		LOOT_FUNCTIONS.register(bus);
	}
}
