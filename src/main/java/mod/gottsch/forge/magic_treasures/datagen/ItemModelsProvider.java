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
package mod.gottsch.forge.magic_treasures.datagen;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.forge.magic_treasures.core.spell.SpellRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Created by Mark Gottschling on 6/1/2023
 */
public class ItemModelsProvider extends ItemModelProvider {

	public ItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, MagicTreasures.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		// tabs
		singleTexture(MagicTreasuresItems.MAGIC_TREASURES_TAB.getId().getPath(),
				mcLoc("item/generated"), "layer0", modLoc("item/magic_treasures_tab"));

		// tools
//		singleTexture(MagicTreasuresItems.JEWELRY_PLIERS.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/jewelry_pliers"));

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
		
		// TODO this method does not distinguish between regular and special jewelry ie Gottsch Ring of the Moon.
		MagicTreasuresItems.STANDARD_JEWELRY.forEach(item -> {
			singleTexture(item.getId().getPath(),
					modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/" + item.getId().getPath()));
		});

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

		// special / non-standard jewelry
		singleTexture(MagicTreasuresItems.SILBROS_RING_OF_VITALITY.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/silbros_ring_of_vitality"));

		singleTexture(MagicTreasuresItems.STRONGMANS_BRACERS.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/wood_bracelet"));

		singleTexture(MagicTreasuresItems.MALDRITCHS_FIRST_AMULET.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/bone_onyx_necklace"));

		singleTexture(MagicTreasuresItems.PEASANTS_FORTUNE.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/great_iron_ring"));

		singleTexture(MagicTreasuresItems.AQUA_RING.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/silver_topaz_ring"));

		singleTexture(MagicTreasuresItems.AMULET_OF_DEFENCE.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/copper_topaz_necklace"));

		singleTexture(MagicTreasuresItems.JOURNEYMANS_BANDS.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/great_gold_jadeite_bracelet"));

		singleTexture(MagicTreasuresItems.MEDICS_TOKEN.getId().getPath(), modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/medics_token"));
		singleTexture(MagicTreasuresItems.ADEPHAGIAS_BOUNTY.getId().getPath(), modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/adephagias_bounty"));

		singleTexture(MagicTreasuresItems.ANGELS_RING.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/angels_ring"));

		singleTexture(MagicTreasuresItems.SALANDAARS_WARD.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/salandaars_ward"));

		singleTexture(MagicTreasuresItems.RING_OF_FORTITUDE.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/ring_of_fortitude"));

		singleTexture(MagicTreasuresItems.EYE_OF_THE_PHOENIX.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/eye_of_the_phoenix"));

		singleTexture(MagicTreasuresItems.RING_LIFE_DEATH.getId().getPath()
				, modLoc("item/jewelry"), "layer0", modLoc("item/jewelry/ring_of_life_death"));

		// belts
		singleTexture(MagicTreasuresItems.SKULL_BELT.getId().getPath()
				, mcLoc("item/generated"), "layer0", modLoc("item/skull_belt"));

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
	
	@Deprecated
	private ResourceLocation buildLoc(Item item) {
		StringBuilder loc = new StringBuilder("items/jewelry/");
		
		ItemStack stack = new ItemStack(item);
		stack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).ifPresent(c -> {
		if (c.getJewelrySizeTier() != JewelrySizeTier.REGULAR) {
			loc .append(c.getJewelrySizeTier().getValue() + "_");
		}
		
		// stone (can't get from key....)

			if (c.hasStone()) {
				loc.append(c.getStone().getPath()).append("_");
			}
			
			// material
			loc.append(c.getMaterial().getName() + "_");
			
			// type
			loc.append(c.getJewelryType().getValue());
			
		});		
		return mcLoc(loc.toString());
	}

}
