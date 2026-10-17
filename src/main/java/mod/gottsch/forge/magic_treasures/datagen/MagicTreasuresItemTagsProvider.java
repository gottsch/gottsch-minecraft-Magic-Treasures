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

import com.google.common.collect.Maps;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.item.*;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryMaterial;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryMaterials;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.forge.magic_treasures.core.setup.Registration;
import mod.gottsch.forge.magic_treasures.core.tag.MagicTreasuresTags;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import mod.gottsch.forge.treasure2.core.item.TreasureItems;
import mod.gottsch.forge.treasure2.core.tags.TreasureTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Mark Gottschling on 5/29/2023
 */
public class MagicTreasuresItemTagsProvider extends ItemTagsProvider {
	/**
	 *
	 */
	public MagicTreasuresItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
										  CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookup, blockTagProvider, MagicTreasures.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {

		// TODO add Silver Ingot to forge "ore registry" tag


		/*
		 * map from jewelry type attribute to tag
		 */
		Map<IJewelryType, TagKey<Item>> TYPE_TAG_MAP = Maps.newHashMap();
		TYPE_TAG_MAP.put(JewelryType.RING, MagicTreasuresTags.Items.RINGS);
		TYPE_TAG_MAP.put(JewelryType.BRACELET, MagicTreasuresTags.Items.BRACELETS);
//		TYPE_TAG_MAP.put(JewelryType.BROACH, GealdorCraftTags.Items.BROACHES);
//		TYPE_TAG_MAP.put(JewelryType.CHARM, MagicTreasuresTags.Items.CHARMS);
//		TYPE_TAG_MAP.put(JewelryType.EARRING, GealdorCraftTags.Items.EARRINGS);
		TYPE_TAG_MAP.put(JewelryType.NECKLACE, MagicTreasuresTags.Items.NECKLACES);
		TYPE_TAG_MAP.put(JewelryType.POCKET, MagicTreasuresTags.Items.POCKETS);

		/*
		 * map from jewelry type attribute to curios tag
		 */
		Map<IJewelryType, TagKey<Item>> CURIOS_TYPE_TAG_MAP = Maps.newHashMap();
		CURIOS_TYPE_TAG_MAP.put(JewelryType.RING, MagicTreasuresTags.Items.CURIOUS_RING);
		CURIOS_TYPE_TAG_MAP.put(JewelryType.BRACELET, MagicTreasuresTags.Items.CURIOUS_BRACELET);
		CURIOS_TYPE_TAG_MAP.put(JewelryType.NECKLACE, MagicTreasuresTags.Items.CURIOUS_NECKLACE);

		Map<JewelryMaterial, TagKey<Item>> MATERIAL_TAG_MAP = Maps.newHashMap();
		MATERIAL_TAG_MAP.put(JewelryMaterials.WOOD, MagicTreasuresTags.Items.WOOD);
		MATERIAL_TAG_MAP.put(JewelryMaterials.IRON, MagicTreasuresTags.Items.IRON);
		MATERIAL_TAG_MAP.put(JewelryMaterials.COPPER, MagicTreasuresTags.Items.COPPER);
		MATERIAL_TAG_MAP.put(JewelryMaterials.SILVER, MagicTreasuresTags.Items.SILVER);
		MATERIAL_TAG_MAP.put(JewelryMaterials.GOLD, MagicTreasuresTags.Items.GOLD);
		MATERIAL_TAG_MAP.put(JewelryMaterials.BLOOD, MagicTreasuresTags.Items.BLOOD);
		MATERIAL_TAG_MAP.put(JewelryMaterials.BONE, MagicTreasuresTags.Items.BONE);
		MATERIAL_TAG_MAP.put(JewelryMaterials.SHADOW, MagicTreasuresTags.Items.SHADOW);
		MATERIAL_TAG_MAP.put(JewelryMaterials.ATIUM, MagicTreasuresTags.Items.ATIUM);
		
		Map<IJewelrySizeTier, TagKey<Item>> SIZE_TAG_MAP = Maps.newHashMap();
		SIZE_TAG_MAP.put(JewelrySizeTier.REGULAR, MagicTreasuresTags.Items.REGULAR);
		SIZE_TAG_MAP.put(JewelrySizeTier.GREAT, MagicTreasuresTags.Items.GREAT);
		SIZE_TAG_MAP.put(JewelrySizeTier.LORDS, MagicTreasuresTags.Items.LORDS);

		Map<IRarity, TagKey<Item>> JEWELRY_RARITY_TAG_MAP = Maps.newHashMap();
		JEWELRY_RARITY_TAG_MAP.put(MagicTreasuresRarity.COMMON, MagicTreasuresTags.Items.JEWELRY_COMMON);
		JEWELRY_RARITY_TAG_MAP.put(MagicTreasuresRarity.UNCOMMON, MagicTreasuresTags.Items.JEWELRY_UNCOMMON);
		JEWELRY_RARITY_TAG_MAP.put(MagicTreasuresRarity.SCARCE, MagicTreasuresTags.Items.JEWELRY_SCARCE);
		JEWELRY_RARITY_TAG_MAP.put(MagicTreasuresRarity.RARE, MagicTreasuresTags.Items.JEWELRY_RARE);
		JEWELRY_RARITY_TAG_MAP.put(MagicTreasuresRarity.EPIC, MagicTreasuresTags.Items.JEWELRY_EPIC);
		JEWELRY_RARITY_TAG_MAP.put(MagicTreasuresRarity.LEGENDARY, MagicTreasuresTags.Items.JEWELRY_LEGENDARY);
		JEWELRY_RARITY_TAG_MAP.put(MagicTreasuresRarity.MYTHICAL, MagicTreasuresTags.Items.JEWELRY_MYTHICAL);

		// default stone rarity mapping
		Map<Item, IRarity> STONE_RARITY_MAP = Maps.newHashMap();
		STONE_RARITY_MAP.put(MagicTreasuresItems.TOPAZ.get(), MagicTreasuresRarity.COMMON);
		STONE_RARITY_MAP.put(MagicTreasuresItems.ONYX.get(), MagicTreasuresRarity.UNCOMMON);
		STONE_RARITY_MAP.put(MagicTreasuresItems.JADEITE.get(), MagicTreasuresRarity.SCARCE);
		STONE_RARITY_MAP.put(Items.DIAMOND, MagicTreasuresRarity.SCARCE);
		STONE_RARITY_MAP.put(MagicTreasuresItems.RUBY.get(), MagicTreasuresRarity.RARE);
		STONE_RARITY_MAP.put(MagicTreasuresItems.SAPPHIRE.get(), MagicTreasuresRarity.EPIC);
		STONE_RARITY_MAP.put(MagicTreasuresItems.WHITE_PEARL.get(), MagicTreasuresRarity.RARE);
		STONE_RARITY_MAP.put(MagicTreasuresItems.BLACK_PEARL.get(), MagicTreasuresRarity.EPIC);

		// default material rarity mapping
		Map<JewelryMaterial, IRarity> MATERIAL_RARITY_MAP = Maps.newHashMap();
		MATERIAL_RARITY_MAP.put(JewelryMaterials.WOOD, MagicTreasuresRarity.COMMON);
		MATERIAL_RARITY_MAP.put(JewelryMaterials.IRON, MagicTreasuresRarity.COMMON);
		MATERIAL_RARITY_MAP.put(JewelryMaterials.COPPER, MagicTreasuresRarity.UNCOMMON);
		MATERIAL_RARITY_MAP.put(JewelryMaterials.SILVER, MagicTreasuresRarity.SCARCE);
		MATERIAL_RARITY_MAP.put(JewelryMaterials.GOLD, MagicTreasuresRarity.RARE);
		MATERIAL_RARITY_MAP.put(JewelryMaterials.BONE, MagicTreasuresRarity.SCARCE);
		// TODO add blood, shadow, atium, etc

		// jewelry tools
		tag(MagicTreasuresTags.Items.STONE_REMOVAL_TOOLS)
				.addTag(Tags.Items.SHEARS)
				.add(Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);

		/*
		 *  process all items.
		 *  if item has Jewelry capability, then categorize it into the different tags
		 */
		Registration.ITEMS.getEntries().forEach(registryItem -> {
			ItemStack stack = new ItemStack(registryItem.get());
			stack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).ifPresent(handler -> {
				// add to the type tag
				tag(TYPE_TAG_MAP.get(handler.getJewelryType())).add(registryItem.get());
				// -- curios integration
				TagKey<Item> curiosTagKey = CURIOS_TYPE_TAG_MAP.get(handler.getJewelryType());
				if (curiosTagKey != null) {
					tag(curiosTagKey).add(registryItem.get());
				}
				// add to the material tag
				tag(MATERIAL_TAG_MAP.get(handler.getMaterial())).add(registryItem.get());
				tag(SIZE_TAG_MAP.get(handler.getJewelrySizeTier())).add(registryItem.get());
				
				// add to the stone tag
//				if (c.hasStone()) {
//					Item stoneItem = ForgeRegistries.ITEMS.getValue(c.getStone());
//					tag(STONE_TAG_MAP.get(stoneItem)).add(item.get());
//				}

				//
				//
				/*
				 ********************************
				 calculate rarity by examining the stone and/ore material
				 ********************************
				 NOTE stone rarity takes precedence over material
				 NOTE stoneHandler, stoneTier, material do not contain any data related to Rarity.
				 Rarities are registered via tags and so, tags wouldn't be populated here yet.
				 Will have to setup hard-coded maps for the default setup that map from stone,
				 material to a rarity.
				 Ex.
				 	map.put(wood, common)
				 	...
				 	map.put(gold, rare)

				 	map2.put(topaz, common)
				 	...
				 	map2.put(ruby, rare)
				 */

				// only run this for standard jewelry (special is handled explicitly)
				if (!MagicTreasuresItems.STANDARD_JEWELRY.contains(registryItem)) return;

				IRarity materialRarity = MATERIAL_RARITY_MAP.get(handler.getMaterial());
				IRarity rarity = materialRarity;
				if (handler.getStone() != null && !handler.getStone().equals(ModUtil.getName(Items.AIR))) {
//				if (handler.hasStone()) { // can't use hasStone here as it uses tag which aren't loaded yet
					Item stoneItem = ForgeRegistries.ITEMS.getValue(handler.getStone());
					IRarity stoneRarity = STONE_RARITY_MAP.get(stoneItem);

					if (stoneRarity != null) {
						rarity = MagicTreasuresRarity.getByCode(Math.max(stoneRarity.getCode(), materialRarity.getCode()));

//						if (handler.getJewelrySizeTier() != JewelrySizeTier.REGULAR && rarity.getCode() < 2) {
//							rarity = MagicTreasuresRarity.getByCode(rarity.getCode() + 1);
//						}
						rarity = MagicTreasuresRarity.getByCode(rarity.getCode() + handler.getJewelrySizeTier().getCode());
					}
//					else {
//						// check the material
//						if (handler.getJewelrySizeTier() != JewelrySizeTier.REGULAR && materialRarity.getCode() < 2) {
//							rarity = MagicTreasuresRarity.getByCode(rarity.getCode() + 1);
//						}
//					}
					else {
						rarity = MagicTreasuresRarity.getByCode(rarity.getCode() + handler.getJewelrySizeTier().getCode());
					}
					tag(JEWELRY_RARITY_TAG_MAP.get(rarity)).add(registryItem.get());
				} else {
//					if (materialRarity.getCode() < 2) {
						rarity = MagicTreasuresRarity.getByCode(rarity.getCode() + handler.getJewelrySizeTier().getCode());
//					}
//					if (handler.getJewelrySizeTier() != JewelrySizeTier.REGULAR) {
//						if (materialRarity.getCode() < 2) {
//							rarity = MagicTreasuresRarity.getByCode(rarity.getCode() + 2);
//						} else {
//							rarity = MagicTreasuresRarity.getByCode(rarity.getCode() + 1);
//						}
//					}
					tag(JEWELRY_RARITY_TAG_MAP.get(rarity)).add(registryItem.get());
				}
			});
		});

