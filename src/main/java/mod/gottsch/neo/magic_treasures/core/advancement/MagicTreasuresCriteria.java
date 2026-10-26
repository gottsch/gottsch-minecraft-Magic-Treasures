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
    /** a gem removed on the anvil with shears (the gem is lost, the jewelry kept) */
    public static final String REMOVE_GEM = "remove_gem";
    /** one of each main jewelry type worn: ring, necklace, bracelet, and a belt (Dressed to the Nines) */
    public static final String DRESSED = "dressed";
    /** every piece of a named set worn; reports the set (Fully Adorned, the per-set advancements) */
    public static final String FULL_SET = "full_set";
    /** jewelry repaired on the anvil with another piece */
    public static final String REPAIR = "repair";
    /** a spell's cost drew on a mana well because its jewelry ran short */
    public static final String MANA_WELL = "mana_well";
    /** a 2.0 jewelry piece carried (inventory or Curios); reports its style and rarity */
    public static final String CARRY = "carry";

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, MagicTreasures.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, JewelryTrigger> JEWELRY =
            TRIGGERS.register("jewelry", JewelryTrigger::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }
}
