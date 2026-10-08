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
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterial;
import mod.gottsch.neo.magic_treasures.core.loot.MagicTreasuresLootFunctions;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.neo.magic_treasures.core.registry.JewelryMaterialRegistry;
import mod.gottsch.neo.magic_treasures.core.registry.JewelryRegistry;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 *
 * @author Mark Gottschling on May 22, 2024
 *
 */
public class RandomJewelry extends LootItemConditionalFunction {
	//	private static final ResourceLocation LOCATION = ResourceLocation.parse("gealdorcraft:random_gemstone");
	private static final String RARITY = "rarity";
	private static final String RARITIES = "rarities";
	private static final String GEMSTONES = "gemstones";
	private static final String LEVELS = "levels";
	private static final String MATERIALS = "materials";

	private final IRarity rarity;
	private final List<IRarity> rarities;
	private final List<ResourceLocation> gemstones;
	private final List<ResourceLocation> materials;
	private final NumberProvider levels;

	public RandomJewelry(List<LootItemCondition> conditions, IRarity rarity, List<IRarity> rarities,
						 List<ResourceLocation> materials, List<ResourceLocation> gemstones, NumberProvider levels) {
		super(conditions);
		this.rarity = rarity;
		this.rarities = rarities;
		this.materials = materials;
		this.gemstones = gemstones;
		this.levels = levels;
	}

	@Override
	public LootItemFunctionType<RandomJewelry> getType() {
		return MagicTreasuresLootFunctions.RANDOM_JEWELRY.get();
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		Random random = new Random();
		Optional<Item> stone = Optional.empty();

		MagicTreasures.LOGGER.debug("incoming stack -> {}", stack.getDisplayName());
		MagicTreasures.LOGGER.debug("rarity -> {}", rarity);
		MagicTreasures.LOGGER.debug("rarities -> {}", rarities);

		List<Item> jewelry = new ArrayList<>();
		if (!materials.isEmpty()) {
			List<Item> finalJewelry = jewelry;
			for(ResourceLocation material : materials) {
				JewelryMaterialRegistry.get(material)
						.map(JewelryRegistry::get).ifPresent(jewelry::addAll);
			}

			// filter by rarity/rarities
			if (rarity != MagicTreasuresRarity.NONE) {
				jewelry = filterByRarity(jewelry, rarity);
				jewelry = filterByGemstones(jewelry, gemstones);
				// NOTE do not filter by level as rarity supersedes it

			} else if (!rarities.isEmpty()) {
				List<Item> filteredJewelry = new ArrayList<>();
				for(IRarity rarity : rarities) {
					filteredJewelry.addAll(filterByRarity(jewelry, rarity));
				}
				jewelry = filterByGemstones(filteredJewelry, gemstones);

			} else if (levels != null) {
				jewelry = filterByLevel(jewelry, levels.getInt(context));
				jewelry = filterByGemstones(jewelry, gemstones);
			}
		} else if (this.rarity != MagicTreasuresRarity.NONE) {
			// get by rarity
			jewelry.addAll(JewelryRegistry.get(rarity));
			jewelry = filterByGemstones(jewelry, gemstones);
		} else if (!this.rarities.isEmpty()) {
			MagicTreasures.LOGGER.debug("adding jewelry by rarities");
			for (IRarity rarity : rarities) {
				jewelry.addAll(JewelryRegistry.get(rarity));
				MagicTreasures.LOGGER.debug("jewelry list ->{}", jewelry);
			}
			jewelry = filterByGemstones(jewelry, gemstones);
			MagicTreasures.LOGGER.debug("filtered jewelry list ->{}", jewelry);
		} else if (levels != null) {
			jewelry = filterByLevel(jewelry, levels.getInt(context));
			jewelry = filterByGemstones(jewelry, gemstones);
		}

		return jewelry.isEmpty() ? stack : new ItemStack(jewelry.get(random.nextInt(jewelry.size())));
	}

	public List<Item> filterByRarity(List<Item> jewelry, IRarity rarity) {
		return jewelry.stream()
				.filter(j -> {
					return JewelryRegistry.get(rarity).contains(j);
				}).toList();
	}

	public List<Item> filterByLevel(List<Item> jewelry, int level) {
		return jewelry.stream()
				.filter(j -> {
					ItemStack jewelryStack = new ItemStack(j);
					return JewelryHandler.get(jewelryStack)
							.map(jj -> jj.getMaxLevel() >= level).orElse(false);
				}).toList();
	}

	public List<Item> filterByGemstones(List<Item> jewelry, List<ResourceLocation> gemstones) {
		if (!gemstones.isEmpty()) {
			// filter by stones
			return jewelry.stream()
					.filter(j -> {
						ItemStack jewelryStack = new ItemStack(j);
						return JewelryHandler.get(jewelryStack)
								.map(jj -> gemstones.contains(jj.getStone())).orElse(false);
					}).toList();
		}
		return jewelry;
	}

	public Optional<Item> selectStone(List<Item> stones) {
		if (!stones.isEmpty()) {
			return Optional.ofNullable(stones.get(new Random().nextInt(stones.size())));
		}
		return Optional.empty();
	}

	public static final MapCodec<RandomJewelry> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
			.and(Codec.STRING.optionalFieldOf(RARITY, "").forGetter(f -> f.rarity.getName()))
			.and(Codec.STRING.listOf().optionalFieldOf(RARITIES, List.of()).forGetter(f -> LootFunctionHelper.rarityNames(f.rarities)))
			.and(Codec.STRING.listOf().optionalFieldOf(MATERIALS, List.of()).forGetter(f -> LootFunctionHelper.names(f.materials)))
			.and(Codec.STRING.listOf().optionalFieldOf(GEMSTONES, List.of()).forGetter(f -> LootFunctionHelper.names(f.gemstones)))
			.and(NumberProviders.CODEC.optionalFieldOf(LEVELS).forGetter(f -> Optional.ofNullable(f.levels)))
			.apply(instance, (conditions, rarity, rarities, materials, gemstones, levels) -> new RandomJewelry(conditions,
					LootFunctionHelper.rarity(rarity, MagicTreasuresRarity.NONE), LootFunctionHelper.rarities(rarities),
					LootFunctionHelper.materials(materials), LootFunctionHelper.locations(gemstones), levels.orElse(null))));
}
