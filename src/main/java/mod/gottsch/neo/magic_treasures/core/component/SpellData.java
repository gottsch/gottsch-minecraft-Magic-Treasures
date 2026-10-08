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
import net.minecraft.resources.ResourceLocation;

/**
 * The persisted state of one spell on a piece of jewelry: which spell, and (for cooldown spells)
 * the game time its cooldown expires.
 * Replaces the 1.20.1 SpellEntity NBT ("name", "cooldownExpireTime").
 */
public record SpellData(ResourceLocation name, double cooldownExpireTime) {

    public static final Codec<SpellData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("name").forGetter(SpellData::name),
            Codec.DOUBLE.optionalFieldOf("cooldown_expire_time", 0D).forGetter(SpellData::cooldownExpireTime)
    ).apply(instance, SpellData::new));

    public SpellData(ResourceLocation name) {
        this(name, 0D);
    }

    public SpellData withCooldownExpireTime(double cooldownExpireTime) {
        return new SpellData(name, cooldownExpireTime);
    }
}
