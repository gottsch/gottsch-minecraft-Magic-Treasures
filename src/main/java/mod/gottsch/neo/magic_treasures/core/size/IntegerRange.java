/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.magic_treasures.core.size;

import mod.gottsch.neo.gottschcore.random.RandomHelper;

import java.util.Objects;
import java.util.Random;

/**
 * Created by Mark Gottschling on 6/1/2023
 */
public class IntegerRange {
    public static final IntegerRange EMPTY = new IntegerRange(0, 0);

    private int min;
    private int max;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getInRange() {
        return getInRange(new Random());
    }

    public int getInRange(Random random) {
        return RandomHelper.randomInt(random, getMin(), getMax());
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerRange that = (IntegerRange) o;
        return min == that.min && max == that.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    @Override
    public String toString() {
        return "IntegerRange{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
