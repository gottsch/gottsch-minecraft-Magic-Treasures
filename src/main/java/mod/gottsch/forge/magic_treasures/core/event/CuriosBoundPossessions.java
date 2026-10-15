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
package mod.gottsch.forge.magic_treasures.core.event;

import mod.gottsch.forge.magic_treasures.core.set.JewelrySets;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

/**
 * Bound possessions in Curios slots: Maldritch's pieces stay in them when a player dies with the full set on
 * ({@link BoundPossessionsHandler} marks that death first). Curios is optional: call {@link #register} only when it's
 * loaded, or loading this class fails with NoClassDefFoundError.
 *
 * @author Mark Gottschling on 10/13/2026
 */
public final class CuriosBoundPossessions {
    private CuriosBoundPossessions() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(CuriosBoundPossessions::onDropRules);
    }

    private static void onDropRules(DropRulesEvent event) {
        if (event.getEntity() instanceof Player player
                && player.getPersistentData().contains(BoundPossessionsHandler.KEY)) {
            event.addOverride(stack -> JewelrySets.MALDRITCHS_REMAINS.contains(stack), ICurio.DropRule.ALWAYS_KEEP);
        }
    }
}