		// TODO is it better to include the specials in the rarity map and randomly select OR
		// TODO manually include in the loot tables?
		// explicitly categorize special jewelry
		tag(MagicTreasuresTags.Items.JEWELRY_COMMON).add(MagicTreasuresItems.SILBROS_RING_OF_VITALITY.get());
		tag(MagicTreasuresTags.Items.JEWELRY_COMMON).add(MagicTreasuresItems.STRONGMANS_BRACERS.get());
		tag(MagicTreasuresTags.Items.JEWELRY_COMMON).add(MagicTreasuresItems.PEASANTS_FORTUNE.get());

		tag(MagicTreasuresTags.Items.JEWELRY_UNCOMMON).add(MagicTreasuresItems.MALDRITCHS_FIRST_AMULET.get());
		tag(MagicTreasuresTags.Items.JEWELRY_UNCOMMON).add(MagicTreasuresItems.AQUA_RING.get());
		tag(MagicTreasuresTags.Items.JEWELRY_UNCOMMON).add(MagicTreasuresItems.AMULET_OF_DEFENCE.get());

		tag(MagicTreasuresTags.Items.JEWELRY_SCARCE).add(MagicTreasuresItems.JOURNEYMANS_BANDS.get());
		tag(MagicTreasuresTags.Items.JEWELRY_SCARCE).add(MagicTreasuresItems.ADEPHAGIAS_BOUNTY.get());
		tag(MagicTreasuresTags.Items.JEWELRY_SCARCE).add(MagicTreasuresItems.MEDICS_TOKEN.get());

