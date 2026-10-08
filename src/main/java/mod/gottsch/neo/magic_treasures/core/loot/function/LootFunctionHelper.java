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
package mod.gottsch.neo.magic_treasures.core.loot.function;

import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.neo.magic_treasures.core.registry.JewelryMaterialRegistry;
import mod.gottsch.neo.magic_treasures.core.util.ModUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Resolves the raw JSON values of the loot functions the same way the 1.20.1 Gson serializers did.
 * The codecs read raw strings (not ResourceLocation.CODEC) so that bare names keep defaulting to
 * the magictreasures namespace (ModUtil.asLocation).
 */
public final class LootFunctionHelper {
    private LootFunctionHelper() {}

    public static IRarity rarity(String name, IRarity fallback) {
        return MagicTreasuresApi.getRarity(name).orElse(fallback);
    }

    /**
     * unknown names are dropped
     */
    public static List<IRarity> rarities(List<String> names) {
        return names.stream()
                .map(n -> MagicTreasuresApi.getRarity(n).orElse(MagicTreasuresRarity.NONE))
                .filter(r -> r != MagicTreasuresRarity.NONE)
                .toList();
    }

    public static List<String> rarityNames(List<IRarity> rarities) {
        return rarities.stream().map(IRarity::getName).toList();
    }

    public static List<ResourceLocation> locations(List<String> names) {
        return names.stream().map(ModUtil::asLocation).toList();
    }

    /**
     * unregistered materials are dropped
     */
    public static List<ResourceLocation> materials(List<String> names) {
        return locations(names).stream().filter(JewelryMaterialRegistry::has).toList();
    }

    public static List<String> names(List<ResourceLocation> locations) {
        return locations.stream().map(ResourceLocation::toString).toList();
    }
}
