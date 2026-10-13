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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * What a set gives while enough of its pieces are worn. A bonus can change spell numbers during a cast, act on its own
 * about once a second, or both. A bonus that other systems check (on death, after a drain) can also be a plain marker:
 * those systems ask {@link SetEquipment#isActive} whether it's on.
 */
public interface SetBonus {

    /**
     * Changes one of {@code spell}'s numbers for this cast by {@code player}; returns {@code value} unchanged when it
     * doesn't apply.
     */
    default double modify(Player player, SpellStat stat, ISpell spell, double value) {
        return value;
    }

    /** Called on the server about once a second while the bonus is active. */
    default void tick(ServerPlayer player) {
    }

    /** Whether this bonus is, or contains, a bonus of {@code type}. */
    default boolean includes(Class<? extends SetBonus> type) {
        return type.isInstance(this);
    }

    /** Several bonuses as one tier: each changes the number in turn, and each ticks. */
    static SetBonus of(SetBonus... bonuses) {
        return new SetBonus() {
            @Override
            public double modify(Player player, SpellStat stat, ISpell spell, double value) {
                for (SetBonus bonus : bonuses) {
                    value = bonus.modify(player, stat, spell, value);
                }
                return value;
            }

            @Override
            public void tick(ServerPlayer player) {
                for (SetBonus bonus : bonuses) {
                    bonus.tick(player);
                }
            }

            @Override
            public boolean includes(Class<? extends SetBonus> type) {
                for (SetBonus bonus : bonuses) {
                    if (bonus.includes(type)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