		tag(MagicTreasuresTags.Items.JEWELRY_RARE).add(MagicTreasuresItems.SALANDAARS_WARD.get());
		tag(MagicTreasuresTags.Items.JEWELRY_RARE).add(MagicTreasuresItems.ANGELS_RING.get());
		tag(MagicTreasuresTags.Items.JEWELRY_RARE).add(MagicTreasuresItems.RING_OF_FORTITUDE.get());

		// TODO move to Legendary or mythical ?
		tag(MagicTreasuresTags.Items.JEWELRY_EPIC).add(MagicTreasuresItems.RING_LIFE_DEATH.get());
		tag(MagicTreasuresTags.Items.JEWELRY_EPIC).add(MagicTreasuresItems.EYE_OF_THE_PHOENIX.get());

		tag(MagicTreasuresTags.Items.JEWELRY).addTags(MagicTreasuresTags.Items.RINGS);
		tag(MagicTreasuresTags.Items.JEWELRY).addTags(MagicTreasuresTags.Items.NECKLACES);
		tag(MagicTreasuresTags.Items.JEWELRY).addTags(MagicTreasuresTags.Items.BRACELETS);
//		tag(MagicTreasuresTags.Items.JEWELRY).addTags(MagicTreasuresTags.Items.POCKETS);
//		tag(MagicTreasuresTags.Items.JEWELRY).addTags(MagicTreasuresTags.Items.CHARMS);
		
