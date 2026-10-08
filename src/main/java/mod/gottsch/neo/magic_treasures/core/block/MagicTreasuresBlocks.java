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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MagicTreasuresBlocks {

    // ore
    public static final Supplier<BlockBehaviour.Properties> ORE_PROPS = () -> BlockBehaviour.Properties.copy(Blocks.STONE);
    public static final Supplier<BlockBehaviour.Properties> DEEPSLATE_ORE_PROPS = () -> BlockBehaviour.Properties.copy(Blocks.DEEPSLATE);

    public static final RegistryObject<Block> TOPAZ_ORE = Registration.BLOCKS.register("topaz_ore", () -> new Block(ORE_PROPS.get()));
    public static final RegistryObject<Block> DEEPSLATE_TOPAZ_ORE = Registration.BLOCKS.register("deepslate_topaz_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final RegistryObject<Block> ONYX_ORE = Registration.BLOCKS.register("onyx_ore", () -> new Block(ORE_PROPS.get()));
    public static final RegistryObject<Block> DEEPSLATE_ONYX_ORE = Registration.BLOCKS.register("deepslate_onyx_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final RegistryObject<Block> JADEITE_ORE = Registration.BLOCKS.register("jadeite_ore", () -> new Block(ORE_PROPS.get()));
    public static final RegistryObject<Block> DEEPSLATE_JADEITE_ORE = Registration.BLOCKS.register("deepslate_jadeite_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final RegistryObject<Block> RUBY_ORE = Registration.BLOCKS.register("ruby_ore", () -> new Block(ORE_PROPS.get()));
    public static final RegistryObject<Block> DEEPSLATE_RUBY_ORE = Registration.BLOCKS.register("deepslate_ruby_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final RegistryObject<Block> SAPPHIRE_ORE = Registration.BLOCKS.register("sapphire_ore", () -> new Block(ORE_PROPS.get()));
    public static final RegistryObject<Block> DEEPSLATE_SAPPHIRE_ORE = Registration.BLOCKS.register("deepslate_sapphire_ore", () -> new Block(DEEPSLATE_ORE_PROPS.get()));

    public static final RegistryObject<Block> SILVER_ORE = Registration.BLOCKS.register("silver_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> DEEPSLATE_SILVER_ORE = Registration.BLOCKS.register("deepslate_silver_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)));

    /**
     *
     */
    public static void register() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Registration.BLOCKS.register(eventBus);
    }
}
