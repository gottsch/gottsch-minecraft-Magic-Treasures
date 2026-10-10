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

import mod.gottsch.neo.magic_treasures.core.spell.ISpell;

import java.util.Set;

/**
 * Changes one number of the given spell types: {@code value * factor + add}.
 * Example: ward spells cost 10% less is {@code (Set.of(...ward types), COST, 0.9, 0)}.
 */
public class SpellStatBonus implements SetBonus {
    private final Set<String> spellTypes;
    private final SpellStat stat;
    private final double factor;
    private final double add;

    public SpellStatBonus(Set<String> spellTypes, SpellStat stat, double factor, double add) {
        this.spellTypes = spellTypes;
        this.stat = stat;
        this.factor = factor;
        this.add = add;
    }

    public static SpellStatBonus multiply(SpellStat stat, double factor, String... spellTypes) {
        return new SpellStatBonus(Set.of(spellTypes), stat, factor, 0);
    }

    public static SpellStatBonus add(SpellStat stat, double add, String... spellTypes) {
        return new SpellStatBonus(Set.of(spellTypes), stat, 1.0, add);
    }

    @Override
    public double modify(SpellStat stat, ISpell spell, double value) {
        if (stat != this.stat || !spellTypes.contains(spell.getType())) {
            return value;
        }
        return value * factor + add;
    }
}
