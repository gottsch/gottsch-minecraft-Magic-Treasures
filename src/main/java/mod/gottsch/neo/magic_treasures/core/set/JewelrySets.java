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

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraft.resources.ResourceLocation;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
            .piece(preset("salandaars_ward"))
            .piece(preset("ring_of_fortitude"))
            .piece(preset("smiths_cuff"))
            .piece(MagicTreasuresItems.STONEMARK_HAMMER)
            .piece(MagicTreasuresItems.SMITHS_HELM)
            .piece(MagicTreasuresItems.ANVIL_SHIELD)
            .bonus(2, SpellStatBonus.multiply(SpellStat.COST, 0.9, WARD_SPELLS))
            .bonus(4, SpellStatBonus.multiply(SpellStat.COOLDOWN, 0.85, WARD_SPELLS))
            .bonus(6, new TemperedBonus(() -> JewelrySets.SALANDAARS_ANVILWORK))
            .build());

    public static final JewelrySet SILBROS_GROVE = register(JewelrySet.builder("silbros_grove")
            .piece(preset("silbros_ring_of_vitality"))
            .piece(preset("strongmans_bracers"))
            .piece(preset("silbros_acorn"))
            .piece(MagicTreasuresItems.ROOTSTAFF)
            .piece(MagicTreasuresItems.BARKSKIN_VEST)
            // 25% more often: a 10 s wait becomes 8 s
            .bonus(2, SetBonus.of(SpellStatBonus.multiply(SpellStat.EFFECT, 1.15, "healing"),
                    SpellStatBonus.multiply(SpellStat.COOLDOWN, 0.8, "healing")))
            .bonus(4, new RegrowthBonus())
            .bonus(5, new TakeRootBonus(() -> JewelrySets.SILBROS_GROVE))
            .build());

    public static final JewelrySet MALDRITCHS_REMAINS = register(JewelrySet.builder("maldritchs_remains")
            .piece(preset("maldritchs_first_amulet"))
            .piece(preset("ring_of_life_death"))
            .piece(MagicTreasuresItems.SKULL_BELT)
            .bonus(2, SpellStatBonus.add(SpellStat.RANGE, 1.0, "drain"))
            .build());

    public static final JewelrySet SELENES_TIDES = register(JewelrySet.builder("selenes_tides")
            .piece(preset("aqua_ring"))
            .piece(preset("journeyman_bands"))
            .piece(preset("eye_of_the_phoenix"))
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

    /** the set an item piece belongs to (1.x jewelry, armor, weapons) */
    public static synchronized Optional<JewelrySet> get(Item item) {
        if (byItem == null) {
            Map<Item, JewelrySet> map = new IdentityHashMap<>();
            for (JewelrySet set : SETS) {
                set.getItems().forEach(piece -> map.put(piece, set));
            }
            byItem = map;
        }
        return Optional.ofNullable(byItem.get(item));
    }

    /** the set a stack belongs to: by its item, or by its 2.0 jewelry preset */
    public static Optional<JewelrySet> get(ItemStack stack) {
        Optional<JewelrySet> byItemSet = get(stack.getItem());
        if (byItemSet.isPresent()) {
            return byItemSet;
        }
        JewelryIdentity identity = stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
        if (identity == null || identity.preset().isEmpty()) {
            return Optional.empty();
        }
        return SETS.stream().filter(set -> set.getPresets().contains(identity.preset().get())).findFirst();
    }

    /** a set piece that is a 2.0 preset (M9: the 1.x named items are gone; M8's presets have the same ids) */
    private static ResourceLocation preset(String path) {
        return ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, path);
    }
}
