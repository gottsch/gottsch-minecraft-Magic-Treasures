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
package mod.gottsch.forge.magic_treasures.core.entity;

import mod.gottsch.forge.magic_treasures.core.setup.Registration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

/**
 * Magic Treasures' entity types. Only spell effects so far: none of them is ever saved or summonable.
 *
 * @author Mark Gottschling on 10/16/2026
 */
public class MagicTreasuresEntities {
    /** a Ring of Swords sword. The client works out its orbit itself, so the server rarely needs to send positions. */
    public static final RegistryObject<EntityType<OrbitingSword>> ORBITING_SWORD = Registration.ENTITIES.register("orbiting_sword",
            () -> EntityType.Builder.<OrbitingSword>of(OrbitingSword::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .noSave()
                    .noSummon()
                    .fireImmune()
                    .clientTrackingRange(8)
                    .updateInterval(20)
                    .build("orbiting_sword"));

    public static void register(IEventBus bus) {
        Registration.ENTITIES.register(bus);
    }
}
