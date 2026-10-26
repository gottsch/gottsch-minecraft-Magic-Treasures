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

import net.minecraft.tags.ItemTags;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.LegacyConversion;
import mod.gottsch.forge.magic_treasures.core.item.GenericJewelry;
import mod.gottsch.forge.magic_treasures.core.item.Jewelry;
import java.util.Optional;
import com.google.common.collect.Maps;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.item.*;
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


		// set weapons: the vanilla swords tag (1.20.1 picks armor and shield enchantments by item class, so no armor tags)
		tag(ItemTags.SWORDS).add(MagicTreasuresItems.ROOTSTAFF.get(), MagicTreasuresItems.STONEMARK_HAMMER.get(),
				MagicTreasuresItems.BONE_SCYTHE.get(),
				MagicTreasuresItems.MOONGLAIVE.get());

		// jewelry tools
		tag(MagicTreasuresTags.Items.STONE_REMOVAL_TOOLS)
				// shears only: the anvil uses up the tool, so no axes
				.addTag(Tags.Items.SHEARS);

		// M9: the 1.x per-item tags (type, material, size, rarity) went with the 1.x items; their Curios slot tags are
		// kept (below). Generic jewelry is in
		// the type tags, so all_jewelry (anvil repair, Treasure2 pouch, the root advancement) holds it
		tag(MagicTreasuresTags.Items.RINGS).add(MagicTreasuresItems.RING.get());
		tag(MagicTreasuresTags.Items.NECKLACES).add(MagicTreasuresItems.NECKLACE.get());
		tag(MagicTreasuresTags.Items.BRACELETS).add(MagicTreasuresItems.BRACELET.get());
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

		// generic jewelry's Curios slots
		tag(MagicTreasuresTags.Items.CURIOUS_RING).add(MagicTreasuresItems.RING.get());
		tag(MagicTreasuresTags.Items.CURIOUS_NECKLACE).add(MagicTreasuresItems.NECKLACE.get());
		tag(MagicTreasuresTags.Items.CURIOUS_BRACELET).add(MagicTreasuresItems.BRACELET.get());

		// the 1.x items keep their Curios slots, so a piece worn in 1.x stays in its slot and converts there. Without
		// them, Curios moves it out (into the inventory, or onto the ground if that's full) when the player loads,
		// a second before the conversion runs. Optional, so the tags still load once the legacy ids are dropped.
		Map<String, TagKey<Item>> curiosSlots = Map.of(
				"ring", MagicTreasuresTags.Items.CURIOUS_RING,
				"necklace", MagicTreasuresTags.Items.CURIOUS_NECKLACE,
				"bracelet", MagicTreasuresTags.Items.CURIOUS_BRACELET);
		LegacyConversion.idsByType().forEach((type, ids) -> {
			TagKey<Item> slot = curiosSlots.get(type);
			if (slot == null) {
				throw new IllegalStateException("legacy jewelry type with no Curios slot: " + type);
			}
			ids.forEach(id -> tag(slot).addOptional(id));
		});

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
