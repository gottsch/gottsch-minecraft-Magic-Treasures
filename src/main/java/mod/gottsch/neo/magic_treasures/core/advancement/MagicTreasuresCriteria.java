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
package mod.gottsch.neo.magic_treasures.core.advancement;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Magic Treasures' advancement triggers.
 *
 * @author Mark Gottschling on 10/9/2026
 */
public class MagicTreasuresCriteria {
    /** jewelry actions the {@code magictreasures:jewelry} advancement trigger reports */
    public static final String ADD_GEM = "add_gem";
    public static final String IMBUE = "imbue";
    public static final String RECHARGE = "recharge";
    public static final String EXTRACT_GEM = "extract_gem";
    public static final String FULL_SET = "full_set";

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, MagicTreasures.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, JewelryTrigger> JEWELRY =
            TRIGGERS.register("jewelry", JewelryTrigger::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }
}
