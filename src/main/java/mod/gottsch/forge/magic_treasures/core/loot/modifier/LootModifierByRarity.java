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
package mod.gottsch.forge.magic_treasures.core.loot.modifier;

import mod.gottsch.forge.magic_treasures.core.item.LoreBook;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresRegistries;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.random.RandomHelper;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.forge.magic_treasures.core.config.Config;
import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.forge.magic_treasures.core.item.set.SetArmor;
import mod.gottsch.forge.magic_treasures.core.item.set.SetShield;
import mod.gottsch.forge.magic_treasures.core.item.set.SiphonWeapon;
import mod.gottsch.forge.magic_treasures.core.set.JewelrySets;
import mod.gottsch.forge.magic_treasures.core.item.SpellScroll;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryLoot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

	public static final Supplier<Codec<LootModifierByRarity>> CODEC = Suppliers.memoize(()
			-> RecordCodecBuilder.create(inst -> codecStart(inst)
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
	public Codec<? extends IGlobalLootModifier> codec() {
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
				map(ForgeConfigSpec.ConfigValue::get).orElse(false);
		MagicTreasures.LOGGER.debug("isEnabled for {} -> {}", rarity, isEnabled);

		if (Config.SERVER.loot.enableVanillaLootModifiers.get()
				&& isEnabled
				&& RandomHelper.checkProbability(context.getLevel().getRandom(), localChance * 100)) {

			IRarity rarity = MagicTreasuresApi.getRarity(this.rarity).orElse(MagicTreasuresRarity.NONE);
			// 2.0: jewelry and gems of exactly this rarity from the registries (as 1.x's rarity tags; no fallback here,
			// the other entries fill the list); a preset with loot weight n is in the list n times
			List<ItemStack> lootList = new ArrayList<>();
			JewelryLoot.jewelry().stream()
					.filter(candidate -> candidate.rarity().getCode().equals(rarity.getCode()))
					.forEach(candidate -> {
						for (int i = 0; i < candidate.weight(); i++) {
							lootList.add(candidate.stack());
						}
					});
			JewelryLoot.gemstones().stream()
					.filter(gem -> gem.value().rarity().map(r -> r.getCode().equals(rarity.getCode())).orElse(false))
					.forEach(gem -> lootList.add(new ItemStack(JewelryLoot.item(gem))));
			MagicTreasuresItems.ALL_SPELL_SCROLLS.stream()
					.map(RegistryObject::get)
					.filter(scroll -> ((SpellScroll)scroll).getSpell().getRarity() == rarity)
					.forEach(scroll -> lootList.add(new ItemStack(scroll)));
			// add some recipes
			if (rarity.getCode() > MagicTreasuresRarity.UNCOMMON.getCode()) {
				lootList.add(new ItemStack(MagicTreasuresItems.RING_RECIPE.get()));
				lootList.add(new ItemStack(MagicTreasuresItems.BRACELET_RECIPE.get()));
				lootList.add(new ItemStack(MagicTreasuresItems.NECKLACE_RECIPE.get()));
//				lootList.add(new ItemStack(MagicTreasuresItems.BELT_RECIPE.get()));
			}
			// mana wells
			if (rarity.getCode() >= MagicTreasuresRarity.RARE.getCode()) {
				lootList.add(new ItemStack(MagicTreasuresItems.SKULL_BELT.get()));
			}
			// set weapons, armor and shields: every one in a set (jewelry and mana wells have their own rules above)
			if (rarity.getCode() >= MagicTreasuresRarity.UNCOMMON.getCode()) {
				JewelrySets.getAll().stream()
						.flatMap(set -> set.getItems().stream())
						.filter(item -> item instanceof SiphonWeapon || item instanceof SetArmor || item instanceof SetShield)
						.forEach(item -> lootList.add(new ItemStack(item)));
			}
			// lore books of this rarity, from the registry
			context.getLevel().registryAccess().registry(MagicTreasuresRegistries.LORE_BOOK).ifPresent(books -> books.holders()
					.filter(book -> !book.value().disabled() && book.value().rarity().getCode() == rarity.getCode())
					.forEach(book -> lootList.add(LoreBook.of(MagicTreasuresItems.LORE_BOOK.get(), book.key().location()))));
			// grab random loot from the loot list (without replacement)
			for (int index = 0; index < count && !lootList.isEmpty(); index++) {
				ItemStack stack = lootList.remove(context.getRandom().nextInt(lootList.size()));
				generatedLoot.add(stack);
				// set pieces drop one per chest
				if (JewelrySets.get(stack).isPresent()) {
					lootList.removeIf(other -> JewelrySets.get(other).isPresent());
				}
				// and lore books one per chest too
				if (stack.is(MagicTreasuresItems.LORE_BOOK.get())) {
					lootList.removeIf(other -> other.is(MagicTreasuresItems.LORE_BOOK.get()));
				}
			}
		}
		return generatedLoot;
	}
}
