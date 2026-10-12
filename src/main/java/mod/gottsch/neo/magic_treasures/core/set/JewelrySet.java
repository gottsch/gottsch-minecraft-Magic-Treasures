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
package mod.gottsch.neo.magic_treasures.core.set;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryDataIndex;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * A named set of pieces (jewelry now; weapons and armor later) with bonuses that switch on as more pieces are worn.
 * Each tier is keyed by the number of distinct pieces it needs; wearing two copies of one piece counts once.
 * <p>
 * Lang keys: {@code set.<namespace>.<path>} for the name and {@code set.<namespace>.<path>.bonus.<pieces>} for each
 * tier's description.
 */
public class JewelrySet {
    private final ResourceLocation id;
    private final List<Piece> pieces;
    private final NavigableMap<Integer, SetBonus> tiers;

    /**
     * One piece of a set: an item (1.x jewelry, the Rootstaff, the Barkskin Vest), a 2.0 jewelry preset (matched by
     * the stack's {@code jewelry_identity} preset id), or both: a named 1.x piece also matches its preset (same id),
     * so old and converted pieces count alike until M9.
     */
    public record Piece(Optional<Supplier<? extends Item>> item, Optional<ResourceLocation> preset) {

        public static Piece of(Supplier<? extends Item> item) {
            return new Piece(Optional.of(item), Optional.empty());
        }

        public static Piece preset(ResourceLocation preset) {
            return new Piece(Optional.empty(), Optional.of(preset));
        }

        public boolean matches(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            if (item.isPresent() && stack.is(item.get().get())) {
                return true;
            }
            JewelryIdentity identity = stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
            return preset.isPresent() && identity != null && identity.preset().equals(preset);
        }

        /** the item's name, or the preset's (its path, until its data is loaded) */
        public Component name() {
            if (item.isPresent()) {
                return item.get().get().getDescription();
            }
            ResourceLocation id = preset.orElseThrow();
            return JewelryDataIndex.preset(id).flatMap(p -> p.value().name()).<Component>map(Component::translatable)
                    .orElseGet(() -> Component.literal(id.getPath()));
        }
    }

    private JewelrySet(ResourceLocation id, List<Piece> pieces, NavigableMap<Integer, SetBonus> tiers) {
        this.id = id;
        this.pieces = List.copyOf(pieces);
        this.tiers = Collections.unmodifiableNavigableMap(tiers);
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    /** The item pieces' items. Suppliers, because sets are declared before items finish registering. */
    public List<Item> getItems() {
        List<Item> items = new ArrayList<>(pieces.size());
        pieces.forEach(piece -> piece.item().ifPresent(item -> items.add(item.get())));
        return items;
    }

    /** the preset pieces' ids */
    public List<ResourceLocation> getPresets() {
        return pieces.stream().flatMap(piece -> piece.preset().stream()).toList();
    }

    public int size() {
        return pieces.size();
    }

    /** Bonus tiers, lowest piece count first. */
    public NavigableMap<Integer, SetBonus> getTiers() {
        return tiers;
    }

    /** whether an item piece is this item (armor and weapons; presets aren't items) */
    public boolean contains(Item item) {
        return pieces.stream().anyMatch(piece -> piece.item().map(s -> s.get() == item).orElse(false));
    }

    /** whether any piece, item or preset, matches this stack */
    public boolean contains(ItemStack stack) {
        return pieces.stream().anyMatch(piece -> piece.matches(stack));
    }

    /** How many of this set's pieces are among {@code worn}; each piece counts once. */
    public int countWorn(Collection<ItemStack> worn) {
        Set<Piece> found = new HashSet<>();
        for (ItemStack stack : worn) {
            for (Piece piece : pieces) {
                if (piece.matches(stack)) {
                    found.add(piece);
                }
            }
        }
        return found.size();
    }

    /** The bonuses switched on by wearing {@code count} pieces: every tier at or below it. */
    public List<SetBonus> getActiveBonuses(int count) {
        return List.copyOf(tiers.headMap(count, true).values());
    }

    public Component getName() {
        return Component.translatable("set." + id.getNamespace() + "." + id.getPath());
    }

    public Component getBonusDescription(int pieces) {
        return Component.translatable("set." + id.getNamespace() + "." + id.getPath() + ".bonus." + pieces);
    }

    public static Builder builder(String name) {
        return new Builder(ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, name));
    }

    public static class Builder {
        private final ResourceLocation id;
        private final List<Piece> pieces = new ArrayList<>();
        private final TreeMap<Integer, SetBonus> tiers = new TreeMap<>();

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder piece(Supplier<? extends Item> item) {
            pieces.add(Piece.of(item));
            return this;
        }

        /** a registered item that is also a preset of the same id (the named jewelry): either one counts */
        public Builder piece(DeferredItem<? extends Item> item) {
            pieces.add(new Piece(Optional.of(item), Optional.of(item.getId())));
            return this;
        }

        /** a 2.0 jewelry preset, e.g. magictreasures:angels_ring (M8) */
        public Builder piece(ResourceLocation preset) {
            pieces.add(Piece.preset(preset));
            return this;
        }

        /** The bonus for wearing at least {@code pieces} pieces. */
        public Builder bonus(int pieces, SetBonus bonus) {
            tiers.put(pieces, bonus);
            return this;
        }

        public JewelrySet build() {
            for (Map.Entry<Integer, SetBonus> tier : tiers.entrySet()) {
                if (tier.getKey() < 1 || tier.getKey() > pieces.size()) {
                    throw new IllegalStateException("set " + id + " has a bonus for " + tier.getKey() + " pieces but only " + pieces.size() + " pieces");
                }
            }
            return new JewelrySet(id, pieces, tiers);
        }
    }
}
