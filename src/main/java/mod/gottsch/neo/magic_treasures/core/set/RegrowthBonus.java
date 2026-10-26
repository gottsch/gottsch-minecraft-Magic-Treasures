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

import mod.gottsch.neo.magic_treasures.core.spell.SpellEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Silbro's Grove, 4 pieces: standing on grass, moss or leaves slowly heals you, about as fast as Regeneration I
 * (half a heart every 3 seconds). Heals directly rather than through the effect, which a once-a-second refresh would
 * speed up.
 */
public class RegrowthBonus implements SetBonus {
    private static final int INTERVAL_TICKS = 60;
    private static final float HEAL = 1.0F;

    @Override
    public void tick(ServerPlayer player) {
        if (player.tickCount % INTERVAL_TICKS != 0 || !player.onGround()
                || player.getHealth() >= player.getMaxHealth() || !isLiving(player.getBlockStateOn())) {
            return;
        }
        player.heal(HEAL);
        SpellEffects.burst(player.level(), player, ParticleTypes.HAPPY_VILLAGER, 4);
    }

    private static boolean isLiving(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.MOSS_CARPET)
                || state.is(BlockTags.LEAVES);
    }
}
