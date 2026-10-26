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
package mod.gottsch.neo.magic_treasures.core.entity;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Magic Treasures' entity types. Only spell effects so far: none of them is ever saved or summonable.
 *
 * @author Mark Gottschling on 10/16/2026
 */
public class MagicTreasuresEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MagicTreasures.MOD_ID);

    /** a Ring of Swords sword. The client works out its orbit itself, so the server rarely needs to send positions. */
    public static final DeferredHolder<EntityType<?>, EntityType<OrbitingSword>> ORBITING_SWORD = ENTITY_TYPES.register("orbiting_sword",
            () -> EntityType.Builder.<OrbitingSword>of(OrbitingSword::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .noSave()
                    .noSummon()
                    .fireImmune()
                    .clientTrackingRange(8)
                    .updateInterval(20)
                    .build("orbiting_sword"));

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
