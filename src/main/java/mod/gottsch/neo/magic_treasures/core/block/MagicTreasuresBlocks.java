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
package mod.gottsch.neo.magic_treasures.core.block;

import mod.gottsch.neo.magic_treasures.core.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class MagicTreasuresBlocks {

    // ore
    public static final Supplier<BlockBehaviour.Properties> ORE_PROPS = () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE);
    public static final Supplier<BlockBehaviour.Properties> DEEPSLATE_ORE_PROPS = () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE);

    public static final DeferredBlock<Block> TOPAZ_ORE = Registration.BLOCKS.register("topaz_ore", () -> new Block(ORE_PROPS.get()));
    public static final DeferredBlock<Block> DEEPSLATE_TOPAZ_ORE = Registration.BLOCKS.register("deepslate_topaz_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final DeferredBlock<Block> ONYX_ORE = Registration.BLOCKS.register("onyx_ore", () -> new Block(ORE_PROPS.get()));
    public static final DeferredBlock<Block> DEEPSLATE_ONYX_ORE = Registration.BLOCKS.register("deepslate_onyx_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final DeferredBlock<Block> JADEITE_ORE = Registration.BLOCKS.register("jadeite_ore", () -> new Block(ORE_PROPS.get()));
    public static final DeferredBlock<Block> DEEPSLATE_JADEITE_ORE = Registration.BLOCKS.register("deepslate_jadeite_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final DeferredBlock<Block> RUBY_ORE = Registration.BLOCKS.register("ruby_ore", () -> new Block(ORE_PROPS.get()));
    public static final DeferredBlock<Block> DEEPSLATE_RUBY_ORE = Registration.BLOCKS.register("deepslate_ruby_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final DeferredBlock<Block> SAPPHIRE_ORE = Registration.BLOCKS.register("sapphire_ore", () -> new Block(ORE_PROPS.get()));
    public static final DeferredBlock<Block> DEEPSLATE_SAPPHIRE_ORE = Registration.BLOCKS.register("deepslate_sapphire_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final DeferredBlock<Block> SILVER_ORE = Registration.BLOCKS.register("silver_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    public static final DeferredBlock<Block> DEEPSLATE_SILVER_ORE = Registration.BLOCKS.register("deepslate_silver_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)));

    /**
     *
     */
    /** force-loads this class so its holders are created before the register is attached to the bus */
    public static void init() {
    }
}
