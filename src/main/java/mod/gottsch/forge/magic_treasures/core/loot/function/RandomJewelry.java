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
package mod.gottsch.forge.magic_treasures.core.loot.function;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import net.minecraft.resources.ResourceLocation;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.GemstoneDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryLoot;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryLoot.Candidate;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryMaterialDef;
import mod.gottsch.forge.magic_treasures.core.loot.MagicTreasuresLootFunctions;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Replaces the stack with random 2.0 generic jewelry built from the registries ({@link JewelryLoot}); the incoming
 * item is ignored, as in 1.x. JSON (unchanged from 1.x, plus {@code #tags}):
 * <ul>
 *     <li>{@code rarity}: one rarity; empty falls back to the nearest lower one. Overrides {@code levels}.</li>
 *     <li>{@code rarities}: several, each with that fallback.</li>
 *     <li>{@code materials}: {@code jewelry_material} ids or #tags.</li>
 *     <li>{@code gemstones}: {@code gemstone} ids or #tags; the jewelry must carry one of them.</li>
 *     <li>{@code levels}: with no rarity, the jewelry's max level must be at least this.</li>
 * </ul>
 * Bare names default to the magictreasures namespace. Nothing matching gives an empty stack.
 *
 * @author Mark Gottschling on May 22, 2024
 */
public class RandomJewelry extends LootItemConditionalFunction {
	private static final String RARITY = "rarity";
	private static final String RARITIES = "rarities";
	private static final String GEMSTONES = "gemstones";
	private static final String LEVELS = "levels";
	private static final String MATERIALS = "materials";

	private final IRarity rarity;
	private final List<IRarity> rarities;
	private final List<String> materials;
	private final List<String> gemstones;
	private final NumberProvider levels;

	public RandomJewelry(LootItemCondition[] conditions, IRarity rarity, List<IRarity> rarities,
						 List<String> materials, List<String> gemstones, NumberProvider levels) {
		super(conditions);
		this.rarity = rarity;
		this.rarities = rarities;
		this.materials = materials;
		this.gemstones = gemstones;
		this.levels = levels;
	}

	@Override
	public LootItemFunctionType getType() {
		return MagicTreasuresLootFunctions.RANDOM_JEWELRY.get();
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		Predicate<Candidate> filter = filter(materials, gemstones);
		List<Candidate> candidates;
		if (rarity != MagicTreasuresRarity.NONE) {
			// NOTE do not filter by level as rarity supersedes it
			candidates = JewelryLoot.jewelry(rarity, filter);
		} else if (!rarities.isEmpty()) {
			// distinct: two rarities may fall back to the same one
			Set<Candidate> union = new LinkedHashSet<>();
			rarities.forEach(r -> union.addAll(JewelryLoot.jewelry(r, filter)));
			candidates = List.copyOf(union);
		} else {
			int level = levels == null ? 0 : levels.getInt(context);
			candidates = JewelryLoot.jewelry().stream()
					.filter(filter)
					.filter(candidate -> candidate.stats().maxLevel() >= level)
					.toList();
		}
		ItemStack result = JewelryLoot.pick(candidates, context.getRandom()).map(Candidate::stack).orElse(ItemStack.EMPTY);
		if (result.isEmpty()) {
			MagicTreasures.LOGGER.debug("random_jewelry: nothing matches rarity {}, rarities {}, materials {}, gemstones {}",
					rarity, rarities, materials, gemstones);
		}
		return result;
	}

	/** the materials and gemstones filters; a filtered gem means the jewelry must have one */
	static Predicate<Candidate> filter(List<String> materials, List<String> gemstones) {
		Predicate<Holder<JewelryMaterialDef>> material = LootFunctionHelper.holders(MagicTreasuresRegistries.JEWELRY_MATERIAL, materials);
		Predicate<Holder<GemstoneDef>> gem = LootFunctionHelper.holders(MagicTreasuresRegistries.GEMSTONE, gemstones);
		return candidate -> JewelryDataIndex.material(candidate.identity().material()).map(material::test).orElse(false)
				&& (gemstones.isEmpty() || candidate.identity().gem().flatMap(JewelryDataIndex::gemstone).map(gem::test).orElse(false));
	}

	public static class Serializer extends LootItemConditionalFunction.Serializer<RandomJewelry> {
		@Override
		public void serialize(JsonObject json, RandomJewelry value, JsonSerializationContext context) {
			super.serialize(json, value, context);
			if (value.rarity != MagicTreasuresRarity.NONE) {
				json.addProperty(RARITY, value.rarity.getName());
			}
			LootFunctionHelper.writeStrings(json, RARITIES, LootFunctionHelper.rarityNames(value.rarities));
			LootFunctionHelper.writeStrings(json, MATERIALS, value.materials);
			LootFunctionHelper.writeStrings(json, GEMSTONES, value.gemstones);
			if (value.levels != null) {
				json.add(LEVELS, context.serialize(value.levels));
			}
		}

		@Override
		public RandomJewelry deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
			NumberProvider levels = json.has(LEVELS) ? GsonHelper.getAsObject(json, LEVELS, context, NumberProvider.class) : null;
			return new RandomJewelry(conditions,
					LootFunctionHelper.rarity(GsonHelper.getAsString(json, RARITY, ""), MagicTreasuresRarity.NONE),
					LootFunctionHelper.rarities(LootFunctionHelper.readStrings(json, RARITIES)),
					LootFunctionHelper.readStrings(json, MATERIALS), LootFunctionHelper.readStrings(json, GEMSTONES), levels);
		}
	}
}
