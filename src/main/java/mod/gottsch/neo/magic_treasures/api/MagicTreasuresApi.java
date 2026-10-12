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
package mod.gottsch.neo.magic_treasures.api;

import mod.gottsch.neo.gottschcore.enums.IEnum;
import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.item.GenericJewelry;
import mod.gottsch.neo.magic_treasures.core.item.IJewelrySizeTier;
import mod.gottsch.neo.magic_treasures.core.item.IJewelryType;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryPresetDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryRules;
import mod.gottsch.neo.magic_treasures.core.registry.EnumRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Magic Treasures' API for other mods.
 * <p>
 * Since 2.0, jewelry is data: materials, styles, stone tiers, gemstones and presets are datapack registries (keys in
 * {@link MagicTreasuresRegistries}), and a piece of jewelry is one of three generic items (ring, necklace,
 * bracelet) whose {@link JewelryIdentity} component says what it is. Add content with JSON, not code; use the stack
 * helpers here to make, recognise and read jewelry. The registries are only filled once a world's data is loaded, so
 * the helpers that check them return empty before that.
 *
 * Created by Mark Gottschling on 5/4/2023
 */
public class MagicTreasuresApi {
    public static final String RARITY = "rarity";

    /** @deprecated 2.0: jewelry types are the generic items' types; removed in 2.1 */
    @Deprecated(since = "2.0", forRemoval = true)
    public static final String JEWELRY_TYPE = "jewelryType";
    /** @deprecated 2.0: sizes are {@code magictreasures:jewelry_style} entries; removed in 2.1 */
    @Deprecated(since = "2.0", forRemoval = true)
    public static final String JEWELRY_SIZE = "jewelrySize";
    /** @deprecated 2.0: unused; removed in 2.1 */
    @Deprecated(since = "2.0", forRemoval = true)
    public static final String JEWELRY_STONE = "jewelryStone";
    /** @deprecated 2.0: unused; removed in 2.1 */
    @Deprecated(since = "2.0", forRemoval = true)
    public static final String JEWELRY_MATERIAL = "jewelryMaterial";

    /*
     * jewelry stacks
     */

    /** whether the stack is Magic Treasures jewelry (a generic item with an identity) */
    public static boolean isJewelry(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.has(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
    }

    /** what a piece of jewelry is: type, material, gem, style and preset */
    public static Optional<JewelryIdentity> getIdentity(ItemStack stack) {
        return isJewelry(stack) ? Optional.of(stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get())) : Optional.empty();
    }

    /** a write-through view of a piece of jewelry: its stats, uses, mana, gem and spells */
    public static Optional<IJewelryHandler> getJewelry(ItemStack stack) {
        return JewelryHandler.get(stack);
    }

    /**
     * A new piece of jewelry. Empty if the type has no generic item, or the material, gem or style isn't loaded (or is
     * disabled). It doesn't check the rules (which combinations crafting and loot make); see {@link #isAllowed}.
     *
     * @param type     {@code "ring"}, {@code "necklace"} or {@code "bracelet"}
     * @param material a {@code magictreasures:jewelry_material} id
     * @param gem      a {@code magictreasures:gemstone} id, or empty for no gem
     * @param style    a {@code magictreasures:jewelry_style} id ({@code magictreasures:regular} for plain jewelry)
     */
    public static Optional<ItemStack> createJewelry(String type, ResourceLocation material, Optional<ResourceLocation> gem,
            ResourceLocation style) {
        if (JewelryDataIndex.material(material).isEmpty() || JewelryDataIndex.style(style).isEmpty()
                || gem.map(id -> JewelryDataIndex.gemstone(id).isEmpty()).orElse(false)) {
            return Optional.empty();
        }
        return create(new JewelryIdentity(type, material, gem, style, Optional.empty()));
    }

    /** A named piece from a {@code magictreasures:jewelry_preset}, with its spells. Empty if the preset isn't loaded. */
    public static Optional<ItemStack> createPreset(ResourceLocation preset) {
        return JewelryDataIndex.preset(preset).flatMap(holder -> {
            JewelryPresetDef def = holder.value();
            return create(new JewelryIdentity(def.type(), def.material(), def.gem(), def.style(), Optional.of(preset)));
        });
    }

    /** whether the rules allow this combination, i.e. whether crafting, loot and the creative tab would make it */
    public static boolean isAllowed(String type, ResourceLocation material, Optional<ResourceLocation> gem, ResourceLocation style) {
        var materialDef = JewelryDataIndex.material(material);
        var styleDef = JewelryDataIndex.style(style);
        var gemDef = gem.flatMap(JewelryDataIndex::gemstone);
        if (materialDef.isEmpty() || styleDef.isEmpty() || (gem.isPresent() && gemDef.isEmpty())) {
            return false;
        }
        return JewelryRules.allows(type, materialDef.get(), styleDef.get(), gemDef.map(g -> g));
    }

    /** uses, mana, repairs and recharges start full the first time the stack is read */
    private static Optional<ItemStack> create(JewelryIdentity identity) {
        return GenericJewelry.forType(identity.type()).map(item -> item.stack(identity));
    }

    /*
     * rarity
     */

    /**
     * TODO how to integrate with Treasure2 rarity and API.
     * @param rarity
     */
    public static void registerRarity(IRarity rarity) {
        EnumRegistry.register(RARITY, rarity);
    }

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

    /*
     * 1.x jewelry types and sizes
     */

    /** @deprecated 2.0: a type is a generic jewelry item; removed in 2.1 */
    @Deprecated(since = "2.0", forRemoval = true)
    public static void registerJewelryType(IJewelryType jewelryType) {
        EnumRegistry.register(JEWELRY_TYPE, jewelryType);
    }

    /** @deprecated 2.0: see {@link IJewelryHandler#getType()}; removed in 2.1 */
    @Deprecated(since = "2.0", forRemoval = true)
    public static List<IJewelryType> getJewelryTypes() {
        List<IEnum> enums = EnumRegistry.getValues(JEWELRY_TYPE);
        ArrayList<IJewelryType> types = new ArrayList<>();
        if (!enums.isEmpty()) {
            types.addAll(enums.stream().map(e -> (IJewelryType)e).toList());
        }
        return types;
    }

    /** @deprecated 2.0: see {@link IJewelryHandler#getType()}; removed in 2.1 */
    @Deprecated(since = "2.0", forRemoval = true)
    public static Optional<IJewelryType> getJewelryType(String key) {
        IEnum ienum = EnumRegistry.get(JEWELRY_TYPE, key);
        if (ienum == null) {
            return Optional.empty();
        }
        else {
            return Optional.of((IJewelryType) ienum);
        }
    }

    /** @deprecated 2.0: sizes are {@code magictreasures:jewelry_style} JSON; removed in 2.1 */
    @Deprecated(since = "2.0", forRemoval = true)
    public static void registerJewelrySize(IJewelrySizeTier size) {
        EnumRegistry.register(JEWELRY_SIZE, size);
    }

    /** @deprecated 2.0: see {@link IJewelryHandler#getStyle()}; removed in 2.1 */
    @Deprecated(since = "2.0", forRemoval = true)
    public static Optional<IJewelrySizeTier> getJewelrySize(String key) {
        IEnum ienum = EnumRegistry.get(JEWELRY_SIZE, key);
        if (ienum == null) {
            return Optional.empty();
        }
        else {
            return Optional.of((IJewelrySizeTier) ienum);
        }
    }
}
