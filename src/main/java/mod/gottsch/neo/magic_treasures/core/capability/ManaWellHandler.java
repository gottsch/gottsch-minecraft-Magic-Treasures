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
package mod.gottsch.neo.magic_treasures.core.capability;

import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.component.ManaWellData;
import mod.gottsch.neo.magic_treasures.core.item.ManaWell;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * A write-through view over a mana well stack's {@link ManaWellData} component. The component syncs to the
 * client on its own whenever it changes. A stack without the component reads as a full well.
 *
 * @author Mark Gottschling on 10/9/2026
 */
public class ManaWellHandler implements IManaWellHandler {
    private final ItemStack stack;
    private final ManaWell well;

    private ManaWellHandler(ItemStack stack, ManaWell well) {
        this.stack = stack;
        this.well = well;
    }

    public static Optional<IManaWellHandler> get(ItemStack stack) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ManaWell well) {
            return Optional.of(new ManaWellHandler(stack, well));
        }
        return Optional.empty();
    }

    /** the stack's data, or a full well if it has never been drawn from (reading never writes) */
    private ManaWellData data() {
        return stack.getOrDefault(MagicTreasuresDataComponents.MANA_WELL, ManaWellData.full(well.getMaxMana(), well.getMaxRecharges()));
    }

    @Override
    public double getMaxMana() {
        return data().maxMana();
    }

    @Override
    public double getMana() {
        return data().mana();
    }

    @Override
    public void setMana(double mana) {
        stack.set(MagicTreasuresDataComponents.MANA_WELL, data().withMana(mana));
    }

    @Override
    public int getMaxRecharges() {
        return data().maxRecharges();
    }

    @Override
    public int getRecharges() {
        return data().recharges();
    }

    @Override
    public void setRecharges(int recharges) {
        stack.set(MagicTreasuresDataComponents.MANA_WELL, data().withRecharges(recharges));
    }
}
