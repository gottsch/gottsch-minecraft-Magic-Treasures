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
package mod.gottsch.forge.magic_treasures.core.capability;

import mod.gottsch.forge.magic_treasures.core.item.ManaWell;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * A view over a mana well stack's NBT. The state lives in the stack tag (not a capability) so vanilla and
 * Curios sync it to the client whenever it changes. Missing values fall back to the item's defaults (full).
 *
 * @author Mark Gottschling on 10/9/2026
 */
public class ManaWellHandler implements IManaWellHandler {
    private static final String TAG = "magictreasures_mana_well";
    private static final String MANA = "mana";
    private static final String RECHARGES = "recharges";

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

    private CompoundTag read() {
        CompoundTag tag = stack.getTagElement(TAG);
        return tag == null ? new CompoundTag() : tag;
    }

    @Override
    public double getMaxMana() {
        return well.getMaxMana();
    }

    @Override
    public double getMana() {
        CompoundTag tag = read();
        return tag.contains(MANA) ? tag.getDouble(MANA) : getMaxMana();
    }

    @Override
    public void setMana(double mana) {
        stack.getOrCreateTagElement(TAG).putDouble(MANA, Math.max(0, Math.min(mana, getMaxMana())));
    }

    @Override
    public int getMaxRecharges() {
        return well.getMaxRecharges();
    }

    @Override
    public int getRecharges() {
        CompoundTag tag = read();
        return tag.contains(RECHARGES) ? tag.getInt(RECHARGES) : getMaxRecharges();
    }

    @Override
    public void setRecharges(int recharges) {
        stack.getOrCreateTagElement(TAG).putInt(RECHARGES, Math.max(0, recharges));
    }
}
