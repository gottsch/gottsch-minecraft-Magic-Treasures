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
package mod.gottsch.neo.magic_treasures.core.event;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.advancement.MagicTreasuresCriteria;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.item.JewelryPliers;
import mod.gottsch.neo.magic_treasures.core.tag.MagicTreasuresTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Optional;

/**
 * Fires the {@code magictreasures:jewelry} advancement trigger for jewelry actions that happen outside spells.
 * (Wearing a full set is checked in {@link SpellEventHandler}.)
 *
 * @author Mark Gottschling on 10/9/2026
 */
@EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class AdvancementEventHandler {

    /**
     * An anvil result was taken. Works out which jewelry action it was the same way
     * {@code AnvilEventHandler} chose the result: from the left jewelry's state and the right item.
     */
    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        Optional<IJewelryHandler> handler = JewelryHandler.get(left.copy());
        if (handler.isEmpty()) {
            return;
        }
        String action = null;
        if (!handler.get().hasStone() && right.is(MagicTreasuresTags.Items.STONES)) {
            action = MagicTreasuresCriteria.ADD_GEM;
        } else if (handler.get().hasStone() && right.is(MagicTreasuresTags.Items.STONE_REMOVAL_TOOLS)) {
            return;
        } else if (handler.get().getSpells().isEmpty() && right.is(MagicTreasuresTags.Items.SPELL_SCROLLS)) {
            action = MagicTreasuresCriteria.IMBUE;
        } else if (handler.get().hasStone() && right.is(MagicTreasuresTags.Items.RECHARGERS)) {
            action = MagicTreasuresCriteria.RECHARGE;
        }
        if (action != null) {
            MagicTreasuresCriteria.JEWELRY.get().trigger(player, action);
        }
    }

    /** A gem was crafted out of jewelry with the pliers ({@code GemExtractionRecipe}). */
    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getCrafting().is(MagicTreasuresTags.Items.STONES)) {
            return;
        }
        Container container = event.getInventory();
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).getItem() instanceof JewelryPliers) {
                MagicTreasuresCriteria.JEWELRY.get().trigger(player, MagicTreasuresCriteria.EXTRACT_GEM);
                return;
            }
        }
    }
}
