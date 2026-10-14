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

import mod.gottsch.neo.magic_treasures.core.spell.ISpell;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Moonlit (Selene's Tides, full set): every spell costs 25% less at night. Night is the clock's night (13000 to 23000
 * ticks), not the sky's darkness, so a daytime thunderstorm doesn't count; dimensions with a fixed time (the Nether,
 * the End) never have one.
 */
public class MoonlitBonus implements SetBonus {
    private static final double COST = 0.75;
    private static final long DUSK = 13000;
    private static final long DAWN = 23000;

    @Override
    public double modify(Player player, SpellStat stat, ISpell spell, double value) {
        return stat == SpellStat.COST && isNight(player.level()) ? value * COST : value;
    }

    public static boolean isNight(Level level) {
        if (level.dimensionType().hasFixedTime()) {
            return false;
        }
        long time = level.getDayTime() % 24000L;
        return time >= DUSK && time < DAWN;
    }
}
