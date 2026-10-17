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
package mod.gottsch.forge.magic_treasures.core.setup;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.forge.magic_treasures.core.config.Config;
import mod.gottsch.forge.magic_treasures.core.integration.MagicTreasuresIntegrations;
import mod.gottsch.forge.magic_treasures.core.item.JewelryType;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryMaterials;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryStoneTiers;
import mod.gottsch.forge.magic_treasures.core.loot.MagicTreasuresLootFunctions;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.forge.magic_treasures.core.tag.MagicTreasuresTags;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark Gottschling on 5/3/2023
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {

    /**
     *
     * @param event
     */
    public static void init(final FMLCommonSetupEvent event) {
        // add mod specific logging
        Config.instance.addRollingFileAppender(MagicTreasures.MOD_ID);

        // register rarities
        MagicTreasuresApi.registerRarity(MagicTreasuresRarity.COMMON);
        MagicTreasuresApi.registerRarity(MagicTreasuresRarity.UNCOMMON);
        MagicTreasuresApi.registerRarity(MagicTreasuresRarity.SCARCE);
        MagicTreasuresApi.registerRarity(MagicTreasuresRarity.RARE);
        MagicTreasuresApi.registerRarity(MagicTreasuresRarity.EPIC);
        MagicTreasuresApi.registerRarity(MagicTreasuresRarity.LEGENDARY);
        MagicTreasuresApi.registerRarity(MagicTreasuresRarity.MYTHICAL);

        // register jewelry types
        MagicTreasuresApi.registerJewelryType(JewelryType.RING);
        MagicTreasuresApi.registerJewelryType(JewelryType.NECKLACE);
        MagicTreasuresApi.registerJewelryType(JewelryType.BRACELET);
        MagicTreasuresApi.registerJewelryType(JewelryType.POCKET);
//        MagicTreasuresApi.registerJewelryType(JewelryType.CHARM);
        // TODO add Broach ?

        // register jewelry size tiers
        MagicTreasuresApi.registerJewelrySize(JewelrySizeTier.REGULAR);
        MagicTreasuresApi.registerJewelrySize(JewelrySizeTier.GREAT);
        MagicTreasuresApi.registerJewelrySize(JewelrySizeTier.LORDS);

        // register jewelry stone tiers
        MagicTreasuresApi.registerJewelryStoneTier(JewelryStoneTiers.TIER1);
        MagicTreasuresApi.registerJewelryStoneTier(JewelryStoneTiers.TIER2);
        MagicTreasuresApi.registerJewelryStoneTier(JewelryStoneTiers.TIER3);
        MagicTreasuresApi.registerJewelryStoneTier(JewelryStoneTiers.TIER4);
        MagicTreasuresApi.registerJewelryStoneTier(JewelryStoneTiers.TIER5);
        MagicTreasuresApi.registerJewelryStoneTier(JewelryStoneTiers.TIER6);
        MagicTreasuresApi.registerJewelryStoneTier(JewelryStoneTiers.SKELETONS_HEART);

        // register jewelry material tiers
        MagicTreasuresApi.registerJewelryMaterial(JewelryMaterials.WOOD);
        MagicTreasuresApi.registerJewelryMaterial(JewelryMaterials.IRON);
        MagicTreasuresApi.registerJewelryMaterial(JewelryMaterials.COPPER);
        MagicTreasuresApi.registerJewelryMaterial(JewelryMaterials.SILVER);
        MagicTreasuresApi.registerJewelryMaterial(JewelryMaterials.GOLD);
        MagicTreasuresApi.registerJewelryMaterial(JewelryMaterials.BLOOD);
        MagicTreasuresApi.registerJewelryMaterial(JewelryMaterials.BONE);
        MagicTreasuresApi.registerJewelryMaterial(JewelryMaterials.SHADOW);
        MagicTreasuresApi.registerJewelryMaterial(JewelryMaterials.ATIUM);

        // register jewelry rarity tags
        MagicTreasuresApi.registerJewelryRarityTag(MagicTreasuresRarity.COMMON, MagicTreasuresTags.Items.JEWELRY_COMMON);
        MagicTreasuresApi.registerJewelryRarityTag(MagicTreasuresRarity.UNCOMMON, MagicTreasuresTags.Items.JEWELRY_UNCOMMON);
        MagicTreasuresApi.registerJewelryRarityTag(MagicTreasuresRarity.SCARCE, MagicTreasuresTags.Items.JEWELRY_SCARCE);
        MagicTreasuresApi.registerJewelryRarityTag(MagicTreasuresRarity.RARE, MagicTreasuresTags.Items.JEWELRY_RARE);
        MagicTreasuresApi.registerJewelryRarityTag(MagicTreasuresRarity.EPIC, MagicTreasuresTags.Items.JEWELRY_EPIC);
        MagicTreasuresApi.registerJewelryRarityTag(MagicTreasuresRarity.LEGENDARY, MagicTreasuresTags.Items.JEWELRY_LEGENDARY);
        MagicTreasuresApi.registerJewelryRarityTag(MagicTreasuresRarity.MYTHICAL, MagicTreasuresTags.Items.JEWELRY_MYTHICAL);

        // register stone rarity tags
        MagicTreasuresApi.registerStoneRarityTag(MagicTreasuresRarity.COMMON, MagicTreasuresTags.Items.STONE_RARITY_COMMON);
        MagicTreasuresApi.registerStoneRarityTag(MagicTreasuresRarity.UNCOMMON, MagicTreasuresTags.Items.STONE_RARITY_UNCOMMON);
        MagicTreasuresApi.registerStoneRarityTag(MagicTreasuresRarity.SCARCE, MagicTreasuresTags.Items.STONE_RARITY_SCARCE);
        MagicTreasuresApi.registerStoneRarityTag(MagicTreasuresRarity.RARE, MagicTreasuresTags.Items.STONE_RARITY_RARE);
        MagicTreasuresApi.registerStoneRarityTag(MagicTreasuresRarity.EPIC, MagicTreasuresTags.Items.STONE_RARITY_EPIC);

        // register type tags (TODO this could be a stream of all JewelryTypes ?)
        MagicTreasuresApi.registerJewerlyTypeTag(JewelryType.RING, MagicTreasuresTags.Items.RINGS);
        MagicTreasuresApi.registerJewerlyTypeTag(JewelryType.BRACELET, MagicTreasuresTags.Items.BRACELETS);
        MagicTreasuresApi.registerJewerlyTypeTag(JewelryType.NECKLACE, MagicTreasuresTags.Items.NECKLACES);
        MagicTreasuresApi.registerJewerlyTypeTag(JewelryType.POCKET, MagicTreasuresTags.Items.POCKETS);
//        MagicTreasuresApi.registerJewerlyTypeTag(JewelryType.CHARM, MagicTreasuresTags.Items.CHARMS);
        
        // register stone tier tags
        MagicTreasuresApi.registerJewerlyStoneTierTag(JewelryStoneTiers.TIER1, MagicTreasuresTags.Items.STONE_TIER1);
        MagicTreasuresApi.registerJewerlyStoneTierTag(JewelryStoneTiers.TIER2, MagicTreasuresTags.Items.STONE_TIER2);
        MagicTreasuresApi.registerJewerlyStoneTierTag(JewelryStoneTiers.TIER3, MagicTreasuresTags.Items.STONE_TIER3);
        MagicTreasuresApi.registerJewerlyStoneTierTag(JewelryStoneTiers.TIER4, MagicTreasuresTags.Items.STONE_TIER4);
        MagicTreasuresApi.registerJewerlyStoneTierTag(JewelryStoneTiers.TIER5, MagicTreasuresTags.Items.STONE_TIER5);
        MagicTreasuresApi.registerJewerlyStoneTierTag(JewelryStoneTiers.TIER6, MagicTreasuresTags.Items.STONE_TIER6);
        MagicTreasuresApi.registerJewerlyStoneTierTag(JewelryStoneTiers.SKELETONS_HEART, MagicTreasuresTags.Items.STONE_TIER_SKELETONS_HEART);

        // map stone items -> handlers
//        MagicTreasuresApi.registerJewelryStoneHandler(Items.DIAMOND, JewelryStoneHandlers.STANDARD);

        // loot functions
        // NOTE are in common setup because there are not deferred, but registered directly into the vanilla registry
        MagicTreasuresLootFunctions.register();

        // treasure2 integration (needs to be registered BEFORE LevelEvent.Load)
        MagicTreasuresIntegrations.registerTreasure2Integration();

        // curios integration
        MagicTreasuresIntegrations.registerCuriosIntegration();

     }
}
