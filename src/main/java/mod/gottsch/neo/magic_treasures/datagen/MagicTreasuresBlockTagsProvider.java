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

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.block.MagicTreasuresBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;


public class MagicTreasuresBlockTagsProvider extends BlockTagsProvider {

    /**
     * 
     */
	public MagicTreasuresBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                           ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MagicTreasures.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
    	// blocks rarity
//    	tag(TreasureTags.Blocks.COMMON_CHESTS).add(TreasureBlocks.WOOD_CHEST.get());

        // ores needs pickaxe
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.JADEITE_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.ONYX_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.TOPAZ_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.RUBY_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.SAPPHIRE_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.SILVER_ORE.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.DEEPSLATE_JADEITE_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.DEEPSLATE_ONYX_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.DEEPSLATE_TOPAZ_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.DEEPSLATE_RUBY_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.DEEPSLATE_SAPPHIRE_ORE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MagicTreasuresBlocks.DEEPSLATE_SILVER_ORE.get());

        // ore needs stone tool
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(MagicTreasuresBlocks.SILVER_ORE.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(MagicTreasuresBlocks.DEEPSLATE_SILVER_ORE.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.TOPAZ_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.ONYX_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.JADEITE_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.RUBY_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.SAPPHIRE_ORE.get());
        // ore needs iron tool
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.DEEPSLATE_TOPAZ_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.DEEPSLATE_ONYX_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.DEEPSLATE_JADEITE_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.DEEPSLATE_RUBY_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(MagicTreasuresBlocks.DEEPSLATE_SAPPHIRE_ORE.get());
    }
}
