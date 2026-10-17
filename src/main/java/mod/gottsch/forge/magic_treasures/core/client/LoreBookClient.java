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
package mod.gottsch.forge.magic_treasures.core.client;

import mod.gottsch.forge.magic_treasures.core.lore.LoreBookDef;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Opens a lore book in Magic Treasures' reader ({@link LoreBookScreen}). Client-only: called from
 * {@code LoreBook.use} on the client side.
 *
 * @author Mark Gottschling on 10/14/2026
 */
public final class LoreBookClient {
    private LoreBookClient() {}

    public static void open(ResourceLocation id, ItemStack book, LoreBookDef def) {
        Map<Integer, List<ItemStack>> pictures = new TreeMap<>();
        for (LoreBookDef.Illustration illustration : def.illustrations()) {
            illustration.stack().ifPresent(stack -> pictures.computeIfAbsent(illustration.after(), k -> new ArrayList<>()).add(stack));
        }
        Minecraft.getInstance().setScreen(new LoreBookScreen(id, book.copyWithCount(1),
                Component.translatable(LoreBookDef.titleKey(id)), Component.translatable(LoreBookDef.authorKey(id)),
                def.pageText(id), pictures));
    }
}