		// rings - TODO merge with the above code block
//		tag(GealdorCraftTags.Items.RINGS).add(GealdorCraftItems.COPPER_RING.get());
//		tag(GealdorCraftTags.Items.RINGS).add(GealdorCraftItems.TOPAZ_COPPER_RING.get());
//		tag(GealdorCraftTags.Items.RINGS).add(GealdorCraftItems.GREAT_ONYX_COPPER_RING.get());
		
		// stone tiers
		tag(MagicTreasuresTags.Items.STONE_TIER1).add(MagicTreasuresItems.TOPAZ.get());
		tag(MagicTreasuresTags.Items.STONE_TIER2).add(MagicTreasuresItems.ONYX.get());
		tag(MagicTreasuresTags.Items.STONE_TIER3).add(Items.DIAMOND);
		tag(MagicTreasuresTags.Items.STONE_TIER4).add(MagicTreasuresItems.JADEITE.get());
		tag(MagicTreasuresTags.Items.STONE_TIER5).add(MagicTreasuresItems.RUBY.get());
		tag(MagicTreasuresTags.Items.STONE_TIER6).add(MagicTreasuresItems.SAPPHIRE.get());
		tag(MagicTreasuresTags.Items.STONE_TIER5).add(MagicTreasuresItems.WHITE_PEARL.get());
		tag(MagicTreasuresTags.Items.STONE_TIER6).add(MagicTreasuresItems.BLACK_PEARL.get());

