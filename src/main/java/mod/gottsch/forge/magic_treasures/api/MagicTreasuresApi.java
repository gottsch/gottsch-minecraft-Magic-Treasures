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
package mod.gottsch.forge.magic_treasures.api;

import mod.gottsch.forge.gottschcore.enums.IEnum;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.magic_treasures.core.item.IJewelrySizeTier;
import mod.gottsch.forge.magic_treasures.core.item.IJewelryType;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryMaterial;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryStoneHandler;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryStoneTier;
import mod.gottsch.forge.magic_treasures.core.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Gottschling on 5/4/2023
 */
public class MagicTreasuresApi {
    public static final String RARITY = "rarity";

    public static final String JEWELRY_TYPE = "jewelryType";
    public static final String JEWELRY_SIZE = "jewelrySize";
    public static final String JEWELRY_STONE = "jewelryStone";
    public static final String JEWELRY_MATERIAL = "jewelryMaterial";

    // TODO this has to be renamed to the specific rarity ie for Stone, Material, Jewelry
    public static void registerStoneRarityTag(IRarity rarity, TagKey<Item> tagKey) {
        TagRegistry.registerStoneRarity(rarity, tagKey);
    }

    public static void registerJewelryRarityTag(IRarity rarity, TagKey<Item> tagKey) {
        TagRegistry.registerJewelryRarity(rarity, tagKey);
    }

    public static Optional<TagKey<Item>> getJewelryRarityTag(IRarity rarity) {
        TagKey<Item> key = TagRegistry.getJewelryRarityTag(rarity);
        if (key == null) {
            return Optional.empty();
        }
        else {
            return Optional.of(key);
        }
    }

	public static void registerJewerlyTypeTag(IJewelryType type, TagKey<Item> tagKey) {
		TagRegistry.registerJewelryType(type, tagKey);
	}

//    @Deprecated
//	public static void registerJewerlyStoneTierTag(IJewelryStoneTier tier, TagKey<Item> tierTagKey) {
////		TagRegistry.registerJewelryStoneTier(tier, tierTagKey);
//	}

    public static void registerJewerlyStoneTierTag(JewelryStoneTier tier, TagKey<Item> tierTagKey) {
        TagRegistry.registerJewelryStoneTier(tier, tierTagKey);
    }
	
    /**
     * TODO how to integrate with Treasure2 rarity and API.
     * @param rarity
     */
    public static void registerRarity(IRarity rarity) {
        EnumRegistry.register(RARITY, rarity);
    }

    /**
     *
     * @param key
     * @return
     */
    public static Optional<IRarity> getRarity(String key) {
        IEnum ienum = EnumRegistry.get(RARITY, key);
        if (ienum == null) {
            return Optional.empty();
        }
        else {
            return Optional.of((IRarity) ienum);
        }
    }

    public static List<IRarity> getRarities() {
        List<IEnum> enums = EnumRegistry.getValues(RARITY);
        ArrayList<IRarity> rarities = new ArrayList<>();
        if (!enums.isEmpty()) {
            rarities.addAll(enums.stream().map(e -> (IRarity)e).toList());
        }
        return rarities;
    }

    public static void registerJewelryType(IJewelryType jewelryType) {
        EnumRegistry.register(JEWELRY_TYPE, jewelryType);
    }

	public static List<IJewelryType> getJewelryTypes() {
		List<IEnum> enums = EnumRegistry.getValues(JEWELRY_TYPE);
		ArrayList<IJewelryType> types = new ArrayList<>();
		if (!enums.isEmpty()) {
			types.addAll(enums.stream().map(e -> (IJewelryType)e).toList());
		}
		return types;
	}
	
    public static Optional<IJewelryType> getJewelryType(String key) {
        IEnum ienum = EnumRegistry.get(JEWELRY_TYPE, key);
        if (ienum == null) {
            return Optional.empty();
        }
        else {
            return Optional.of((IJewelryType) ienum);
        }
    }

    public static void registerJewelrySize(IJewelrySizeTier size) {
        EnumRegistry.register(JEWELRY_SIZE, size);
    }

    public static Optional<IJewelrySizeTier> getJewelrySize(String key) {
        IEnum ienum = EnumRegistry.get(JEWELRY_SIZE, key);
        if (ienum == null) {
            return Optional.empty();
        }
        else {
            return Optional.of((IJewelrySizeTier) ienum);
        }
    }

    public static void registerJewelryStoneTier(mod.gottsch.forge.magic_treasures.core.jewelry.JewelryStoneTier tier) {
        JewelryStoneTierRegistry.register(tier);
    }

    public static List<JewelryStoneTier> getJewelryStoneTiers() {
        return JewelryStoneTierRegistry.getStoneTiers();
    }

    public static void registerJewelryMaterial(JewelryMaterial material) {
        JewelryMaterialRegistry.register(material);
    }

    public static Optional<JewelryMaterial> getJewelryMaterial(ResourceLocation name) {
        return JewelryMaterialRegistry.get(name);
    }

    public static Optional<Item> registerJewelryStone(Item item) {
        return StoneRegistry.register(item);
    }

    public static Optional<JewelryStoneHandler> registerJewelryStoneHandler(Item item, JewelryStoneHandler standard) {
        return StoneRegistry.register(item, standard);
    }
}
