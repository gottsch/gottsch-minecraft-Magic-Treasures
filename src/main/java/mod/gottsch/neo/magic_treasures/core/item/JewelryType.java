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
package mod.gottsch.neo.magic_treasures.core.item;

import mod.gottsch.neo.gottschcore.enums.IEnum;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by Mark Gottschling on 5/29/2023
 */
public enum JewelryType implements IJewelryType {
	UNKNOWN(-1, "unknown"),
	RING(0, "ring"),
    NECKLACE(1, "necklace"),
    BRACELET(2, "bracelet"),
    POCKET(3, "pocket"),
    EARRING(4, "earring"),
	BROACH(5, "broach"),
	CHARM(6, "charm"),
	BELT(7, "belt");
	
	private static final Map<Integer, IEnum> codes = new HashMap<Integer, IEnum>();
	private static final Map<String, IEnum> values = new HashMap<String, IEnum>();
	private Integer code;
	private String value;
	
	// setup reverse lookup
	static {
		for (IJewelryType x : EnumSet.allOf(JewelryType.class)) {
			codes.put(x.getCode(), x);
			values.put(x.getValue(), x);
		}
	}
	
	/**
	 * Full constructor
	 * @param code
	 * @param value
	 */
	JewelryType(Integer code, String value) {
		this.code = code;
		this.value = value;
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
	public static IJewelryType getByCode(Integer code) {
		return (JewelryType) codes.get(code);
	}
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static IJewelryType getByValue(String value) {
		return (JewelryType) values.get(value);
	}

	@Override
	public Map<Integer, IEnum> getCodes() {
		return codes;
	}
	@Override
	public Map<String, IEnum> getValues() {
		return values;
	}
	
	/**
	 * 
	 * @return
	 */
	public static List<String> getNames() {
		List<String> names = EnumSet.allOf(JewelryType.class).stream().map(x -> x.name()).collect(Collectors.toList());
		return names;
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
