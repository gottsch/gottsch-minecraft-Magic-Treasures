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

import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.integration.MagicTreasuresIntegrations;
import mod.gottsch.forge.magic_treasures.core.network.MagicTreasuresNetworking;
import mod.gottsch.forge.magic_treasures.core.network.SpellUpdateS2C;
import mod.gottsch.forge.magic_treasures.core.spell.SpellContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Worn jewelry with spells, for changes made outside a cast (siphon, Take Root), and the sync that tells the client.
 * <p>
 * Forge: changing a jewelry capability doesn't resend the stack, so after changing mana or uses, call {@link #sync}.
 */
public final class WornJewelry {

    private WornJewelry() {}

    /** Every worn jewelry piece with a spell, with the slot it sits in: hands first, then the equipment slots. */
    public static List<SpellContext> contexts(ServerPlayer player) {
        List<SpellContext> contexts = new ArrayList<>();
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            Optional<IJewelryHandler> cap = stack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).resolve();
            if (cap.isPresent() && !cap.get().getSpells().isEmpty()) {
                contexts.add(new SpellContext.Builder().with($ -> {
                    $.hand = hand;
                    $.slot = "";
                    $.slotProviderId = "minecraft";
                    $.itemStack = stack;
                    $.capability = cap.get();
                    $.index = 0;
                    $.entity = cap.get().getSpells().get(0);
                }).build());
            }
        }
        contexts.addAll(MagicTreasuresIntegrations.getEquipmentHandler().getWornJewelryContexts(player));
        return contexts;
    }

    /** The worn piece with the smallest share of its mana left, if any isn't full. */
    public static Optional<SpellContext> lowestMana(ServerPlayer player) {
        return contexts(player).stream()
                .filter(context -> context.getCapability().getMaxMana() > 0
                        && context.getCapability().getMana() < context.getCapability().getMaxMana())
                .min(Comparator.comparingDouble(context -> context.getCapability().getMana() / context.getCapability().getMaxMana()));
    }

    /** Sends the jewelry's mana, uses and cooldown to its owner. */
    public static void sync(ServerPlayer player, SpellContext context) {
        MagicTreasuresNetworking.channel.send(PacketDistributor.PLAYER.with(() -> player), new SpellUpdateS2C(player.getUUID(), context));
    }
}
