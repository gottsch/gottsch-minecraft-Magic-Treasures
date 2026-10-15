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
package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A set of entries from one of Magic Treasures' datapack registries, as the client receives it on Forge 1.20.1: the
 * ids and {@code #tags} as written, resolved against the client's registries the first time it is used.
 * <p>
 * Why: 1.20.1 sends datapack registries to a joining client with ops that only know the built-in registries
 * ({@code ClientboundLoginPacket}'s {@code BUILTIN_CONTEXT_OPS}), so a synced registry's network codec can't refer to
 * another datapack registry ({@code RegistryCodecs.homogeneousList} fails with "Can't access registry"). A material's
 * {@code styles} and {@code gems}, a preset's {@code gems} and a gem's {@code affixes_to} do. Their network codec
 * ({@link #codec}) sends the entries as strings instead. On a dedicated server only: single player passes packets
 * without encoding them. NeoForge 1.21.1 syncs registries with full registry access and doesn't need this.
 * <p>
 * Resolution waits for {@link JewelryDataIndex}, which is built once the client has the registries and their tags,
 * and is redone whenever the index is rebuilt. Unknown ids and empty tags resolve to nothing.
 *
 * @author Mark Gottschling on 10/15/2026
 */
public final class NetworkHolderSet<T> extends HolderSet.ListBacked<T> {
    private final ResourceKey<? extends Registry<T>> registry;
    /** {@code "ns:id"} or {@code "#ns:tag"}, as sent */
    private final List<String> entries;

    private List<Holder<T>> resolved = List.of();
    private int generation = -1;

    public NetworkHolderSet(ResourceKey<? extends Registry<T>> registry, List<String> entries) {
        this.registry = registry;
        this.entries = List.copyOf(entries);
    }

    /** The network form of a holder set: a list of ids and {@code #tags}; decodes to a {@link NetworkHolderSet}. */
    public static <T> Codec<HolderSet<T>> codec(ResourceKey<? extends Registry<T>> registry) {
        return Codec.STRING.listOf().xmap(entries -> new NetworkHolderSet<>(registry, entries), NetworkHolderSet::entries);
    }

    /** the set as ids and {@code #tags}: what was sent for a received set, the tag or the entries' ids otherwise */
    static <T> List<String> entries(HolderSet<T> set) {
        if (set instanceof NetworkHolderSet<T> network) {
            return network.entries;
        }
        return set.unwrap().map(
                tag -> List.of("#" + tag.location()),
                holders -> holders.stream()
                        .map(holder -> holder.unwrapKey().map(key -> key.location().toString()).orElse(null))
                        .filter(Objects::nonNull)
                        .toList());
    }

    @Override
    protected List<Holder<T>> contents() {
        int current = JewelryDataIndex.generation();
        if (generation != current) {
            resolved = resolve();
            generation = current;
        }
        return resolved;
    }

    private List<Holder<T>> resolve() {
        Optional<? extends Registry<T>> lookup = JewelryDataIndex.registryAccess().flatMap(access -> access.registry(registry));
        if (lookup.isEmpty()) {
            return List.of();
        }
        Registry<T> reg = lookup.get();
        List<Holder<T>> holders = new ArrayList<>();
        for (String entry : entries) {
            if (entry.startsWith("#")) {
                ResourceLocation id = ResourceLocation.tryParse(entry.substring(1));
                if (id != null) {
                    reg.getTag(TagKey.create(registry, id)).ifPresent(tag -> tag.forEach(holders::add));
                }
            } else {
                ResourceLocation id = ResourceLocation.tryParse(entry);
                if (id != null) {
                    reg.getHolder(ResourceKey.create(registry, id)).ifPresent(holders::add);
                }
            }
        }
        return List.copyOf(holders.stream().distinct().toList());
    }

    /** by key, so a holder from any lookup of the same registry matches */
    @Override
    public boolean contains(Holder<T> holder) {
        return holder.unwrapKey()
                .map(key -> contents().stream().anyMatch(h -> h.is(key)))
                .orElseGet(() -> contents().contains(holder));
    }

    @Override
    public Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(contents());
    }

    @Override
    public Optional<TagKey<T>> unwrapKey() {
        return Optional.empty();
    }

    /** not tied to a registry: the network codec writes it from {@link #entries} */
    @Override
    public boolean canSerializeIn(HolderOwner<T> owner) {
        return false;
    }

    @Override
    public String toString() {
        return "NetworkHolderSet[" + registry.location() + " " + entries + "]";
    }
}
