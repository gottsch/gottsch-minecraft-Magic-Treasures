/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.gottschcore.random.RandomHelper;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.neo.magic_treasures.core.config.Config;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.set.JewelrySets;
import mod.gottsch.neo.magic_treasures.core.item.SpellScroll;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.neo.magic_treasures.core.registry.JewelryRegistry;
import mod.gottsch.neo.magic_treasures.core.registry.StoneRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 
 * @author Mark Gottschling Jun 12, 2023
 *
 */
public class LootModifierByRarity extends LootModifier {
	// the number of items to add
	private final int count;
	private final String rarity;
	private final double chance;
	/** which enable option in the server config gates this modifier; blank = the one named after its rarity */
	private final String config;

	public static final Supplier<MapCodec<LootModifierByRarity>> CODEC = Suppliers.memoize(()
			-> RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
			.and(Codec.INT.fieldOf("count").forGetter(m -> m.count))
			.and(Codec.STRING.fieldOf("rarity").forGetter(m -> m.rarity))
			.and(Codec.DOUBLE.fieldOf("chance").forGetter(m -> m.chance))
			.and(Codec.STRING.optionalFieldOf("config", "").forGetter(m -> m.config))
			.apply(inst, LootModifierByRarity::new)));


	protected LootModifierByRarity(LootItemCondition[] conditionsIn, int count, String rarity, double chance, String config) {
		super(conditionsIn);
		this.count = count;
		this.rarity = rarity;
		this.chance = chance;
		this.config = config;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		MagicTreasures.LOGGER.debug("executing LootModifierByRarity");

		// if chance was left blank or null, then set to 100% by default
		double localChance = chance == 0.0 ? 1.0 : chance;

		// determine if specific loot modifier is enabled
		String configKey = config.isBlank() ? rarity.toLowerCase() : config;
		boolean isEnabled = Optional.ofNullable(Config.enableLootModifiers.get(configKey)).
				map(ModConfigSpec.ConfigValue::get).orElse(false);
		MagicTreasures.LOGGER.debug("isEnabled for {} -> {}", rarity, isEnabled);

		if (Config.SERVER.loot.enableVanillaLootModifiers.get()
				&& isEnabled
				&& RandomHelper.checkProbability(context.getLevel().getRandom(), localChance * 100)) {

			IRarity rarity = MagicTreasuresApi.getRarity(this.rarity).orElse(MagicTreasuresRarity.NONE);
			List<Item> lootList = JewelryRegistry.get(rarity);
			lootList.addAll(StoneRegistry.get(rarity));
			lootList.addAll(
					MagicTreasuresItems.ALL_SPELL_SCROLLS.stream()
							.map(DeferredItem::get)
							.filter(scroll -> ((SpellScroll)scroll).getSpell().getRarity() == rarity)
							.toList()
			);
			// add some recipes
			if (rarity.getCode() > MagicTreasuresRarity.UNCOMMON.getCode()) {
				lootList.add(MagicTreasuresItems.RING_RECIPE.get());
				lootList.add(MagicTreasuresItems.BRACELET_RECIPE.get());
				lootList.add(MagicTreasuresItems.NECKLACE_RECIPE.get());
//				lootList.add(MagicTreasuresItems.BELT_RECIPE.get());
			}
			// mana wells
			if (rarity.getCode() >= MagicTreasuresRarity.RARE.getCode()) {
				lootList.add(MagicTreasuresItems.SKULL_BELT.get());
			}
			// set weapons and armor
			if (rarity.getCode() >= MagicTreasuresRarity.UNCOMMON.getCode()) {
				lootList.add(MagicTreasuresItems.ROOTSTAFF.get());
				lootList.add(MagicTreasuresItems.BARKSKIN_VEST.get());
			}
			// grab random loot from the loot list (without replacement)
			for (int index = 0; index < count && !lootList.isEmpty(); index++) {
				Item item = lootList.remove(context.getRandom().nextInt(lootList.size()));
				generatedLoot.add(new ItemStack(item));
				// set pieces drop one per chest
				if (JewelrySets.get(item).isPresent()) {
					lootList.removeIf(other -> JewelrySets.get(other).isPresent());
				}
			}
		}
		return generatedLoot;
	}
}
