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
package mod.gottsch.forge.magic_treasures.core.set;

import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import net.minecraft.world.entity.player.Player;

/**
 * Maldritch's Remains, 4 pieces: life you drain also refills a little mana. After a drain spell heals the player,
 * half the health it took goes back into the jewelry that cast it.
 *
 * @author Mark Gottschling on 10/13/2026
 */
public class DrainRefillBonus implements SetBonus {
    /** the share of drained health that comes back as mana */
    public static final double SHARE = 0.5;

    /** Puts {@link #SHARE} of {@code drained} into {@code handler}'s mana, if the player has the bonus on. */
    public static void refill(Player player, IJewelryHandler handler, double drained) {
        if (drained <= 0 || !SetEquipment.isActive(player, DrainRefillBonus.class)) {
            return;
        }
        handler.setMana(Math.min(handler.getMaxMana(), handler.getMana() + drained * SHARE));
    }
}
