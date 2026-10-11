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
package mod.gottsch.neo.magic_treasures.core.set;

import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Worn jewelry with spells, for changes made outside a cast (siphon, Take Root).
 * <p>
 * The handlers write the jewelry's data component, which syncs to the client with the stack, so no packet is needed
 * (the Forge port sends one).
 */
public final class WornJewelry {

    public record Piece(ItemStack stack, IJewelryHandler handler) {}

    private WornJewelry() {}

    /** Every worn jewelry piece with a spell. */
    public static List<Piece> pieces(Player player) {
        List<Piece> pieces = new ArrayList<>();
        for (ItemStack stack : SetEquipment.getWornItems(player)) {
            JewelryHandler.get(stack).filter(handler -> !handler.getSpells().isEmpty())
                    .ifPresent(handler -> pieces.add(new Piece(stack, handler)));
        }
        return pieces;
    }

    /** The worn piece with the smallest share of its mana left, if any isn't full. */
    public static Optional<IJewelryHandler> lowestMana(Player player) {
        return pieces(player).stream()
                .map(Piece::handler)
                .filter(handler -> handler.getMaxMana() > 0 && handler.getMana() < handler.getMaxMana())
                .min(Comparator.comparingDouble(handler -> handler.getMana() / handler.getMaxMana()));
    }
}
