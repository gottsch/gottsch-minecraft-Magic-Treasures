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
package mod.gottsch.neo.magic_treasures.core.setup;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.block.MagicTreasuresBlocks;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresCreativeModeTabs;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.loot.MagicTreasuresLootFunctions;
import mod.gottsch.neo.magic_treasures.core.loot.modifier.MagicTreasuresLootModifiers;
import mod.gottsch.neo.magic_treasures.core.particle.MagicTreasuresParticles;
import mod.gottsch.neo.magic_treasures.core.world.feature.MagicTreasuresConfiguredFeatures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import mod.gottsch.neo.magic_treasures.core.recipe.MagicTreasuresRecipes;

/**
 * Created by Mark Gottschling on 5/3/2023
 */
public class Registration {
    /*
     * deferred registries
     */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MagicTreasures.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MagicTreasures.MOD_ID);

    /**
     * Attaches every deferred register to the mod event bus.
     */
    public static void init(IEventBus eventBus) {
        // force-load the static holders before their registers are attached to the bus
        MagicTreasuresBlocks.init();
        MagicTreasuresItems.init();

        MagicTreasuresDataComponents.DATA_COMPONENTS.register(eventBus);
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        MagicTreasuresConfiguredFeatures.FEATURES.register(eventBus);
        MagicTreasuresLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        MagicTreasuresLootFunctions.LOOT_FUNCTIONS.register(eventBus);
        MagicTreasuresParticles.register(eventBus);
        MagicTreasuresRecipes.register(eventBus);
        MagicTreasuresCreativeModeTabs.TABS.register(eventBus);
    }
}
