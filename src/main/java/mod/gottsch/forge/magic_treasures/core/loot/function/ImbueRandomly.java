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

import com.google.gson.*;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.forge.magic_treasures.core.loot.MagicTreasuresLootFunctions;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.forge.magic_treasures.core.spell.ISpell;
import mod.gottsch.forge.magic_treasures.core.spell.MagicTreasuresSpells;
import mod.gottsch.forge.magic_treasures.core.spell.SpellEntity;
import mod.gottsch.forge.magic_treasures.core.spell.SpellRegistry;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 *
 * @author Mark Gottschling on May 23, 2024
 *
 */
public class ImbueRandomly extends LootItemConditionalFunction {
//	private static final ResourceLocation LOCATION = new ResourceLocation("gealdorcraft:random_spell");
	private static final String LEVELS = "levels";
	private static final String RARITY = "rarity";
	private static final String RARITIES = "rarities";

	private final NumberProvider levels;
	private final IRarity rarity;
	private final List<IRarity> rarities;

	/**
	 *
	 * @param conditions
	 * @param levels
	 * @param rarity
	 */
	public ImbueRandomly(LootItemCondition[] conditions, NumberProvider levels, IRarity rarity, List<IRarity> rarities) {
		super(conditions);
		this.levels = levels;
		this.rarity = rarity;
		this.rarities = rarities != null ? rarities : new ArrayList<>();
	}

	@Override
	public LootItemFunctionType getType() {
		return MagicTreasuresLootFunctions.IMBUE_RANDOMLY;
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		Random rand = new Random();
		MagicTreasures.LOGGER.debug("incoming stack -> {}", stack.getDisplayName());
		List<ISpell> spells = new ArrayList<>();
		ISpell spell;

		MagicTreasures.LOGGER.debug("rarity -> {}", rarity);

		int stackMaxLevel = stack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).map(IJewelryHandler::getMaxLevel).orElse(0);

		spell = MagicTreasuresSpells.DEFAULT_HEALING;
		if (rarity != null && rarity != MagicTreasuresRarity.NONE) {
			// get all the spells by rarity
			spells.addAll(SpellRegistry.get(rarity));

		} else if (!rarities.isEmpty()) {
			for (IRarity rarity : rarities) {
				spells.addAll(SpellRegistry.get(rarity));
			}
		} else {
			// select random level
			int level = this.levels == null ? 1 : this.levels.getInt(context);
			MagicTreasures.LOGGER.debug("selected level -> {}", level);

			// select a spell by level
			spells.addAll(SpellRegistry.get(level));
		}

		// filter spells to those <= stackMaxLevel
		spells = spells.stream().filter(s -> s.getLevel() <= stackMaxLevel).toList();

		// default
		if (spells.isEmpty()) {
			spells = SpellRegistry.get(stackMaxLevel);
		}

		if (!spells.isEmpty()) {
			spell = spells.get(new Random().nextInt(spells.size()));
		}
		MagicTreasures.LOGGER.debug("selected spell -> {}", spell.getName());

		SpellEntity spellEntity = spell.entity();
		stack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).ifPresent(handler -> {
			handler.getSpells().add(spellEntity);
		});

		return stack;
	}

	/*
	 *
	 */
	public static class Serializer extends LootItemConditionalFunction.Serializer<ImbueRandomly> {
		public void serialize(JsonObject json, ImbueRandomly function, JsonSerializationContext context) {
			super.serialize(json, function, context);
			json.add(LEVELS, context.serialize(function.levels));
			json.addProperty(RARITY, function.rarity.getName());
			if (!function.rarities.isEmpty()) {
				final JsonArray jsonArray = new JsonArray();
				function.rarities.forEach(r -> {
					jsonArray.add(new JsonPrimitive(r.getName().toString()));
				});
				json.add(RARITIES, jsonArray);
			}
		}

		public ImbueRandomly deserialize(JsonObject jsonObject, JsonDeserializationContext context, LootItemCondition[] conditions) {
			NumberProvider levels = null;
			if (jsonObject.has(LEVELS)) {
				levels = GsonHelper.getAsObject(jsonObject, LEVELS, context, NumberProvider.class);
			}
			String rarityStr = "";
			if(jsonObject.has(RARITY)) {
				rarityStr = GsonHelper.getAsString(jsonObject, RARITY);
            }
			IRarity rarity = MagicTreasuresApi.getRarity(rarityStr).orElse(MagicTreasuresRarity.NONE);

			List<IRarity> rarities = new ArrayList<>();
			if (jsonObject.has(RARITIES)) {
				GsonHelper.getAsJsonArray(jsonObject, RARITIES).forEach(element -> {
					String rarityName = GsonHelper.convertToString(element, RARITY);
					IRarity r = MagicTreasuresApi.getRarity(rarityName).orElse(MagicTreasuresRarity.NONE);
					if (r != MagicTreasuresRarity.NONE) {
						rarities.add(r);
					}
                });
            }
			return new ImbueRandomly(conditions, levels, rarity, rarities);
		}
	}
}