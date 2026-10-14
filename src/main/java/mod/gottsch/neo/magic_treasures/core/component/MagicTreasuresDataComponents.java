/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.magic_treasures.core.component;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component types.
 */
public class MagicTreasuresDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MagicTreasures.MOD_ID);

    /*
     * NOTE jewelry items have no default value for this component: the default depends on stone tiers,
     * which come from item tags that aren't loaded at startup. It is created on first access by
     * JewelryHandler.get(stack) - the same moment the 1.20.1 capability was created (initCapabilities).
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<JewelryData>> JEWELRY =
            DATA_COMPONENTS.registerComponentType("jewelry", builder -> builder
                    .persistent(JewelryData.CODEC)
                    .networkSynchronized(JewelryData.STREAM_CODEC)
                    .cacheEncoding());

    /** 2.0 generic jewelry: what the piece is (type, material, gem, size, preset); see JewelryIdentity */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<JewelryIdentity>> JEWELRY_IDENTITY =
            DATA_COMPONENTS.registerComponentType("jewelry_identity", builder -> builder
                    .persistent(JewelryIdentity.CODEC)
                    .networkSynchronized(JewelryIdentity.STREAM_CODEC)
                    .cacheEncoding());

    /**
     * 2.0 generic jewelry: its play state (uses, mana, repairs, recharges, spells). Max values are computed from the
     * identity, never stored. Filled from the computed stats the first time a handler reads the stack.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<JewelryState>> JEWELRY_STATE =
            DATA_COMPONENTS.registerComponentType("jewelry_state", builder -> builder
                    .persistent(JewelryState.CODEC)
                    .networkSynchronized(JewelryState.STREAM_CODEC)
                    .cacheEncoding());

    /**
     * Sal'andaar's Anvilwork, full set: the piece's next anvil repair is free (keeps its repairs). Stamped on worn set
     * jewelry by TemperedBonus; the anvil removes it.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> TEMPERED =
            DATA_COMPONENTS.registerComponentType("tempered", builder -> builder
                    .persistent(Unit.CODEC)
                    .networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    /** a mana well's mana and recharges; the item sets a full default, so every well has it */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ManaWellData>> MANA_WELL =
            DATA_COMPONENTS.registerComponentType("mana_well", builder -> builder
                    .persistent(ManaWellData.CODEC)
                    .networkSynchronized(ManaWellData.STREAM_CODEC)
                    .cacheEncoding());

    /** a lore book's story: its id in the {@code magictreasures:lore_book} registry */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> LORE_BOOK =
            DATA_COMPONENTS.registerComponentType("lore_book", builder -> builder
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC));
}
