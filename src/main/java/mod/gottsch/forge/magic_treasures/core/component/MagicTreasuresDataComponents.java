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
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

/**
 * The 2.0 stack data, as {@link StackComponent}s on the stack's NBT (Forge 1.20.1 has no data components). Same
 * names, codecs and meanings as the 1.21.1 data components; read and write them with {@link StackData}. Nothing to
 * register.
 * <p>
 * Mana wells keep their 1.x capability on this loader (2.0 didn't change them).
 */
public final class MagicTreasuresDataComponents {
    private MagicTreasuresDataComponents() {}

    /** 2.0 generic jewelry: what the piece is (type, material, gem, size, preset); see JewelryIdentity */
    public static final StackComponent<JewelryIdentity> JEWELRY_IDENTITY = of("jewelry_identity", JewelryIdentity.CODEC);

    /**
     * 2.0 generic jewelry: its play state (uses, mana, repairs, recharges, spells). Max values are computed from the
     * identity, never stored. Filled from the computed stats the first time a handler reads the stack.
     */
    public static final StackComponent<JewelryState> JEWELRY_STATE = of("jewelry_state", JewelryState.CODEC);

    /**
     * Sal'andaar's Anvilwork, full set: the piece's next anvil repair is free (keeps its repairs). Stamped on worn set
     * jewelry by TemperedBonus; the anvil removes it.
     */
    public static final StackComponent<Unit> TEMPERED = of("tempered", Codec.unit(Unit.INSTANCE));

    /** a lore book's story: its id in the {@code magictreasures:lore_book} registry */
    public static final StackComponent<ResourceLocation> LORE_BOOK = of("lore_book", ResourceLocation.CODEC);

    private static <T> StackComponent<T> of(String name, Codec<T> codec) {
        return new StackComponent<>(MagicTreasures.MOD_ID + ":" + name, codec);
    }
}
