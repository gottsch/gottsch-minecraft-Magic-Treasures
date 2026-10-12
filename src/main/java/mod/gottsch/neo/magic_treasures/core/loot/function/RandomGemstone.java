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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.GemstoneDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryLoot;
import mod.gottsch.neo.magic_treasures.core.loot.MagicTreasuresLootFunctions;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Replaces the stack with a random gem from the {@code gemstone} registry. JSON: {@code rarity} or {@code rarities}
 * (each falls back to the nearest lower rarity with gems, #14.9), or {@code gemstones} (gemstone ids or #tags; bare
 * names default to magictreasures), and {@code count}. With none of them, any gem with a rarity.
 * <p>
 * 1.x defaulted the rarity to common, so a {@code gemstones} list was never read and every such entry gave topaz.
 * The default is now no rarity, and the list is used.
 *
 * @author Mark Gottschling on May 21, 2024
 */
public class RandomGemstone extends LootItemConditionalFunction {
	private static final String RARITY = "rarity";
	private static final String RARITIES = "rarities";
	private static final String GEMSTONES = "gemstones";
	private static final String COUNT = "count";

	private final NumberProvider count;
	private final IRarity rarity;
	private final List<IRarity> rarities;
	private final List<String> gemstones;

	public RandomGemstone(List<LootItemCondition> conditions, NumberProvider count,
						  IRarity rarity, List<IRarity> rarities, List<String> gemstones) {
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
		List<Holder.Reference<GemstoneDef>> gems;
		if (rarity != MagicTreasuresRarity.NONE) {
			gems = JewelryLoot.gemstones(rarity, gem -> true);
		} else if (!rarities.isEmpty()) {
			Set<Holder.Reference<GemstoneDef>> union = new LinkedHashSet<>();
			rarities.forEach(r -> union.addAll(JewelryLoot.gemstones(r, gem -> true)));
			gems = List.copyOf(union);
		} else if (!gemstones.isEmpty()) {
			// named gems needn't have a rarity
			gems = JewelryDataIndex.gemstones().values().stream()
					.filter(LootFunctionHelper.holders(MagicTreasuresRegistries.GEMSTONE, gemstones)::test)
					.toList();
		} else {
			gems = JewelryLoot.gemstones();
		}
		if (gems.isEmpty()) {
			MagicTreasures.LOGGER.debug("random_gemstone: nothing matches rarity {}, rarities {}, gemstones {}", rarity, rarities, gemstones);
			return stack;
		}
		Holder.Reference<GemstoneDef> gem = gems.get(context.getRandom().nextInt(gems.size()));
		int count = this.count == null ? 1 : this.count.getInt(context);
		return new ItemStack(JewelryLoot.item(gem), count);
	}

	public static final MapCodec<RandomGemstone> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
			.and(NumberProviders.CODEC.optionalFieldOf(COUNT).forGetter(f -> Optional.ofNullable(f.count)))
			.and(Codec.STRING.optionalFieldOf(RARITY, "").forGetter(f -> f.rarity == MagicTreasuresRarity.NONE ? "" : f.rarity.getName()))
			.and(Codec.STRING.listOf().optionalFieldOf(RARITIES, List.of()).forGetter(f -> LootFunctionHelper.rarityNames(f.rarities)))
			.and(Codec.STRING.listOf().optionalFieldOf(GEMSTONES, List.of()).forGetter(f -> f.gemstones))
			.apply(instance, (conditions, count, rarity, rarities, gemstones) -> new RandomGemstone(conditions, count.orElse(null),
					LootFunctionHelper.rarity(rarity, MagicTreasuresRarity.NONE), LootFunctionHelper.rarities(rarities),
					gemstones)));
}