		tag(MagicTreasuresTags.Items.STONE_RARITY_COMMON).add(MagicTreasuresItems.TOPAZ.get());
		tag(MagicTreasuresTags.Items.STONE_RARITY_UNCOMMON).add(MagicTreasuresItems.ONYX.get());
		tag(MagicTreasuresTags.Items.STONE_RARITY_SCARCE).add(Items.DIAMOND);
		tag(MagicTreasuresTags.Items.STONE_RARITY_SCARCE).add(MagicTreasuresItems.JADEITE.get());
		tag(MagicTreasuresTags.Items.STONE_RARITY_RARE).add(MagicTreasuresItems.RUBY.get());
		tag(MagicTreasuresTags.Items.STONE_RARITY_EPIC).add(MagicTreasuresItems.SAPPHIRE.get());
		tag(MagicTreasuresTags.Items.STONE_RARITY_RARE).add(MagicTreasuresItems.WHITE_PEARL.get());
		tag(MagicTreasuresTags.Items.STONE_RARITY_EPIC).add(MagicTreasuresItems.BLACK_PEARL.get());

		// TODO need to add a different way because if one tier doesn't have items, it won't be generated
		// and this will fail. AND probably should list out each explicitly as tier and stones server
		// different purposes.
		tag(MagicTreasuresTags.Items.STONES).addTags(
				MagicTreasuresTags.Items.STONE_TIER1,
				MagicTreasuresTags.Items.STONE_TIER2,
				MagicTreasuresTags.Items.STONE_TIER3,
				MagicTreasuresTags.Items.STONE_TIER4,
				MagicTreasuresTags.Items.STONE_TIER5,
				MagicTreasuresTags.Items.STONE_TIER6);

		// recharging stones
		tag(MagicTreasuresTags.Items.RECHARGERS).add(Items.AMETHYST_SHARD);
		tag(MagicTreasuresTags.Items.RECHARGERS).add(Items.EMERALD);
		tag(MagicTreasuresTags.Items.RECHARGERS).add(MagicTreasuresItems.RECHARGE_SCROLL.get());

		Registration.ITEMS.getEntries().forEach(o -> {
			if (o.get() instanceof SpellScroll) {
				tag(MagicTreasuresTags.Items.SPELL_SCROLLS).add(o.get());
			}
		});

		// belts
		tag(MagicTreasuresTags.Items.CURIOUS_BELT).add(MagicTreasuresItems.SKULL_BELT.get());

		// ores
		tag(MagicTreasuresTags.Items.ORE_SILVER).add(MagicTreasuresItems.SILVER_ORE_BLOCKITEM.get());
		tag(MagicTreasuresTags.Items.ORE_TOPAZ).add(MagicTreasuresItems.TOPAZ_ORE.get());
		tag(MagicTreasuresTags.Items.ORE_ONYX).add(MagicTreasuresItems.ONYX_ORE.get());
		tag(MagicTreasuresTags.Items.ORE_JADE).add(MagicTreasuresItems.JADEITE_ORE.get());
		tag(MagicTreasuresTags.Items.ORE_JADEITE).add(MagicTreasuresItems.JADEITE_ORE.get());
		tag(MagicTreasuresTags.Items.ORE_RUBY).add(MagicTreasuresItems.RUBY_ORE.get());
		tag(MagicTreasuresTags.Items.ORE_SAPPHIRE).add(MagicTreasuresItems.SAPPHIRE_ORE.get());

		// gems
		tag(MagicTreasuresTags.Items.GEMS_TOPAZ).add(MagicTreasuresItems.TOPAZ.get());
		tag(MagicTreasuresTags.Items.GEMS_ONYX).add(MagicTreasuresItems.ONYX.get());
		tag(MagicTreasuresTags.Items.GEMS_JADE).add(MagicTreasuresItems.JADEITE.get());
		tag(MagicTreasuresTags.Items.GEMS_JADEITE).add(MagicTreasuresItems.JADEITE.get());
		tag(MagicTreasuresTags.Items.GEMS_RUBY).add(MagicTreasuresItems.RUBY.get());
		tag(MagicTreasuresTags.Items.GEMS_SAPPHIRE).add(MagicTreasuresItems.SAPPHIRE.get());
		tag(MagicTreasuresTags.Items.GEMS_PEARL).add(MagicTreasuresItems.WHITE_PEARL.get());
		tag(MagicTreasuresTags.Items.GEMS_PEARL).add(MagicTreasuresItems.BLACK_PEARL.get());

		// ingots
		tag(MagicTreasuresTags.Items.INGOTS_SILVER).add(MagicTreasuresItems.SILVER_INGOT.get());

