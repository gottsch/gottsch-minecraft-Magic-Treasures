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
package mod.gottsch.forge.magic_treasures.core.component;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * {@code ItemStack.get/has/set/remove} for {@link StackComponent}s: the 1.21.1 component calls, on the stack's NBT.
 * The stack tag syncs to clients by itself, so nothing here needs a packet (unlike 1.x's item capability).
 * <p>
 * Reads never write: {@link #get} and {@link #getOrDefault} leave the stack alone, so tooltips and item-bar rendering
 * can call them freely.
 *
 * @author Mark Gottschling on 10/14/2026
 */
public final class StackData {
    private StackData() {}

    public static boolean has(ItemStack stack, StackComponent<?> component) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(component.key());
    }

    /** the value, or null when the stack has none or it doesn't decode */
    @Nullable
    public static <T> T get(ItemStack stack, StackComponent<T> component) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(component.key())) {
            return null;
        }
        return component.codec().parse(NbtOps.INSTANCE, tag.get(component.key()))
                .resultOrPartial(error -> MagicTreasures.LOGGER.warn("bad {} on {}: {}", component.key(), stack, error))
                .orElse(null);
    }

    public static <T> T getOrDefault(ItemStack stack, StackComponent<T> component, T defaultValue) {
        T value = get(stack, component);
        return value != null ? value : defaultValue;
    }

    /** sets the value; null removes it, as 1.21.1's {@code set} does */
    public static <T> void set(ItemStack stack, StackComponent<T> component, @Nullable T value) {
        if (value == null) {
            remove(stack, component);
            return;
        }
        component.codec().encodeStart(NbtOps.INSTANCE, value)
                .resultOrPartial(error -> MagicTreasures.LOGGER.warn("can't write {} on {}: {}", component.key(), stack, error))
                .ifPresent((Tag encoded) -> stack.getOrCreateTag().put(component.key(), encoded));
    }

    public static void remove(ItemStack stack, StackComponent<?> component) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(component.key());
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
    }
}
