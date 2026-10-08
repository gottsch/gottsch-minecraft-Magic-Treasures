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
package mod.gottsch.neo.magic_treasures.core.loot.modifier;

import com.mojang.serialization.Codec;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 
 * @author Mark Gottschling on Jul 9, 2024
 *
 */
public class MagicTreasuresLootModifiers {
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = 
			DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MagicTreasures.MOD_ID);
	
	public static final RegistryObject<Codec<? extends IGlobalLootModifier>> MAGIC_TREASUREs_LOOT_MODIFIER =
			LOOT_MODIFIER_SERIALIZERS.register("default", LootModifierByRarity.CODEC);

	public static final RegistryObject<Codec<? extends IGlobalLootModifier>> MAGIC_TREASURES_LOOT_MODIFIER_BY_LOOT_TABLE =
			LOOT_MODIFIER_SERIALIZERS.register("loot_modifier_by_loot_table", LootModifierByLootTable.CODEC);

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		LOOT_MODIFIER_SERIALIZERS.register(bus);
	}
}
