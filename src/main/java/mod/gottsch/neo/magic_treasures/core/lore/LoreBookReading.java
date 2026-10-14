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
package mod.gottsch.neo.magic_treasures.core.lore;

import com.mojang.serialization.Codec;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.advancement.MagicTreasuresCriteria;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Which lore books a player has read: a data attachment on the player, saved with them and kept through death, so the
 * Archivist and per-character advancements can ask "has this player read all of X?".
 *
 * @author Mark Gottschling on 10/14/2026
 */
public final class LoreBookReading {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MagicTreasures.MOD_ID);

    /** the ids of the books read; stored as a list, held as an unmodifiable set */
    private static final Codec<Set<ResourceLocation>> CODEC =
            ResourceLocation.CODEC.listOf().xmap(Set::copyOf, List::copyOf);

    public static final Supplier<AttachmentType<Set<ResourceLocation>>> READ_LORE_BOOKS = ATTACHMENT_TYPES.register(
            "read_lore_books", () -> AttachmentType.<Set<ResourceLocation>>builder(() -> Set.of())
                    .serialize(CODEC)
                    .copyOnDeath()
                    .build());

    private LoreBookReading() {}

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    public static Set<ResourceLocation> read(Player player) {
        return player.getData(READ_LORE_BOOKS);
    }

    /** Records that {@code player} read {@code book}, then lets the lore advancements check what they've read. */
    public static void markRead(ServerPlayer player, ResourceLocation book) {
        Set<ResourceLocation> read = read(player);
        if (!read.contains(book)) {
            Set<ResourceLocation> updated = new HashSet<>(read);
            updated.add(book);
            read = Set.copyOf(updated);
            player.setData(READ_LORE_BOOKS, read);
        }
        MagicTreasuresCriteria.LORE_BOOK.get().trigger(player, read);
    }
}
