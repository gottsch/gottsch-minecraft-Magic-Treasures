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
package mod.gottsch.forge.magic_treasures.core.advancement;

import com.google.gson.JsonObject;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.forge.magic_treasures.core.lore.LoreBookDef;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * {@code magictreasures:lore_book}: fires each time a player reads a lore book, and checks what they've read so far.
 * Conditions (all optional; with none, any book read matches):
 * <ul>
 *     <li>{@code book}: they've read this book;</li>
 *     <li>{@code character}: they've read every book of this character ({@code "silbro"});</li>
 *     <li>{@code all}: {@code true} when they've read every book there is.</li>
 * </ul>
 * "Every" means every enabled book in the {@code lore_book} registry, so a pack's books count too.
 *
 * @author Mark Gottschling on 10/14/2026
 */
public class LoreBookTrigger extends SimpleCriterionTrigger<LoreBookTrigger.TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation(MagicTreasures.MOD_ID, "lore_book");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        return new TriggerInstance(player,
                json.has("book") ? Optional.of(new ResourceLocation(GsonHelper.getAsString(json, "book"))) : Optional.empty(),
                json.has("character") ? Optional.of(GsonHelper.getAsString(json, "character")) : Optional.empty(),
                GsonHelper.getAsBoolean(json, "all", false));
    }

    public void trigger(ServerPlayer player, Set<ResourceLocation> read) {
        List<Holder.Reference<LoreBookDef>> books = player.level().registryAccess().registry(MagicTreasuresRegistries.LORE_BOOK)
                .map(registry -> registry.holders().filter(book -> !book.value().disabled()).toList())
                .orElse(List.of());
        this.trigger(player, instance -> instance.matches(read, books));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final Optional<ResourceLocation> book;
        private final Optional<String> character;
        private final boolean all;

        public TriggerInstance(ContextAwarePredicate player, Optional<ResourceLocation> book, Optional<String> character,
                               boolean all) {
            super(ID, player);
            this.book = book;
            this.character = character;
            this.all = all;
        }

        public boolean matches(Set<ResourceLocation> read, List<Holder.Reference<LoreBookDef>> books) {
            if (book.isPresent() && !read.contains(book.get())) {
                return false;
            }
            if (character.isPresent() && !readEvery(read, books.stream()
                    .filter(b -> b.value().character().equals(character)).toList())) {
                return false;
            }
            return !all || readEvery(read, books);
        }

        /** every one of {@code books} read; never true for no books, so an empty set can't grant an advancement */
        private static boolean readEvery(Set<ResourceLocation> read, List<Holder.Reference<LoreBookDef>> books) {
            return !books.isEmpty() && books.stream().allMatch(b -> read.contains(b.key().location()));
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            book.ifPresent(id -> json.addProperty("book", id.toString()));
            character.ifPresent(c -> json.addProperty("character", c));
            if (all) {
                json.addProperty("all", true);
            }
            return json;
        }
    }
}
