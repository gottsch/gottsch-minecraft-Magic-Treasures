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
package mod.gottsch.neo.magic_treasures.datagen;

import java.util.Map;
import mod.gottsch.neo.magic_treasures.core.item.Jewelry;
import java.util.Optional;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.neo.magic_treasures.core.spell.SpellRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Created by Mark Gottschling on 6/1/2023
 */
public class ItemModelsProvider extends ItemModelProvider {
	/** 1.x named pieces drawn with a combination's art (as their 1.x item models and the M8 presets' texture) */
	private static final Map<String, String> LEGACY_BORROWED_ART = Map.of(
			"strongmans_bracers", "wood_bracelet",
			"maldritchs_first_amulet", "bone_onyx_necklace",
			"peasants_fortune", "great_iron_ring",
			"amulet_of_defence", "copper_topaz_necklace");


	public ItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, MagicTreasures.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		// tabs
		singleTexture(MagicTreasuresItems.MAGIC_TREASURES_TAB.getId().getPath(),
				mcLoc("item/generated"), "layer0", modLoc("item/magic_treasures_tab"));

		// tools
		singleTexture(MagicTreasuresItems.JEWELRY_PLIERS.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/jewelry_pliers"));

		// ingots
		singleTexture(MagicTreasuresItems.SILVER_INGOT.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/silver_ingot"));

		// ores
		singleTexture(MagicTreasuresItems.RAW_SILVER.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/raw_silver"));

		// recipes
		singleTexture(MagicTreasuresItems.RING_RECIPE.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/ring_recipe_scroll"));
		singleTexture(MagicTreasuresItems.NECKLACE_RECIPE.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/necklace_recipe_scroll"));
		singleTexture(MagicTreasuresItems.BRACELET_RECIPE.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/bracelet_recipe_scroll"));

		// recharge scroll
		singleTexture(MagicTreasuresItems.RECHARGE_SCROLL.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/recharge_scroll"));

		/*
		 * jewelry
		 */
		// stones
		singleTexture(MagicTreasuresItems.JADEITE.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/gem/jadeite"));
		singleTexture(MagicTreasuresItems.TOPAZ.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/gem/topaz"));
		singleTexture(MagicTreasuresItems.ONYX.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/gem/onyx"));
		singleTexture(MagicTreasuresItems.RUBY.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/gem/ruby"));
		singleTexture(MagicTreasuresItems.SAPPHIRE.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/gem/sapphire"));
		singleTexture(MagicTreasuresItems.WHITE_PEARL.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/gem/white_pearl"));
		singleTexture(MagicTreasuresItems.BLACK_PEARL.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/gem/black_pearl"));
		
		// the 418 1.x item ids (M9: legacy items that convert when carried) keep their 1.x art, so unconverted stacks in
		// chests, storage and item frames still look right. Named pieces that borrowed a combination's art keep it.
		MagicTreasuresItems.LEGACY_JEWELRY.forEach((id, item) -> singleTexture(id.getPath(),
				modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/" + LEGACY_BORROWED_ART.getOrDefault(id.getPath(), id.getPath()))));

		// scrolls
		SpellRegistry.values().forEach(spell -> {
			singleTexture(spell.getName().getPath() + "_scroll",
					mcLoc("item/generated"), "layer0", modLoc(
							switch(spell.getLevel()) {
								case 1,2 -> "item/yellow_spell_scroll";
								case 3,4 -> "item/green_spell_scroll";
								case 5,6 -> "item/blue_spell_scroll";
								case 7,8 -> "item/red_spell_scroll";
                                default -> "item/black_spell_scroll";
                            }));
		});

		// set weapons and armor (the Rootstaff's model is hand-made in src/main: it's drawn bigger in hand)
		singleTexture(MagicTreasuresItems.BARKSKIN_VEST.getId().getPath()
				, mcLoc("item/generated"), "layer0", modLoc("item/set/barkskin_vest"));
		// the Stonemark Hammer's, Bone Scythe's, Moonglaive's and Anvil Shield's models are hand-made in src/main (handheld; shield elements)
		singleTexture(MagicTreasuresItems.SMITHS_HELM.getId().getPath()
				, mcLoc("item/generated"), "layer0", modLoc("item/set/smiths_helm"));
		singleTexture(MagicTreasuresItems.LICHS_MASK.getId().getPath()
				, mcLoc("item/generated"), "layer0", modLoc("item/set/lichs_mask"));
		singleTexture(MagicTreasuresItems.PEARL_CIRCLET.getId().getPath()
				, mcLoc("item/generated"), "layer0", modLoc("item/set/pearl_circlet"));

		// belts
		singleTexture(MagicTreasuresItems.SKULL_BELT.getId().getPath()
				, mcLoc("item/generated"), "layer0", modLoc("item/skull_belt"));

		// Ring of Swords' sword: see-through, so the translucent render type
		singleTexture(MagicTreasuresItems.SPECTRAL_SWORD.getId().getPath()
				, mcLoc("item/handheld"), "layer0", modLoc("item/spectral_sword"))
				.renderType("minecraft:translucent");

		/*
		 * blocks
		 */
		withExistingParent(MagicTreasuresItems.TOPAZ_ORE.getId().getPath(), modLoc("block/topaz_ore"));
		withExistingParent(MagicTreasuresItems.ONYX_ORE.getId().getPath(), modLoc("block/onyx_ore"));
		withExistingParent(MagicTreasuresItems.JADEITE_ORE.getId().getPath(), modLoc("block/jadeite_ore"));
		withExistingParent(MagicTreasuresItems.RUBY_ORE.getId().getPath(), modLoc("block/ruby_ore"));
		withExistingParent(MagicTreasuresItems.SAPPHIRE_ORE.getId().getPath(), modLoc("block/sapphire_ore"));
		withExistingParent(MagicTreasuresItems.SILVER_ORE_BLOCKITEM.getId().getPath(), modLoc("block/silver_ore"));

		withExistingParent(MagicTreasuresItems.DEEPSLATE_TOPAZ_ORE.getId().getPath(), modLoc("block/deepslate_topaz_ore"));
		withExistingParent(MagicTreasuresItems.DEEPSLATE_ONYX_ORE.getId().getPath(), modLoc("block/deepslate_onyx_ore"));
		withExistingParent(MagicTreasuresItems.DEEPSLATE_JADEITE_ORE.getId().getPath(), modLoc("block/deepslate_jadeite_ore"));
		withExistingParent(MagicTreasuresItems.DEEPSLATE_RUBY_ORE.getId().getPath(), modLoc("block/deepslate_ruby_ore"));
		withExistingParent(MagicTreasuresItems.DEEPSLATE_SAPPHIRE_ORE.getId().getPath(), modLoc("block/deepslate_sapphire_ore"));
		withExistingParent(MagicTreasuresItems.DEEPSLATE_SILVER_ORE_BLOCKITEM.getId().getPath(), modLoc("block/deepslate_silver_ore"));
	}
	
}
