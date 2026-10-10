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

import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The registered sets. 2.0 starts with the four lore characters' sets, built from existing named jewelry; their
 * weapons, armor and higher tiers come later (see the lore bible).
 */
public final class JewelrySets {
    private static final List<JewelrySet> SETS = new ArrayList<>();
    /** item -> its set; built on first lookup, after items are registered */
    private static Map<Item, JewelrySet> byItem;

    private static final String[] WARD_SPELLS = {"magic_resistance", "mana_shield", "reflection", "spectral_armor"};

    public static final JewelrySet SALANDAARS_ANVILWORK = register(JewelrySet.builder("salandaars_anvilwork")
            .piece(MagicTreasuresItems.SALANDAARS_WARD)
            .piece(MagicTreasuresItems.RING_OF_FORTITUDE)
            .bonus(2, SpellStatBonus.multiply(SpellStat.COST, 0.9, WARD_SPELLS))
            .build());

    public static final JewelrySet SILBROS_GROVE = register(JewelrySet.builder("silbros_grove")
            .piece(MagicTreasuresItems.SILBROS_RING_OF_VITALITY)
            .piece(MagicTreasuresItems.STRONGMANS_BRACERS)
            // 25% more often: a 10 s pulse comes every 8 s
            .bonus(2, SetBonus.of(SpellStatBonus.multiply(SpellStat.EFFECT, 1.15, "healing"),
                    SpellStatBonus.multiply(SpellStat.FREQUENCY, 0.8, "healing")))
            .build());

    public static final JewelrySet MALDRITCHS_REMAINS = register(JewelrySet.builder("maldritchs_remains")
            .piece(MagicTreasuresItems.MALDRITCHS_FIRST_AMULET)
            .piece(MagicTreasuresItems.RING_LIFE_DEATH)
            .piece(MagicTreasuresItems.SKULL_BELT)
            .bonus(2, SpellStatBonus.add(SpellStat.RANGE, 1.0, "drain"))
            .build());

    public static final JewelrySet SELENES_TIDES = register(JewelrySet.builder("selenes_tides")
            .piece(MagicTreasuresItems.AQUA_RING)
            .piece(MagicTreasuresItems.JOURNEYMANS_BANDS)
            .piece(MagicTreasuresItems.EYE_OF_THE_PHOENIX)
            .bonus(2, SpellStatBonus.multiply(SpellStat.DURATION, 1.5, "water_breathing", "night_vision"))
            .build());

    private JewelrySets() {}

    /** Loads this class, so the sets above are registered. */
    public static void init() {
    }

    public static synchronized JewelrySet register(JewelrySet set) {
        SETS.add(set);
        byItem = null;
        return set;
    }

    public static List<JewelrySet> getAll() {
        return Collections.unmodifiableList(SETS);
    }

    public static synchronized Optional<JewelrySet> get(Item item) {
        if (byItem == null) {
            Map<Item, JewelrySet> map = new IdentityHashMap<>();
            for (JewelrySet set : SETS) {
                set.getPieces().forEach(piece -> map.put(piece, set));
            }
            byItem = map;
        }
        return Optional.ofNullable(byItem.get(item));
    }
}
