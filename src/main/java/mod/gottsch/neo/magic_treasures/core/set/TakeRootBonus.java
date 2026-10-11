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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Take Root (Silbro's Grove, full set): living wood heals. In daylight under open sky, every worn piece of the set
 * gets back 1 durability every 30 seconds: uses for jewelry, damage for the weapon and armor.
 */
public class TakeRootBonus implements SetBonus {
    private static final int INTERVAL_TICKS = 600;

    /** the set's pieces; a supplier, since the bonus is built before its set exists */
    private final Supplier<JewelrySet> set;

    public TakeRootBonus(Supplier<JewelrySet> set) {
        this.set = set;
    }

    @Override
    public void tick(ServerPlayer player) {
        if (player.tickCount % INTERVAL_TICKS != 0 || !player.level().isDay()
                || !player.level().canSeeSky(player.blockPosition().above())) {
            return;
        }
        JewelrySet pieces = set.get();
        for (WornJewelry.Piece piece : WornJewelry.pieces(player)) {
            IJewelryHandler handler = piece.handler();
            if (pieces.contains(piece.stack().getItem()) && !handler.isInfinite() && handler.getUses() < handler.getMaxUses()) {
                handler.setUses(handler.getUses() + 1);
            }
        }
        // the weapon and armor: plain item damage
        for (ItemStack stack : SetEquipment.getWornItems(player)) {
            if (pieces.contains(stack.getItem()) && stack.isDamageableItem() && stack.isDamaged()) {
                stack.setDamageValue(stack.getDamageValue() - 1);
            }
        }
    }
}
