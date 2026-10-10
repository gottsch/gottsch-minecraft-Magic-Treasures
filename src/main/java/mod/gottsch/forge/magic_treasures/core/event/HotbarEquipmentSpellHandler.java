/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2024 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.magic_treasures.core.event;

import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.spell.SpellContext;
import mod.gottsch.forge.magic_treasures.core.spell.SpellEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.item.IJewelryType;
import mod.gottsch.forge.magic_treasures.core.item.JewelryType;
import mod.gottsch.forge.magic_treasures.core.item.ManaWell;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.world.entity.player.Player;

/**
 *
 */
public class HotbarEquipmentSpellHandler implements IEquipmentSpellHandler {
	private static final int MAX_HOTBAR_JEWELRY = 4;
	/** the jewelry types that make a full set */
	private static final Set<IJewelryType> FULL_SET_TYPES = Set.of(JewelryType.RING, JewelryType.NECKLACE, JewelryType.BRACELET);

	@Override
	public List<SpellContext> handleEquipmentSpells(Event event, ServerPlayer player) {
		final List<SpellContext> contexts = new ArrayList<>(5);
		AtomicInteger jewelryCount = new AtomicInteger(0);
		AtomicReference<String> hotbarSlotStr = new AtomicReference<>("");
		// check hotbar - get the context at each slot
		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			hotbarSlotStr.set(String.valueOf(hotbarSlot));
			ItemStack inventoryStack = player.getInventory().getItem(hotbarSlot);
			if (inventoryStack != player.getItemInHand(InteractionHand.MAIN_HAND)
					&& inventoryStack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).isPresent()) {
				inventoryStack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).ifPresent(cap -> {

					AtomicInteger index = new AtomicInteger();
					// requires indexed for-loop
					for (int i = 0; i < cap.getSpells().size(); i++) {
						SpellEntity entity =  ((List<SpellEntity>)cap.getSpells()).get(i);
						if (!entity.getSpell().getRegisteredEvent().equals(event.getClass())) {
							continue;
						}
						index.set(i);
						SpellContext context  = new SpellContext.Builder().with($ -> {
							$.slotProviderId = "minecraft";
							$.slot = hotbarSlotStr.get();
							$.itemStack = inventoryStack;
							$.capability = cap;
							$.index = index.get();
							$.entity = entity;
						}).build();
						contexts.add(context);
					}

				});
				jewelryCount.getAndIncrement();
				if (jewelryCount.get() >= MAX_HOTBAR_JEWELRY) {
					break;
				}

			}
		}
		return contexts;
	}

	/** The hotbar jewelry that casts (same rules as handleEquipmentSpells), each with its first spell. */
	@Override
	public List<SpellContext> getWornJewelryContexts(ServerPlayer player) {
		List<SpellContext> contexts = new ArrayList<>(MAX_HOTBAR_JEWELRY);
		int jewelryCount = 0;
		for (int hotbarSlot = 0; hotbarSlot < 9 && jewelryCount < MAX_HOTBAR_JEWELRY; hotbarSlot++) {
			ItemStack inventoryStack = player.getInventory().getItem(hotbarSlot);
			if (inventoryStack == player.getItemInHand(InteractionHand.MAIN_HAND)) {
				continue;
			}
			Optional<IJewelryHandler> cap = inventoryStack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).resolve();
			if (cap.isEmpty()) {
				continue;
			}
			jewelryCount++;
			if (cap.get().getSpells().isEmpty()) {
				continue;
			}
			String slot = String.valueOf(hotbarSlot);
			contexts.add(new SpellContext.Builder().with($ -> {
				$.slotProviderId = "minecraft";
				$.slot = slot;
				$.itemStack = inventoryStack;
				$.capability = cap.get();
				$.index = 0;
				$.entity = cap.get().getSpells().get(0);
			}).build());
		}
		return contexts;
	}

	/**
	 * Without an equipment mod the hotbar stands in for the jewelry slots, so a full set is a ring, a necklace and a
	 * bracelet among the jewelry that is active there (same rules as handleEquipmentSpells: not the main hand, and
	 * only the first MAX_HOTBAR_JEWELRY pieces).
	 */
	@Override
	public boolean isWearingFullSet(ServerPlayer player) {
		Set<IJewelryType> types = new HashSet<>();
		int jewelryCount = 0;
		for (int hotbarSlot = 0; hotbarSlot < 9 && jewelryCount < MAX_HOTBAR_JEWELRY; hotbarSlot++) {
			ItemStack inventoryStack = player.getInventory().getItem(hotbarSlot);
			if (inventoryStack == player.getItemInHand(InteractionHand.MAIN_HAND)) {
				continue;
			}
			Optional<IJewelryHandler> cap = inventoryStack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).resolve();
			if (cap.isPresent()) {
				types.add(cap.get().getJewelryType());
				jewelryCount++;
			}
		}
		return types.containsAll(FULL_SET_TYPES);
	}

	/**
	 * The jewelry that casts from the hotbar (not the main hand, first MAX_HOTBAR_JEWELRY pieces), plus hotbar mana
	 * wells.
	 */
	@Override
	public List<ItemStack> getWornJewelry(Player player) {
		List<ItemStack> worn = new ArrayList<>(5);
		int jewelryCount = 0;
		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			ItemStack inventoryStack = player.getInventory().getItem(hotbarSlot);
			if (inventoryStack.isEmpty() || inventoryStack == player.getItemInHand(InteractionHand.MAIN_HAND)) {
				continue;
			}
			if (inventoryStack.getItem() instanceof ManaWell) {
				worn.add(inventoryStack);
			} else if (jewelryCount < MAX_HOTBAR_JEWELRY && inventoryStack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).isPresent()) {
				worn.add(inventoryStack);
				jewelryCount++;
			}
		}
		return worn;
	}

	/**
	 * Mana wells on the hotbar. The main hand is skipped because SpellEventHandler already adds held wells.
	 */
	@Override
	public List<ItemStack> getManaWells(ServerPlayer player) {
		List<ItemStack> wells = new ArrayList<>(1);
		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			ItemStack inventoryStack = player.getInventory().getItem(hotbarSlot);
			if (inventoryStack != player.getItemInHand(InteractionHand.MAIN_HAND)
					&& inventoryStack.getItem() instanceof ManaWell) {
				wells.add(inventoryStack);
			}
		}
		return wells;
	}
}
