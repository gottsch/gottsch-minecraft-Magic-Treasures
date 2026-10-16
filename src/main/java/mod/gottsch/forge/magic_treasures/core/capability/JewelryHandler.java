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
package mod.gottsch.forge.magic_treasures.core.capability;

import mod.gottsch.forge.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.forge.gottschcore.nbt.StackData;
import mod.gottsch.forge.magic_treasures.core.component.SpellData;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * The entry point to a piece of jewelry: {@code JewelryHandler.get(stack)} gives its {@link IJewelryHandler}, if it is
 * jewelry. Since M9 only 2.0 generic jewelry is ({@link GenericJewelryHandler}); the 1.x handler that read the
 * {@code jewelry} component went with the 1.x items (their stacks convert, see
 * {@link mod.gottsch.forge.magic_treasures.core.jewelry.data.LegacyConversion}).
 */
public final class JewelryHandler {
    private JewelryHandler() {}

    /** a handler for a jewelry stack, or empty if the stack isn't jewelry (or is a bare generic item with no identity) */
    public static Optional<IJewelryHandler> get(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !StackData.has(stack, MagicTreasuresDataComponents.JEWELRY_IDENTITY)) {
            return Optional.empty();
        }
        return GenericJewelryHandler.get(stack);
    }

    /** writes one spell's persisted state back to the stack (used by bound SpellEntity instances) */
    public static void updateSpell(ItemStack stack, int index, SpellData spell) {
        if (StackData.has(stack, MagicTreasuresDataComponents.JEWELRY_IDENTITY)) {
            GenericJewelryHandler.updateSpell(stack, index, spell);
        }
    }
}
