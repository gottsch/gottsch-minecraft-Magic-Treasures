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

import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.spell.SpellEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.function.Supplier;

/**
 * Tidewalker (Selene's Tides, 4 pieces): the tide refills her pearls. In water, the player swims with Dolphin's Grace,
 * and every worn jewelry piece of the set gets back 1 mana every 5 seconds: a slow mana source only her set has.
 *
 * @author Mark Gottschling on 10/14/2026
 */
public class TidewalkerBonus implements SetBonus {
    private static final int MANA_INTERVAL_TICKS = 100;
    private static final double MANA = 1.0;
    /** longer than the once-a-second tick, so the effect never flickers off between refreshes */
    private static final int GRACE_TICKS = 60;

    /** the set's pieces; a supplier, since the bonus is built before its set exists */
    private final Supplier<JewelrySet> set;

    public TidewalkerBonus(Supplier<JewelrySet> set) {
        this.set = set;
    }

    @Override
    public void tick(ServerPlayer player) {
        if (!player.isInWater()) {
            return;
        }
        // ambient, no particles: a set bonus, not a potion
        player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, GRACE_TICKS, 0, true, false, true));

        if (player.tickCount % MANA_INTERVAL_TICKS != 0) {
            return;
        }
        JewelrySet pieces = set.get();
        boolean refilled = false;
        for (WornJewelry.Piece piece : WornJewelry.pieces(player)) {
            IJewelryHandler handler = piece.handler();
            if (pieces.contains(piece.stack()) && handler.getMana() < handler.getMaxMana()) {
                handler.setMana(Math.min(handler.getMaxMana(), handler.getMana() + MANA));
                refilled = true;
            }
        }
        if (refilled) {
            SpellEffects.burst(player.level(), player, ParticleTypes.BUBBLE, 6);
        }
    }
}
