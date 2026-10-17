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
package mod.gottsch.forge.magic_treasures.core.tag;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.forge.magic_treasures.core.item.IJewelryType;
import mod.gottsch.forge.magic_treasures.core.item.JewelryType;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryStoneTier;
import mod.gottsch.forge.magic_treasures.core.registry.JewelryRegistry;
import mod.gottsch.forge.magic_treasures.core.registry.StoneRegistry;
import mod.gottsch.forge.magic_treasures.core.registry.TagRegistry;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Mark Gottschling on 5/29/2023
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MagicTreasuresTags {
    private static final String CURIOS_MODID = "curios";
    private static final String FORGE_MODID = "forge";

    public static class Items {
        // curios integration
        public static final TagKey<Item> CURIOUS_NECKLACE = mod(CURIOS_MODID, JewelryType.NECKLACE.getValue());
        public static final TagKey<Item> CURIOUS_RING = mod(CURIOS_MODID, JewelryType.RING.getValue());
        public static final TagKey<Item> CURIOUS_BRACELET = mod(CURIOS_MODID, JewelryType.BRACELET.getValue());
//        public static final TagKey<Item> CURIOUS_CHARM = mod(CURIOS_MODID, "charm");
        public static final TagKey<Item> CURIOUS_BELT = mod(CURIOS_MODID, JewelryType.BELT.getValue());

        // jewelry
        public static final TagKey<Item> RINGS = mod(MagicTreasures.MOD_ID, "jewelry/rings");
        public static final TagKey<Item> BRACELETS = mod(MagicTreasures.MOD_ID, "jewelry/bracelets");
        public static final TagKey<Item> NECKLACES = mod(MagicTreasures.MOD_ID, "jewelry/necklaces");
//        public static final TagKey<Item> CHARMS = mod(MagicTreasures.MOD_ID, "jewelry/charms");
        public static final TagKey<Item> POCKETS = mod(MagicTreasures.MOD_ID, "jewelry/pockets");
        public static final TagKey<Item> JEWELRY = mod(MagicTreasures.MOD_ID, "jewelry/all_jewelry");

        // other
        public static final TagKey<Item> STONE_REMOVAL_TOOLS = mod(MagicTreasures.MOD_ID, "tools/stone_removal");

        // categorization by tier
        public static final TagKey<Item> WOOD = mod(MagicTreasures.MOD_ID, "jewelry/tiers/materials/wood");
        public static final TagKey<Item> IRON = mod(MagicTreasures.MOD_ID, "jewelry/tiers/materials/iron");
        public static final TagKey<Item> COPPER = mod(MagicTreasures.MOD_ID, "jewelry/tiers/materials/copper");
        public static final TagKey<Item> SILVER = mod(MagicTreasures.MOD_ID, "jewelry/tiers/materials/silver");
        public static final TagKey<Item> GOLD = mod(MagicTreasures.MOD_ID, "jewelry/tiers/materials/gold");
        public static final TagKey<Item> BLOOD = mod(MagicTreasures.MOD_ID, "jewelry/tiers/materials/blood");
        public static final TagKey<Item> BONE = mod(MagicTreasures.MOD_ID, "jewelry/tiers/materials/bone");
        public static final TagKey<Item> SHADOW = mod(MagicTreasures.MOD_ID, "jewelry/tiers/materials/shadow");
        public static final TagKey<Item> ATIUM = mod(MagicTreasures.MOD_ID, "jewelry/tiers/materials/atium");
        
        public static final TagKey<Item> REGULAR = mod(MagicTreasures.MOD_ID, "jewelry/tiers/sizes/regular");
        public static final TagKey<Item> GREAT = mod(MagicTreasures.MOD_ID, "jewelry/tiers/sizes/great");
        public static final TagKey<Item> LORDS = mod(MagicTreasures.MOD_ID, "jewelry/tiers/sizes/lords");

        public static final TagKey<Item> JEWELRY_COMMON = mod(MagicTreasures.MOD_ID, "jewelry/rarity/common");
        public static final TagKey<Item> JEWELRY_UNCOMMON = mod(MagicTreasures.MOD_ID, "jewelry/rarity/uncommon");
        public static final TagKey<Item> JEWELRY_SCARCE = mod(MagicTreasures.MOD_ID, "jewelry/rarity/scarce");
        public static final TagKey<Item> JEWELRY_RARE = mod(MagicTreasures.MOD_ID, "jewelry/rarity/rare");
        public static final TagKey<Item> JEWELRY_EPIC = mod(MagicTreasures.MOD_ID, "jewelry/rarity/epic");
        public static final TagKey<Item> JEWELRY_LEGENDARY = mod(MagicTreasures.MOD_ID, "jewelry/rarity/legendary");
        public static final TagKey<Item> JEWELRY_MYTHICAL = mod(MagicTreasures.MOD_ID, "jewelry/rarity/mythical");

        // stone tiers
        public static final TagKey<Item> STONE_TIER1 = mod(MagicTreasures.MOD_ID, "jewelry/stones/tier/tier1");
        public static final TagKey<Item> STONE_TIER2 = mod(MagicTreasures.MOD_ID, "jewelry/stones/tier/tier2");
        public static final TagKey<Item> STONE_TIER3 = mod(MagicTreasures.MOD_ID, "jewelry/stones/tier/tier3");
        public static final TagKey<Item> STONE_TIER4 = mod(MagicTreasures.MOD_ID, "jewelry/stones/tier/tier4");
        public static final TagKey<Item> STONE_TIER5 = mod(MagicTreasures.MOD_ID, "jewelry/stones/tier/tier5");
        public static final TagKey<Item> STONE_TIER6 = mod(MagicTreasures.MOD_ID, "jewelry/stones/tier/tier6");
        public static final TagKey<Item> STONE_TIER_SKELETONS_HEART = mod(MagicTreasures.MOD_ID, "jewelry/stones/tier/skeletons_heart");

        // NOTE stone rarity is loosely equivalent to tier.
        public static final TagKey<Item> STONE_RARITY_COMMON = mod(MagicTreasures.MOD_ID, "jewelry/stones/rarity/common");
        public static final TagKey<Item> STONE_RARITY_UNCOMMON = mod(MagicTreasures.MOD_ID, "jewelry/stones/rarity/uncommon");
        public static final TagKey<Item> STONE_RARITY_SCARCE = mod(MagicTreasures.MOD_ID, "jewelry/stones/rarity/scarce");
        public static final TagKey<Item> STONE_RARITY_RARE = mod(MagicTreasures.MOD_ID, "jewelry/stones/rarity/rare");
        public static final TagKey<Item> STONE_RARITY_EPIC = mod(MagicTreasures.MOD_ID, "jewelry/stones/rarity/epic");

        public static final TagKey<Item> STONES = mod(MagicTreasures.MOD_ID, "jewelry/stones/all_stones");
        public static final TagKey<Item> RECHARGERS = mod(MagicTreasures.MOD_ID, "jewelry/stones/rechargers");


        public static final TagKey<Item> CASTLE_RING_RUBY = mod(MagicTreasures.MOD_ID, "jewelry/castle_ring/ruby");
        public static final TagKey<Item> CASTLE_RING_SAPPHIRE = mod(MagicTreasures.MOD_ID, "jewelry/castle_ring/sapphire");

        public static final TagKey<Item> SPELL_SCROLLS = mod(MagicTreasures.MOD_ID, "scrolls/spells");

        // /////
        // forge integration tags
        // /////

        // ores
        public static final TagKey<Item> ORE_SILVER = mod(FORGE_MODID, "ores/silver");
        public static final TagKey<Item> ORE_TOPAZ = mod(FORGE_MODID, "ores/topaz");
        public static final TagKey<Item> ORE_ONYX = mod(FORGE_MODID, "ores/onyx");
        public static final TagKey<Item> ORE_JADE = mod(FORGE_MODID, "ores/jade");
        public static final TagKey<Item> ORE_JADEITE = mod(FORGE_MODID, "ores/jadeite");
        public static final TagKey<Item> ORE_RUBY = mod(FORGE_MODID, "ores/ruby");
        public static final TagKey<Item> ORE_SAPPHIRE = mod(FORGE_MODID, "ores/sapphire");

        // raw metal

        // gems
        public static final TagKey<Item> GEMS_TOPAZ = mod(FORGE_MODID, "gems/topaz");
        public static final TagKey<Item> GEMS_ONYX = mod(FORGE_MODID, "gems/onyx");
        public static final TagKey<Item> GEMS_JADE = mod(FORGE_MODID, "gems/jade");
        public static final TagKey<Item> GEMS_JADEITE = mod(FORGE_MODID, "gems/jadeite");
        public static final TagKey<Item> GEMS_RUBY = mod(FORGE_MODID, "gems/ruby");
        public static final TagKey<Item> GEMS_SAPPHIRE = mod(FORGE_MODID, "gems/sapphire");
        public static final TagKey<Item> GEMS_PEARL = mod(FORGE_MODID, "gems/pearl");

        // ingots
        public static final TagKey<Item> INGOTS_SILVER = mod(FORGE_MODID, "ingots/silver");

        // raw materials
        public static final TagKey<Item> RAW_SILVER = mod(FORGE_MODID, "raw_materials/silver");

        /**
         *
         * @param domain
         * @param path
         * @return
         */
        public static TagKey<Item> mod(String domain, String path) {
            return ItemTags.create(new ResourceLocation(domain, path));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> ALL_OVERWORLD = mod(MagicTreasures.MOD_ID, "all_overworld");

        public static TagKey<Biome> mod(String domain, String path) {
            return TagKey.create(Registries.BIOME, new ResourceLocation(domain, path));
        }
    }
    
	@SubscribeEvent
	public static void registerTags(TagsUpdatedEvent event) {
		MagicTreasures.LOGGER.info("in tags updated event");
		
		// clear any registries
		JewelryRegistry.clear();

        /*
         * register all jewelry items by looking up the jewelry type tags.
         */
		// process all items in the JewelryType tags
        List<IJewelryType> types = MagicTreasuresApi.getJewelryTypes();
        types.forEach(type -> {
        	TagKey<Item> tagKey = TagRegistry.getJewelryTypeTag(type);
			if (tagKey != null) {
				// get the tag
				ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
				// for each item in the tag
				for (Iterator<Item> iterator = tag.iterator(); iterator.hasNext();) {
					Item jewelry = iterator.next();
					JewelryRegistry.register(jewelry);
					MagicTreasures.LOGGER.debug("registering jewelry -> {} ", ModUtil.getName(jewelry));
				}
			}
        });

        // process all items in the JewelryStoneTier tags
        List<JewelryStoneTier> tiers = MagicTreasuresApi.getJewelryStoneTiers();
        tiers.forEach(tier -> {
            TagKey<Item> tagKey = TagRegistry.getJewelryStoneTierTag(tier);
            if (tagKey != null) {
                // get the tag
                ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
                // for each item in the tag
                for (Item stone : tag) {
                    // register the stone with the StoneRegistry
                    StoneRegistry.register(stone);
                    // register the stone with the tier in the StoneRegistry
                    StoneRegistry.register(stone, tier);
                    MagicTreasures.LOGGER.debug("registering stone to tier -> {} <--> {} ", ModUtil.getName(stone), tier.getName());
                }
            }
        });

        // registry stones by rarity
        List<IRarity> rarities = MagicTreasuresApi.getRarities();
        rarities.forEach(rarity -> {
            TagKey<Item> tagKey = TagRegistry.getStoneRarityTag(rarity);
            if (tagKey != null) {
                ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
                for (Item stone : tag) {
                    StoneRegistry.register(rarity, stone);
                    MagicTreasures.LOGGER.debug("registering stone to rarity -> {} <--> {} ", ModUtil.getName(stone), rarity.getName());
                }
            }
            // register jewelry by rarity
            MagicTreasuresApi.getJewelryRarityTag(rarity)
                    .ifPresent(key -> {
                        ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(key);
                        tag.forEach(item -> {
                            JewelryRegistry.register(rarity, item);
                            MagicTreasures.LOGGER.debug("registering jewelry to rarity -> {} <--> {} ", ModUtil.getName(item), rarity.getName());
                        });
                    });
        });
	}
}
