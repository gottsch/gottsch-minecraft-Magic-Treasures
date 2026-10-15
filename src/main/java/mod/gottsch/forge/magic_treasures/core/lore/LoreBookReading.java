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

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.advancement.MagicTreasuresCriteria;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Which lore books a player has read, so the Archivist and per-character advancements can ask "has this player read
 * all of X?". Kept in the player's {@code PlayerPersisted} NBT, which Forge copies through death (1.21.1 uses a data
 * attachment with {@code copyOnDeath}; same key, {@code magictreasures:read_lore_books}).
 *
 * @author Mark Gottschling on 10/14/2026
 */
public final class LoreBookReading {
    private static final String KEY = MagicTreasures.MOD_ID + ":read_lore_books";

    private LoreBookReading() {}

    public static Set<ResourceLocation> read(Player player) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        Set<ResourceLocation> read = new HashSet<>();
        for (Tag tag : persisted.getList(KEY, Tag.TAG_STRING)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getAsString());
            if (id != null) {
                read.add(id);
            }
        }
        return Set.copyOf(read);
    }

    /** Records that {@code player} read {@code book}, then lets the lore advancements check what they've read. */
    public static void markRead(ServerPlayer player, ResourceLocation book) {
        Set<ResourceLocation> read = read(player);
        if (!read.contains(book)) {
            Set<ResourceLocation> updated = new HashSet<>(read);
            updated.add(book);
            read = Set.copyOf(updated);
            ListTag list = new ListTag();
            read.forEach(id -> list.add(StringTag.valueOf(id.toString())));
            CompoundTag data = player.getPersistentData();
            CompoundTag persisted = data.getCompound(Player.PERSISTED_NBT_TAG);
            persisted.put(KEY, list);
            data.put(Player.PERSISTED_NBT_TAG, persisted);
        }
        MagicTreasuresCriteria.LORE_BOOK.trigger(player, read);
    }
}
