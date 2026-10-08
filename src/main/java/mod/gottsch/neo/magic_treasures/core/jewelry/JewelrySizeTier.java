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
package mod.gottsch.neo.magic_treasures.core.jewelry;

import mod.gottsch.neo.gottschcore.enums.IEnum;
import mod.gottsch.neo.magic_treasures.core.item.IJewelrySizeTier;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Mark Gottschling on 5/29/2023
 */
public enum JewelrySizeTier implements IJewelrySizeTier {
    UNKNOWN(-1, "unknown", 0f, 0f, 0),
    REGULAR(0, "regular", 1f, 1f, 0),
    GREAT(1, "great", 2f, 1.5f, 1),
    LORDS(2, "lords", 3f, 2f, 2);

    private static final Map<Integer, IEnum> codes = new HashMap<Integer, IEnum>();
    private static final Map<String, IEnum> values = new HashMap<String, IEnum>();
    private Integer code;
    private String value;

    private final float usesMultiplier;
    private final float manaMultiplier;
    private final int repairs;

    // setup reverse lookup
    static {
        for (IJewelrySizeTier x : EnumSet.allOf(JewelrySizeTier.class)) {
            codes.put(x.getCode(), x);
            values.put(x.getValue(), x);
        }
    }

    /**
     * Full constructor
     * @param code
     * @param value
     */
    JewelrySizeTier(Integer code, String value, float usesMultiplier, float manaMultiplier, int repairs) {
        this.code = code;
        this.value = value;
        this.usesMultiplier = usesMultiplier;
        this.manaMultiplier = manaMultiplier;
        this.repairs = repairs;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     *
     * @param code
     * @return
     */
    public static IJewelrySizeTier getByCode(Integer code) {
        return (JewelrySizeTier) codes.get(code);
    }
    /**
     *
     * @param value
     * @return
     */
    public static IJewelrySizeTier getByValue(String value) {
        return (JewelrySizeTier) values.get(value);
    }

    @Override
    public Map<Integer, IEnum> getCodes() {
        return codes;
    }
    @Override
    public Map<String, IEnum> getValues() {
        return values;
    }

    @Override
    public float getUsesMultiplier() {
        return usesMultiplier;
    }

    @Override
    public float getManaMultiplier() {
        return manaMultiplier;
    }

    @Override
    public int getRepairs() {
        return repairs;
    }

    /**
     *
     * @return
     */
    public static List<String> getNames() {
        List<String> names = EnumSet.allOf(JewelrySizeTier.class).stream().map(x -> x.name()).collect(Collectors.toList());
        return names;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
