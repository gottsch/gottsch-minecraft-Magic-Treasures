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

import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Tempered (Sal'andaar's Anvilwork, full set): worn jewelry of the set is stamped {@code magictreasures:tempered}, and
 * its next anvil repair uses the stamp instead of a repair. Stamping happens here, while the set is worn; the anvil
 * can't check the set itself, because the piece being repaired is in the anvil, not worn.
 */
public class TemperedBonus implements SetBonus {
    /** the set's pieces; a supplier, since the bonus is built before its set exists */
    private final Supplier<JewelrySet> set;

    public TemperedBonus(Supplier<JewelrySet> set) {
        this.set = set;
    }

    @Override
    public void tick(ServerPlayer player) {
        JewelrySet pieces = set.get();
        for (ItemStack stack : SetEquipment.getWornItems(player)) {
            if (pieces.contains(stack) && JewelryHandler.get(stack).isPresent()
                    && !stack.has(MagicTreasuresDataComponents.TEMPERED.get())) {
                stack.set(MagicTreasuresDataComponents.TEMPERED.get(), Unit.INSTANCE);
            }
        }
    }
}
