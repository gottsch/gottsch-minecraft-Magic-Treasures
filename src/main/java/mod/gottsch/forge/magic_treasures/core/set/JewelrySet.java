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
package mod.gottsch.forge.magic_treasures.core.set;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
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
    private final List<Supplier<? extends Item>> pieces;
    private final NavigableMap<Integer, SetBonus> tiers;

    private JewelrySet(ResourceLocation id, List<Supplier<? extends Item>> pieces, NavigableMap<Integer, SetBonus> tiers) {
        this.id = id;
        this.pieces = List.copyOf(pieces);
        this.tiers = Collections.unmodifiableNavigableMap(tiers);
    }

    public ResourceLocation getId() {
        return id;
    }

    /** The pieces' items. Suppliers, because sets are declared before items finish registering. */
    public List<Item> getPieces() {
        List<Item> items = new ArrayList<>(pieces.size());
        pieces.forEach(piece -> items.add(piece.get()));
        return items;
    }

    public int size() {
        return pieces.size();
    }

    /** Bonus tiers, lowest piece count first. */
    public NavigableMap<Integer, SetBonus> getTiers() {
        return tiers;
    }

    public boolean contains(Item item) {
        return pieces.stream().anyMatch(piece -> piece.get() == item);
    }

    /** How many of this set's pieces are among {@code worn}; each piece counts once. */
    public int countWorn(Collection<ItemStack> worn) {
        Set<Item> found = new HashSet<>();
        for (ItemStack stack : worn) {
            if (!stack.isEmpty() && contains(stack.getItem())) {
                found.add(stack.getItem());
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
        return new Builder(new ResourceLocation(MagicTreasures.MOD_ID, name));
    }

    public static class Builder {
        private final ResourceLocation id;
        private final List<Supplier<? extends Item>> pieces = new ArrayList<>();
        private final TreeMap<Integer, SetBonus> tiers = new TreeMap<>();

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder piece(Supplier<? extends Item> item) {
            pieces.add(item);
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
