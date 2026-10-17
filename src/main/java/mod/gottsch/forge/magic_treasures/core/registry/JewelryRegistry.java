/*
 * This file is part of  GealdronCraft.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * GealdronCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GealdronCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GealdronCraft.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.magic_treasures.core.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryMaterial;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryStoneTier;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryStoneTiers;
import mod.gottsch.forge.magic_treasures.core.registry.support.JewelryRegistryKey;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.Map.Entry;

/**
 * Registers all the jewelery items by ResourceLocation, and MaterialType and Key(Material, Stone, Size)
 * @author Mark Gottschling Jul 6, 2023
 *
 */
public class JewelryRegistry {
	private static final Map<ResourceLocation, Item> NAME_MAP = Maps.newHashMap();
	private static final Multimap<JewelryRegistryKey, Item> KEY_MAP = ArrayListMultimap.create();
	private static final Multimap<IRarity, Item> RARITY_MAP = ArrayListMultimap.create();

	/**
	 * Note: this registers a known item.
	 * @param item
	 */
	public static void register(Item item) {		
		ItemStack stack = new ItemStack(item);
		stack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).ifPresent(c -> {
			NAME_MAP.put(ModUtil.getName(item), item);

			// get the stone
			Item stone = null;
			JewelryStoneTier stoneTier = JewelryStoneTiers.NONE;
			if (c.hasStone()) {
				Optional<Item> optionalStone = StoneRegistry.get(c.getStone());
				if (optionalStone.isPresent()) {
					stone = optionalStone.get();
					Optional<JewelryStoneTier> tier = StoneRegistry.getStoneTier(stone);
					if (tier.isPresent()) {
						stoneTier = tier.get();
					}
				}

			}

			// generate keys
			JewelryRegistryKey key = new JewelryRegistryKey(
						c.getJewelryType(),
						c.getMaterial(),
						stoneTier,
						c.getJewelrySizeTier());

			KEY_MAP.put(key, item);
		});
	}

	/**
	 * registered during TagsUpdatedEvent.
	 *
	 * @param rarity
	 * @param item
	 * @return
	 */
	public static boolean register(IRarity rarity, Item item) {
		return RARITY_MAP.put(rarity, item);
	}
		
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Optional<Item> get(ResourceLocation name) {
		return Optional.ofNullable(NAME_MAP.get(name));
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static List<Item> get(JewelryRegistryKey key) {
		return new ArrayList<>(KEY_MAP.get(key));
	}
	
	/**
	 * 
	 * @param material
	 * @return
	 */
	public static List<Item> get(JewelryMaterial material) {
		List<Item> list = KEY_MAP.entries()
				.stream()
				.filter(e -> e.getKey().getMaterial().equals(material))
				.map(e -> e.getValue())
				.toList();
		return list;
	}

	public static List<Item> get(IRarity rarity) {
		List<Item> list = new ArrayList<>();
		if (RARITY_MAP.containsKey(rarity)) {
			list.addAll(RARITY_MAP.get(rarity));
		}
		return list;
	}

	/**
	 * 
	 */
	public static void clear() {
		NAME_MAP.clear();
		KEY_MAP.clear();
		RARITY_MAP.clear();
	}

	public static List<Item> getAll() {
		return new ArrayList<>(NAME_MAP.values());
	}
	
	public static Set<Entry<ResourceLocation, Item>> getNameEntries() {
		return NAME_MAP.entrySet();
	}
	
	public static Collection<Entry<JewelryRegistryKey, Item>> getKeyEntries() {
		return KEY_MAP.entries();
	}
}
