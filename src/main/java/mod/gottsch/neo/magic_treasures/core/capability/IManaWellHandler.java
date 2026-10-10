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
package mod.gottsch.neo.magic_treasures.core.capability;

/**
 * A mana well's state: a worn or held reservoir that pays the part of a spell's cost its jewelry can't.
 *
 * @author Mark Gottschling on 10/9/2026
 */
public interface IManaWellHandler {
    double getMaxMana();

    double getMana();

    void setMana(double mana);

    int getMaxRecharges();

    int getRecharges();

    void setRecharges(int recharges);
}