		// raw materials
		tag(MagicTreasuresTags.Items.RAW_SILVER).add(MagicTreasuresItems.RAW_SILVER.get());

		// special jewelry tags
		tag(MagicTreasuresTags.Items.CASTLE_RING_RUBY).add(MagicTreasuresItems.RUBY.get());
		tag(MagicTreasuresTags.Items.CASTLE_RING_SAPPHIRE).add(MagicTreasuresItems.SAPPHIRE.get());

		// //////////////////////////////////////////////
		// treasure2 integration
		// //////////////////////////////////////////////

		// add treasure2 gems to mt stone tiers
		tag(MagicTreasuresTags.Items.STONE_TIER1).addOptional(TreasureItems.TOPAZ.getId());
		tag(MagicTreasuresTags.Items.STONE_TIER2).addOptional(TreasureItems.ONYX.getId());
		tag(MagicTreasuresTags.Items.STONE_TIER5).addOptional(TreasureItems.RUBY.getId());
		tag(MagicTreasuresTags.Items.STONE_TIER6).addOptional(TreasureItems.SAPPHIRE.getId());
		tag(MagicTreasuresTags.Items.STONE_TIER5).addOptional(TreasureItems.WHITE_PEARL.getId());
		tag(MagicTreasuresTags.Items.STONE_TIER6).addOptional(TreasureItems.BLACK_PEARL.getId());

		// add treasure2 gems to mt stone rarities
		tag(MagicTreasuresTags.Items.STONE_RARITY_COMMON).addOptional(TreasureItems.TOPAZ.getId());
		tag(MagicTreasuresTags.Items.STONE_RARITY_UNCOMMON).addOptional(TreasureItems.ONYX.getId());
		tag(MagicTreasuresTags.Items.STONE_RARITY_RARE).addOptional(TreasureItems.RUBY.getId());
		tag(MagicTreasuresTags.Items.STONE_RARITY_EPIC).addOptional(TreasureItems.SAPPHIRE.getId());
		tag(MagicTreasuresTags.Items.STONE_RARITY_RARE).addOptional(TreasureItems.WHITE_PEARL.getId());
		tag(MagicTreasuresTags.Items.STONE_RARITY_EPIC).addOptional(TreasureItems.BLACK_PEARL.getId());

		// add treasure2 gems mt special jewelry tags
		tag(MagicTreasuresTags.Items.CASTLE_RING_RUBY).addOptional(TreasureItems.RUBY.getId());
		tag(MagicTreasuresTags.Items.CASTLE_RING_SAPPHIRE).addOptional(TreasureItems.SAPPHIRE.getId());

		// wishables
		tag(TreasureTags.Items.SCARCE_WISHABLE).add(MagicTreasuresItems.TOPAZ.get());
		tag(TreasureTags.Items.SCARCE_WISHABLE).add(MagicTreasuresItems.ONYX.get());
		tag(TreasureTags.Items.SCARCE_WISHABLE).add(MagicTreasuresItems.JADEITE.get());
		tag(TreasureTags.Items.RARE_WISHABLE).add(MagicTreasuresItems.RUBY.get());
		tag(TreasureTags.Items.RARE_WISHABLE).add(MagicTreasuresItems.WHITE_PEARL.get());
		tag(TreasureTags.Items.EPIC_WISHABLE).add(MagicTreasuresItems.SAPPHIRE.get());
		tag(TreasureTags.Items.EPIC_WISHABLE).add(MagicTreasuresItems.BLACK_PEARL.get());

		// pouch
		tag(TreasureTags.Items.POUCH).addTag(MagicTreasuresTags.Items.STONES);
		tag(TreasureTags.Items.POUCH).addTag(MagicTreasuresTags.Items.RECHARGERS);
		tag(TreasureTags.Items.POUCH).addTag(MagicTreasuresTags.Items.JEWELRY);

		// /////////////////
		// diamethysts integration
		// //////////////////

		tag(MagicTreasuresTags.Items.RECHARGERS).addOptional(new ResourceLocation("diamethysts:diamethyst_shard"));
		tag(MagicTreasuresTags.Items.RECHARGERS).addOptional(new ResourceLocation("diamethysts:diamethyst_crystal"));
	}
}
