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
package mod.gottsch.forge.magic_treasures.core.loot.function;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.function.Predicate;

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
     * A filter over a 2.0 registry from loot JSON: each name is an id or a {@code #tag} of that registry (a bare name
     * defaults to the magictreasures namespace). An empty list lets everything through.
     */
    public static <T> Predicate<Holder<T>> holders(ResourceKey<? extends Registry<T>> registry, List<String> names) {
        if (names.isEmpty()) {
            return holder -> true;
        }
        List<Predicate<Holder<T>>> tests = names.stream().<Predicate<Holder<T>>>map(name -> {
            if (name.startsWith("#")) {
                TagKey<T> tag = TagKey.create(registry, ModUtil.asLocation(name.substring(1)));
                return holder -> holder.is(tag);
            }
            ResourceLocation id = ModUtil.asLocation(name);
            return holder -> holder.is(id);
        }).toList();
        return holder -> tests.stream().anyMatch(test -> test.test(holder));
    }

    public static List<String> names(List<ResourceLocation> locations) {
        return locations.stream().map(ResourceLocation::toString).toList();
    }

    /** a JSON array of strings, or empty when the key is missing */
    public static List<String> readStrings(JsonObject json, String key) {
        if (!json.has(key)) {
            return List.of();
        }
        List<String> list = new ArrayList<>();
        GsonHelper.getAsJsonArray(json, key).forEach(element -> list.add(GsonHelper.convertToString(element, key)));
        return list;
    }

    /** writes a JSON array of strings; nothing when the list is empty */
    public static void writeStrings(JsonObject json, String key, List<String> values) {
        if (!values.isEmpty()) {
            JsonArray array = new JsonArray();
            values.forEach(array::add);
            json.add(key, array);
        }
    }
}
