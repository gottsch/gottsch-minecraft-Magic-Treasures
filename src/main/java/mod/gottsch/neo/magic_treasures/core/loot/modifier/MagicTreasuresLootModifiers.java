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

import com.mojang.serialization.MapCodec;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 *
 * @author Mark Gottschling on Jul 9, 2024
 *
 */
public class MagicTreasuresLootModifiers {
	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
			DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MagicTreasures.MOD_ID);

	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<LootModifierByRarity>> MAGIC_TREASURES_LOOT_MODIFIER =
			LOOT_MODIFIER_SERIALIZERS.register("default", LootModifierByRarity.CODEC);

	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<LootModifierByLootTable>> MAGIC_TREASURES_LOOT_MODIFIER_BY_LOOT_TABLE =
			LOOT_MODIFIER_SERIALIZERS.register("loot_modifier_by_loot_table", LootModifierByLootTable.CODEC);
}
