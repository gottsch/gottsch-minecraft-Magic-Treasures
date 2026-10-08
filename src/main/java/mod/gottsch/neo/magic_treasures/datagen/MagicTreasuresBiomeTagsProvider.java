
package mod.gottsch.neo.magic_treasures.datagen;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.tag.MagicTreasuresTags;
import mod.gottsch.neoforge.treasure2.core.tags.TreasureTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * 
 * @author Mark Gottschling on July 10, 2024
 *
 */
public class MagicTreasuresBiomeTagsProvider extends BiomeTagsProvider {
    /**
     *
     * @param existingFileHelper
     */
	public MagicTreasuresBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper existingFileHelper) {
        super(output, lookup, MagicTreasures.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        String BOP = "biomesoplenty";
        String BWG = "biomeswevegone";

    	// blocks rarity
//    	tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.BADLANDS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.BAMBOO_JUNGLE);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.BASALT_DELTAS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.BIRCH_FOREST);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.BEACH);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.CRIMSON_FOREST);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.DARK_FOREST);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.DESERT);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.DRIPSTONE_CAVES);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.FOREST);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.FLOWER_FOREST);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.FROZEN_PEAKS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.FROZEN_RIVER);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.GROVE);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.ICE_SPIKES);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.JUNGLE);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.JAGGED_PEAKS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.LUSH_CAVES);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.MEADOW);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.MUSHROOM_FIELDS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.OLD_GROWTH_BIRCH_FOREST);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.OLD_GROWTH_PINE_TAIGA);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.OLD_GROWTH_SPRUCE_TAIGA);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.PLAINS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.RIVER);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.SAVANNA);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.SAVANNA_PLATEAU);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.SNOWY_PLAINS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.SNOWY_SLOPES);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.SNOWY_TAIGA);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.SPARSE_JUNGLE);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.STONY_PEAKS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.SUNFLOWER_PLAINS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.SWAMP);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.TAIGA);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.WINDSWEPT_FOREST);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.WINDSWEPT_SAVANNA);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.WINDSWEPT_HILLS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.WINDSWEPT_GRAVELLY_HILLS);
//        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).add(Biomes.WOODED_BADLANDS);
        tag(TreasureTags.Biomes.ALL_OVERWORLD).addTag(BiomeTags.IS_OVERWORLD);

        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "aspen_glade"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "auroral_garden"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "bayou"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "bog"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "clover_patch"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "cold_desert"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "coniferous_forest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "crag"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "crystalline_chasm"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "dead_forest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "dryland"));

        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "dune_beach"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "end_wilds"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "end_reef"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "end_corruption"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "erupting_inferno"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "field"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "fir_clearing"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "floodplain"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "forested_field"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP,  "fungal_jungle"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP,  "glowing_grotto"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "grassland"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "gravel_beach"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "highland"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "hot_springs"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "jacaranda_glade"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "jade_cliffs"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "lavender_forest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "lush_desert"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "lush_savanna"));

        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "maple_woods"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "marsh"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP,  "mediterranean_forest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "moor"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "muskeg"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "mystic_grove"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "old_growth_dead_forest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "old_growth_woodland"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "ominous_woods"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "orchard"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "origin_valley"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "pasture"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "prairie"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "pumpkin_patch"));

        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "rainforest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "redwood_forest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "rocky_rainforest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "rocky_shrubland"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "scrubland"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "seasonal_forest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "shrubland"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP,  "snowblossom_grove"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "snowy_coniferous_forest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "snowy_fir_clearing"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "snowy_maple_woods"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "spider_nest"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "tropics"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "tundra"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "undergrowth"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "visceral_heap"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "volcano"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "volcanic_plains"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "wasteland"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "wasteland_steppe"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "wetland"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "wintry_origin_valley"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "withered_abyss"));
        tag(MagicTreasuresTags.Biomes.ALL_OVERWORLD).addOptional(new ResourceLocation(BOP, "woodland"));

        // BWG
        tag(TreasureTags.Biomes.ALL_OVERWORLD).addOptionalTag(new ResourceLocation(BWG, "overworld"));

    }
}
