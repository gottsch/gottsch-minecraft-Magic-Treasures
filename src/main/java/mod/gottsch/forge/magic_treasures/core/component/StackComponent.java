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

import com.mojang.serialization.Codec;

/**
 * Forge 1.20.1 has no data components. This stands in for one: a value type stored on the stack's NBT under
 * {@link #key()}, written and read with its codec through {@link StackData}. The 1.21.1 port's component ids are
 * kept as the NBT keys ({@code magictreasures:jewelry_identity}), so both loaders name the same data the same way.
 *
 * @param key   the NBT key under the stack's tag
 * @param codec reads and writes the value
 * @author Mark Gottschling on 10/14/2026
 */
public record StackComponent<T>(String key, Codec<T> codec) {

    /** itself: lets code written for 1.21.1's {@code DeferredHolder} components ({@code X.get()}) read the same */
    public StackComponent<T> get() {
        return this;
    }
}
