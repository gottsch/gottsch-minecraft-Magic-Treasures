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

import mod.gottsch.forge.magic_treasures.core.integration.MagicTreasuresIntegrations;
import mod.gottsch.forge.magic_treasures.core.spell.ISpell;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * What a player is wearing, for sets, and which set bonuses that switches on.
 * <p>
 * "Worn" means what casts spells: the equipment handler's slots (Curios slots, or the active hotbar jewelry and
 * wells without Curios), plus both hands and the armor slots. Every set feature (tiers, tooltips, later the weapon
 * and armor mechanics) goes through {@link #getWornItems}, so Curios and non-Curios players can't drift apart.
 */
public final class SetEquipment {
    /** the worn items and active bonuses, computed at most once per tick per player (spells ask several times per cast) */
    private static final Map<Player, Cached> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    /** resonance: each worn armor piece (or shield) of a set adds this much effect to the spells on that set's jewelry */
    public static final double RESONANCE_PER_PIECE = 0.10;

    private record Cached(long gameTime, List<ItemStack> worn, List<SetBonus> bonuses) {}

    private SetEquipment() {}

    /** the client's player, for tooltips; client setup sets it, since common code can't reach Minecraft */
    private static Supplier<Player> tooltipPlayer = () -> null;

    public static void setTooltipPlayer(Supplier<Player> supplier) {
        tooltipPlayer = supplier;
    }

    /** The player whose set bonuses a tooltip of {@code jewelry} shows: the client's player, if it wears it. */
    public static Player getTooltipWearer(ItemStack jewelry) {
        Player player = tooltipPlayer.get();
        if (player == null) {
            return null;
        }
        for (ItemStack stack : getWornItems(player)) {
            if (stack == jewelry || ItemStack.isSameItemSameTags(stack, jewelry)) {
                return player;
            }
        }
        return null;
    }

    /** Every stack that counts as worn, without duplicates. */
    public static List<ItemStack> getWornItems(Player player) {
        Set<ItemStack> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        List<ItemStack> worn = new ArrayList<>();
        for (ItemStack stack : MagicTreasuresIntegrations.getEquipmentHandler().getWornJewelry(player)) {
            if (!stack.isEmpty() && seen.add(stack)) {
                worn.add(stack);
            }
        }
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty() && seen.add(stack)) {
                worn.add(stack);
            }
        }
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty() && seen.add(stack)) {
                worn.add(stack);
            }
        }
        return worn;
    }

    /** How many of {@code set}'s pieces the player is wearing. */
    public static int countWorn(Player player, JewelrySet set) {
        return set.countWorn(getWornItems(player));
    }

    /** Every set bonus currently switched on for the player. */
    public static List<SetBonus> getActiveBonuses(Player player) {
        return cached(player).bonuses();
    }

    private static Cached cached(Player player) {
        long now = player.level().getGameTime();
        Cached cached = CACHE.get(player);
        if (cached != null && cached.gameTime() == now) {
            return cached;
        }
        List<ItemStack> worn = getWornItems(player);
        List<SetBonus> bonuses = new ArrayList<>();
        for (JewelrySet set : JewelrySets.getAll()) {
            int count = set.countWorn(worn);
            if (count > 0) {
                bonuses.addAll(set.getActiveBonuses(count));
            }
        }
        cached = new Cached(now, List.copyOf(worn), List.copyOf(bonuses));
        CACHE.put(player, cached);
        return cached;
    }

    /**
     * {@code value} after every active set bonus has had its say, then resonance when {@code jewelry} is a set piece:
     * each worn armor piece of its set adds {@link #RESONANCE_PER_PIECE} to the effect.
     */
    public static double modify(Player player, ItemStack jewelry, ISpell spell, SpellStat stat, double value) {
        if (player == null) {
            return value;
        }
        for (SetBonus bonus : getActiveBonuses(player)) {
            value = bonus.modify(stat, spell, value);
        }
        if (stat == SpellStat.EFFECT && jewelry != null) {
            int resonating = countResonating(player, jewelry);
            if (resonating > 0) {
                value *= 1.0 + RESONANCE_PER_PIECE * resonating;
            }
        }
        return value;
    }

    /** How many worn armor pieces (and shields) of {@code jewelry}'s set resonate with it; 0 if it's in no set. */
    public static int countResonating(Player player, ItemStack jewelry) {
        Optional<JewelrySet> set = JewelrySets.get(jewelry.getItem());
        if (set.isEmpty()) {
            return 0;
        }
        Set<Item> found = new HashSet<>();
        for (ItemStack stack : cached(player).worn()) {
            if (isResonant(stack.getItem()) && set.get().contains(stack.getItem())) {
                found.add(stack.getItem());
            }
        }
        return found.size();
    }

    /** Set armor and shields resonate; jewelry and weapons don't. */
    public static boolean isResonant(Item item) {
        return item instanceof ArmorItem || item instanceof ShieldItem;
    }

    /** About once a second, from the spell tick: lets active bonuses act on their own. */
    public static void tick(ServerPlayer player) {
        for (SetBonus bonus : getActiveBonuses(player)) {
            bonus.tick(player);
        }
    }
}
