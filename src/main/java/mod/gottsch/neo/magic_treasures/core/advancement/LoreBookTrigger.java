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
package mod.gottsch.neo.magic_treasures.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.neo.magic_treasures.core.lore.LoreBookDef;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

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
 */
public class LoreBookTrigger extends SimpleCriterionTrigger<LoreBookTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Set<ResourceLocation> read) {
        List<Holder.Reference<LoreBookDef>> books = player.registryAccess().registry(MagicTreasuresRegistries.LORE_BOOK)
                .map(registry -> registry.holders().filter(book -> !book.value().disabled()).toList())
                .orElse(List.of());
        this.trigger(player, instance -> instance.matches(read, books));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ResourceLocation> book,
                                  Optional<String> character, boolean all) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                ResourceLocation.CODEC.optionalFieldOf("book").forGetter(TriggerInstance::book),
                Codec.STRING.optionalFieldOf("character").forGetter(TriggerInstance::character),
                Codec.BOOL.optionalFieldOf("all", false).forGetter(TriggerInstance::all)
        ).apply(instance, TriggerInstance::new));

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
    }
}
