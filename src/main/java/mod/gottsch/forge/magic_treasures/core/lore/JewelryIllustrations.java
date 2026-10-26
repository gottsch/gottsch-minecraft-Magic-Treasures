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
package mod.gottsch.forge.magic_treasures.core.lore;

import mod.gottsch.forge.gottschbooks.api.Illustrations;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Magic Treasures' pictures for Gottsch Books (lore books and the manual):
 * <ul>
 *     <li>{@code preset:magictreasures:ring_of_life_death}: a named piece, with its spells;</li>
 *     <li>{@code jewelry:magictreasures:<type>/<material>[/<gem>[/<style>]]}: any combination, e.g.
 *     {@code jewelry:magictreasures:ring/gold/ruby} or {@code jewelry:magictreasures:necklace/bone/none/great}
 *     ({@code none} for no gem). Material, gem and style are ids in the reference's namespace; the style
 *     defaults to {@code regular}.</li>
 * </ul>
 * Used as {@code ![caption](preset:...)}, a {@code ```spotlight} or an {@code icon:}.
 *
 * @author Mark Gottschling on 10/17/2026
 */
public final class JewelryIllustrations {

    private JewelryIllustrations() {}

    public static void register() {
        Illustrations.register("preset", MagicTreasuresApi::createPreset);
        Illustrations.register("jewelry", JewelryIllustrations::jewelry);
    }

    private static Optional<ItemStack> jewelry(ResourceLocation id) {
        String[] parts = id.getPath().split("/");
        if (parts.length < 2) {
            return Optional.empty();
        }
        String namespace = id.getNamespace();
        Optional<ResourceLocation> gem = parts.length > 2 && !parts[2].equals("none")
                ? Optional.of(new ResourceLocation(namespace, parts[2])) : Optional.empty();
        ResourceLocation style = new ResourceLocation(namespace, parts.length > 3 ? parts[3] : "regular");
        return MagicTreasuresApi.createJewelry(parts[0], new ResourceLocation(namespace, parts[1]), gem, style);
    }
}
