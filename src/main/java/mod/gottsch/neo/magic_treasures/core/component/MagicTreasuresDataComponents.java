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

    /** a mana well's mana and recharges; the item sets a full default, so every well has it */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ManaWellData>> MANA_WELL =
            DATA_COMPONENTS.registerComponentType("mana_well", builder -> builder
                    .persistent(ManaWellData.CODEC)
                    .networkSynchronized(ManaWellData.STREAM_CODEC)
                    .cacheEncoding());
}
