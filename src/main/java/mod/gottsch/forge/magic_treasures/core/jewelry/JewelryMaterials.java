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

/**
 * @author Mark Gottschling May 8, 2024
 *
 */
// TODO setup each metal for cost, effect, duration, freq, range modifiers
public class JewelryMaterials {
    public static final JewelryMaterial NONE = new JewelryMaterial.Builder("none", 0, 0)
            .with($ -> {
                $.maxLevel = 0;
                $.acceptsAffixer = a -> false;
            }).build();

    public static final JewelryMaterial WOOD = new JewelryMaterial.Builder("wood", 40, 20)
            .with(
                    $ -> $.maxLevel = 2
            ).build();

    public static final JewelryMaterial IRON = new JewelryMaterial.Builder("iron", 200, 50)
            .with($ -> {
                $.repairs = 3;
                $.maxLevel = 3;
                $.spellDurationFactor = 1.25;
            }).build();

    public static final JewelryMaterial COPPER = new JewelryMaterial.Builder("copper", 75, 50)
            .with($ -> {
                $.repairs = 1;
                $.maxLevel = 4;
                $.spellCooldownFactor = 0.95;
                $.spellFrequencyFactor = 0.95;
            }).build();

    public static final JewelryMaterial SILVER = new JewelryMaterial.Builder("silver", 150, 75)
            .with($ -> {
                $.repairs = 2;
                $.maxLevel = 5;
                $.spellCostFactor = 0.95;
            }).build();

    public static final JewelryMaterial GOLD = new JewelryMaterial.Builder("gold", 150, 100)
            .with($ -> {
                $.repairs = 2;
                $.maxLevel = 6;
                $.spellCostFactor = 0.925;
                $.spellCooldownFactor = 0.96;
                $.spellFrequencyFactor = 0.96;
            }).build();

    public static final JewelryMaterial BONE = new JewelryMaterial.Builder("bone", 200, 50)
            .with($ -> {
                $.repairs = 0;
                $.maxLevel = 7;
                $.spellCostFactor = 1.05;
                $.spellEffectAmountFactor = 1.25;
            }).build();

    // aka hemalsteel
    public static final JewelryMaterial BLOOD = new JewelryMaterial.Builder("blood", 250, 100)
            .with($ -> {
                $.repairs = 2;
                $.maxLevel = 8;
                $.spellCostFactor = 0.90;
            }).build();

    public static final JewelryMaterial SHADOW = new JewelryMaterial.Builder("shadow", 275, 125)
            .with($ -> {
                $.repairs = 3;
                $.maxLevel = 9;
                $.spells = 2;
                $.spellCostFactor = 1.1; // NOTE costs more!
                $.spellEffectAmountFactor = 1.5;
            }).build();

    public static final JewelryMaterial ATIUM = new JewelryMaterial.Builder("atium", 325, 200)
            .with($ -> {
                $.repairs = 3;
                $.maxLevel = 10;
                $.spells = 2;
                $.spellCostFactor = 0.9;
                $.spellCooldownFactor = 0.9;
                $.spellDurationFactor = 1.5;
                $.spellEffectAmountFactor = 1.12;
                $.spellFrequencyFactor = 0.9;
                $.spellRangeFactor = 1.1;
            }).build();
}
