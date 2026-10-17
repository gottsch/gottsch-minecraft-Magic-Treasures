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
package mod.gottsch.neo.magic_treasures.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * A mana well's state, stored under {@code magictreasures:mana_well}. Immutable: change it with the
 * {@code with...} copies (see {@code ManaWellHandler}).
 *
 * @author Mark Gottschling on 10/9/2026
 */
public record ManaWellData(double maxMana, double mana, int maxRecharges, int recharges) {

    public static final Codec<ManaWellData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("max_mana").forGetter(ManaWellData::maxMana),
            Codec.DOUBLE.fieldOf("mana").forGetter(ManaWellData::mana),
            Codec.INT.fieldOf("max_recharges").forGetter(ManaWellData::maxRecharges),
            Codec.INT.fieldOf("recharges").forGetter(ManaWellData::recharges)
    ).apply(instance, ManaWellData::new));

    public static final StreamCodec<ByteBuf, ManaWellData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ManaWellData::maxMana,
            ByteBufCodecs.DOUBLE, ManaWellData::mana,
            ByteBufCodecs.VAR_INT, ManaWellData::maxRecharges,
            ByteBufCodecs.VAR_INT, ManaWellData::recharges,
            ManaWellData::new);

    /** A full mana well. */
    public static ManaWellData full(double maxMana, int maxRecharges) {
        return new ManaWellData(maxMana, maxMana, maxRecharges, maxRecharges);
    }

    public ManaWellData withMana(double mana) {
        return new ManaWellData(maxMana, Math.max(0, Math.min(mana, maxMana)), maxRecharges, recharges);
    }

    public ManaWellData withRecharges(int recharges) {
        return new ManaWellData(maxMana, mana, maxRecharges, Math.max(0, recharges));
    }
}
