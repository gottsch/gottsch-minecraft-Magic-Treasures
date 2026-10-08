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
package mod.gottsch.neo.magic_treasures.core.loot.function;

import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import com.google.gson.*;
import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.loot.MagicTreasuresLootFunctions;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.neo.magic_treasures.core.registry.StoneRegistry;
import mod.gottsch.neo.magic_treasures.core.spell.ISpell;
import mod.gottsch.neo.magic_treasures.core.spell.MagicTreasuresSpells;
import mod.gottsch.neo.magic_treasures.core.spell.SpellRegistry;
import mod.gottsch.neo.magic_treasures.core.util.ModUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 *
 * @author Mark Gottschling on May 21, 2024
 *
 */
public class RandomGemstone extends LootItemConditionalFunction {
//	private static final ResourceLocation LOCATION = ResourceLocation.parse("gealdorcraft:random_gemstone");
	private static final String RARITY = "rarity";
	private static final String RARITIES = "rarities";
	private static final String GEMSTONES = "gemstones";
	private static final String COUNT = "count";

	private final NumberProvider count;
	private final IRarity rarity;
	private final List<IRarity> rarities;
	private final List<ResourceLocation> gemstones;


	/**
	 *
	 * @param conditions
	 * @param count
	 * @param rarity
	 */
	public RandomGemstone(List<LootItemCondition> conditions, NumberProvider count,
						  IRarity rarity, List<IRarity> rarities, List<ResourceLocation> gemstones) {
		super(conditions);
		this.count = count;
		this.rarity = rarity;
		this.rarities = rarities;
		this.gemstones = gemstones;
	}

	@Override
	public LootItemFunctionType<RandomGemstone> getType() {
		return MagicTreasuresLootFunctions.RANDOM_GEMSTONE.get();
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		Random random = new Random();
		Optional<Item> stone = Optional.empty();

		MagicTreasures.LOGGER.debug("incoming stack -> {}", stack.getDisplayName());
		MagicTreasures.LOGGER.debug("rarity -> {}", rarity);

		List<Item> stones = new ArrayList<>();
		if (rarity != null) {
			// get all the gemstones by rarity
			stones.addAll(StoneRegistry.get(rarity));
			stone = selectStone(stones);
		} else if (!rarities.isEmpty()) {
			rarities.forEach(rarity -> {
				stones.addAll(StoneRegistry.get(rarity));
			});
			stone = selectStone(stones);
		} else if (!gemstones.isEmpty()) {
			stone = Optional.ofNullable(BuiltInRegistries.ITEM.get(gemstones.get(random.nextInt(gemstones.size()))));
		}

		// select random count
		int count = this.count == null ? 1 : this.count.getInt(context);
		MagicTreasures.LOGGER.debug("selected count -> {}", count);

        return stone.map(s -> new ItemStack(s, count)).orElse(stack);
    }

	public Optional<Item> selectStone(List<Item> stones) {
		if (!stones.isEmpty()) {
			return Optional.ofNullable(stones.get(new Random().nextInt(stones.size())));
		}
		return Optional.empty();
	}

	public static final MapCodec<RandomGemstone> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
			.and(NumberProviders.CODEC.optionalFieldOf(COUNT).forGetter(f -> Optional.ofNullable(f.count)))
			.and(Codec.STRING.optionalFieldOf(RARITY, "").forGetter(f -> f.rarity.getName()))
			.and(Codec.STRING.listOf().optionalFieldOf(RARITIES, List.of()).forGetter(f -> LootFunctionHelper.rarityNames(f.rarities)))
			.and(Codec.STRING.listOf().optionalFieldOf(GEMSTONES, List.of()).forGetter(f -> LootFunctionHelper.names(f.gemstones)))
			.apply(instance, (conditions, count, rarity, rarities, gemstones) -> new RandomGemstone(conditions, count.orElse(null),
					LootFunctionHelper.rarity(rarity, MagicTreasuresRarity.COMMON), LootFunctionHelper.rarities(rarities),
					LootFunctionHelper.locations(gemstones))));
}
