/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2024 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.magic_treasures.core.jewelry;

import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;

/**
 * @author Mark Gottschling May 9, 2024
 *
 */
public class JewelryStoneTiers {
    public static final JewelryStoneTier NONE = new JewelryStoneTier.Builder("none", 0, 0).build();
    public static final JewelryStoneTier TIER1 = new JewelryStoneTier.Builder("tier1", 25, 1).build();
    public static final JewelryStoneTier TIER2 = new JewelryStoneTier.Builder("tier2", 50, 1).build();
    public static final JewelryStoneTier TIER3 = new JewelryStoneTier.Builder("tier3", 75, 2)
            .with($ -> {
                $.spellCostFactor = .98;
                $.spellCooldownFactor = .98;
                $.spellFrequencyFactor = .98;
            })
            .build();
    public static final JewelryStoneTier TIER4 = new JewelryStoneTier.Builder("tier4", 100, 2)
            .with($ -> {
                $.spellCostFactor = .96;
                $.spellCooldownFactor = .96;
                $.spellEffectAmountFactor = 1.03;
                $.spellFrequencyFactor = .96;
            })
            .build();
    public static final JewelryStoneTier TIER5 = new JewelryStoneTier.Builder("tier5", 125, 3)
            .with($ -> {
                $.spellCostFactor = 0.94;
                $.spellCooldownFactor = 0.94;
                $.spellEffectAmountFactor = 1.06;
//                $.spellDurationFactor = 1.15;
                $.spellRangeFactor = 1.03;
                $.spellFrequencyFactor = 0.94;
            }).build();

    public static final JewelryStoneTier TIER6 = new JewelryStoneTier.Builder("tier6", 150, 4)
            .with($ -> {
                $.spellCostFactor = 0.92;
                $.spellCooldownFactor = 0.92;
                $.spellEffectAmountFactor = 1.1;
//                $.spellDurationFactor = 1.25;
                $.spellRangeFactor = 1.06;
                $.spellFrequencyFactor = 0.92;
            }).build();

    // POC: Skeleton's Heart gem can only affix to Bone material.
    public static final JewelryStoneTier SKELETONS_HEART =
            new JewelryStoneTier.Builder("skeletons_heart", 100, 3)
                    .with($ -> {
                        $.canAffix = p -> {
                            IJewelryHandler jewelryHandler = p.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);
                            return jewelryHandler.getMaterial().equals(JewelryMaterials.BONE);
                        };
                        $.spellCostFactor = 0.75;
                        $.spellEffectAmountFactor = 1.5;
                        $.spellFrequencyFactor = 1.10;
                        $.spellCooldownFactor = 1.10;
                    }).build();
}
