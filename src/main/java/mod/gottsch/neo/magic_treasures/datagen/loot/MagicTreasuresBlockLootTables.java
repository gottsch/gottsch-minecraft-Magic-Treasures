/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2024 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.magic_treasures.datagen.loot;

import mod.gottsch.neo.magic_treasures.core.block.MagicTreasuresBlocks;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Set;

/**
 *
 * @author Mark Gottschling on Sept 1, 2024
 *
 */
public class MagicTreasuresBlockLootTables extends BlockLootSubProvider {

    public MagicTreasuresBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {


        this.add(MagicTreasuresBlocks.DEEPSLATE_JADEITE_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.DEEPSLATE_JADEITE_ORE.get()));
        this.add(MagicTreasuresBlocks.DEEPSLATE_ONYX_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.DEEPSLATE_ONYX_ORE.get()));
        this.add(MagicTreasuresBlocks.DEEPSLATE_RUBY_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.DEEPSLATE_RUBY_ORE.get()));
        this.add(MagicTreasuresBlocks.DEEPSLATE_SAPPHIRE_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.DEEPSLATE_SAPPHIRE_ORE.get()));
        this.add(MagicTreasuresBlocks.DEEPSLATE_TOPAZ_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.DEEPSLATE_TOPAZ_ORE.get()));

        this.add(MagicTreasuresBlocks.JADEITE_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.JADEITE_ORE.get()));
        this.add(MagicTreasuresBlocks.ONYX_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.ONYX_ORE.get()));
        this.add(MagicTreasuresBlocks.RUBY_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.RUBY_ORE.get()));
        this.add(MagicTreasuresBlocks.SAPPHIRE_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.SAPPHIRE_ORE.get()));
        this.add(MagicTreasuresBlocks.TOPAZ_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.TOPAZ_ORE.get()));

        this.add(MagicTreasuresBlocks.SILVER_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.RAW_SILVER.get()));

        this.add(MagicTreasuresBlocks.DEEPSLATE_SILVER_ORE.get(), block -> createOreDrop(block,
                MagicTreasuresItems.RAW_SILVER.get()));

        // OR
//        this.add(MagicTreasuresBlocks.SILVER_ORE.get(), block ->
//                createSingleItemTable(MagicTreasuresItems.SILVER_ORE.get(),
//                        UniformGenerator.between(1, 2)));


    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // only return blocks that are generated in this class
        return Arrays.asList(
                MagicTreasuresBlocks.DEEPSLATE_JADEITE_ORE.get(),
                MagicTreasuresBlocks.DEEPSLATE_ONYX_ORE.get(),
                MagicTreasuresBlocks.DEEPSLATE_RUBY_ORE.get(),
                MagicTreasuresBlocks.DEEPSLATE_SAPPHIRE_ORE.get(),
                MagicTreasuresBlocks.DEEPSLATE_TOPAZ_ORE.get(),
                MagicTreasuresBlocks.DEEPSLATE_SILVER_ORE.get(),

                MagicTreasuresBlocks.JADEITE_ORE.get(),
                MagicTreasuresBlocks.ONYX_ORE.get(),
                MagicTreasuresBlocks.RUBY_ORE.get(),
                MagicTreasuresBlocks.SAPPHIRE_ORE.get(),
                MagicTreasuresBlocks.TOPAZ_ORE.get(),
                MagicTreasuresBlocks.SILVER_ORE.get()
        );
    }
}
