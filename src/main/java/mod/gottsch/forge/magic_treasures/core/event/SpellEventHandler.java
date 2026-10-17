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

import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.world.WorldInfo;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.network.MagicTreasuresNetworking;
import mod.gottsch.forge.magic_treasures.core.network.SpellUpdateS2C;
import mod.gottsch.forge.magic_treasures.core.spell.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author Mark Gottschling on May 3, 2024
 *
 */
public class SpellEventHandler {

	private IEquipmentSpellHandler equipmentSpellHandler;

	/**
	 *
	 * @param handler
	 */
	public SpellEventHandler(IEquipmentSpellHandler handler) {
		equipmentSpellHandler = handler;
	}
	
	/*
	 * Subscribing to multiple types of Living events for Spell Interactions so that instanceof doesn't have to be called everytime.
	 */

	/**
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void checkSpellsInteraction(LivingEvent.LivingTickEvent event) {
		if (WorldInfo.isClientSide(event.getEntity().level())) {
			return;
		}

		// do something to player every update tick:
		if (event.getEntity() instanceof Player) {
			// get the player
			ServerPlayer player = (ServerPlayer) event.getEntity();
			processSpells(event, player);
		}
	}

	/**
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void checkSpellsInteractionWithDamage(LivingDamageEvent event) {
		if (WorldInfo.isClientSide(event.getEntity().level())) {
			return;
		}

		// NOTE mimic checkSpells...(LivingHurEvent) for checking the player entity, IFF a spell causes a mob damage.
		
		// do something to player every update tick:
		if (event.getEntity() instanceof Player) {
			// get the player
			ServerPlayer player = (ServerPlayer) event.getEntity();
			processSpells(event, player);
		}		
	}

	/**
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void checkSpellsInteractionWithAttack(LivingHurtEvent event) {
		if (WorldInfo.isClientSide(event.getEntity().level())) {
			return;
		}

		// get the player
		ServerPlayer player = null;
		if (event.getEntity() instanceof Player) {
			player = (ServerPlayer) event.getEntity();
		}
		else if (event.getSource().getEntity() instanceof Player) {
			player = (ServerPlayer) event.getSource().getEntity();
		}
		
		if (player != null) {
			processSpells(event, player);
		}
	}

	/**
	 * 
	 * @param event
	 * @param player
	 */
	private void processSpells(Event event, ServerPlayer player) {
		/*
		 * a list of spell contexts to execute
		 */
		List<SpellContext> spellsToExecute;

		// gather all spells
		spellsToExecute = gatherSpells(event, player);

		// sort spells
		Collections.sort(spellsToExecute, SpellContext.priorityComparator);

		// execute spells
		executeSpells(event, player, spellsToExecute);
	}

	/**
	 * Examine and collect all Spells (not SpellEntity) that the player has in valid slots.
	 * @param event
	 * @param player
	 * @return
	 */
	private List<SpellContext> gatherSpells(Event event, ServerPlayer player) {
		final List<SpellContext> contexts = new ArrayList<>(5);
		
		// check each hand
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack heldStack = player.getItemInHand(hand);
			if (heldStack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).isPresent()) {
				contexts.addAll(getSpellsFromStack(event, hand, "", heldStack));
			}
		}

		// check equipment slots
		if (getEquipmentSpellHandler() != null) {
			List<SpellContext> equipmentContexts = getEquipmentSpellHandler().handleEquipmentSpells(event, player);
			contexts.addAll(equipmentContexts);
		}

		return contexts;
	}

	/**
	 * 
	 * @param event
	 * @param hand
	 * @param itemStack
	 * @return
	 */
	private List<SpellContext> getSpellsFromStack(Event event, InteractionHand hand, String slot, ItemStack itemStack) {
		final List<SpellContext> contexts = new ArrayList<>(5);
		IJewelryHandler handler = itemStack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalSelectorException::new);

		int index = 0;
		for (int i = 0; i < handler.getSpells().size(); i++) {
			SpellEntity entity = handler.getSpells().get(i);
			if (!entity.getSpell().getRegisteredEvent().equals(event.getClass())) {
				continue;
			}
			index = i;
			SpellContext context = new SpellContext.Builder()
					.withIndex(index)
					.with($ -> {
				$.hand = hand;
				$.slot = slot;
				$.slotProviderId = "minecraft";
				$.itemStack = itemStack;
				$.capability = handler;
				$.entity = entity;
			}).build();
			contexts.add(context);

		}
		return contexts;
	}
	
	/**
	 * 
	 * @param event
	 * @param player
	 * @param contexts
	 */
	private void executeSpells(Event event, ServerPlayer player, List<SpellContext> contexts) {
		/*
		 * a list of spell types that are non-stackable that should not be executed more than once.
		 */
		final List<String> executeOnceSpellTypes = new ArrayList<>(5);

		contexts.forEach(context -> {
			ISpell spell = (ISpell)context.getEntity().getSpell();
//			MagicTreasures.LOGGER.debug("processing spell -> {}", spell.getName().toString());
			if (!spell.isEffectStackable()) {
				// TODO this probably needs to change to spell.getName comparison
				// check if this spell type is already in the monitored list
				if (executeOnceSpellTypes.contains(spell.getType())) {
					return;
				}
				else {
					// add the spell type to the monitored list
					executeOnceSpellTypes.add(spell.getType());
				}
			}

			// if spell is executable and executes successfully
			ICastSpellContext castContext = new CastSpellContext(context.getItemStack(), null, context.getEntity(), player);
			if (context.getEntity().getSpell().serverUpdate(player.level(), new Random(), new Coords(player.position()), event, castContext)) {
//				MagicTreasures.LOGGER.debug("spell {} successfully updated.", spell.getName().toString());
				processUsage(player.level(), player, event, context);
				
				// TODO would be nice if ALL spells processed during event could sent 1 bundled message instead of individual messages

				// send state message to client
				SpellUpdateS2C message = new SpellUpdateS2C(player.getUUID(), context);
//				MagicTreasures.LOGGER.debug("sending message to client -> {}", message);
				MagicTreasuresNetworking.channel.send(PacketDistributor.PLAYER.with(() -> player), message);
			}
//			else {
//				MagicTreasures.LOGGER.debug("spell.serverUpdate failed for some reason.");
//			}
		});
	}

	private static void processUsage(Level world, Player player, Event event, SpellContext context) {
		MagicTreasures.LOGGER.debug("processing usage for spell -> {}", context.getEntity().getSpell().getName().toString());
		// TODO call capability.getDecrementor.apply() or something like that.
		ItemStack stack = context.getItemStack();
		// get capability
		stack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).ifPresent(handler -> {
			if (handler.isInfinite()) {
				return;
			}
			// remove/destroy item stack if damage is greater than durability
//			stack.setDamageValue(stack.getDamageValue() + 1);
			handler.setUses(handler.getUses() - 1);
//			if (stack.getDamageValue() >= handler.getUses()) {
//				stack.shrink(1);
//			}
			if (handler.getUses() <= 0) {
				stack.shrink(1);
			}
		});
	}
	
	/**
	 * 
	 * @return
	 */
	private IEquipmentSpellHandler getEquipmentSpellHandler() {
		return equipmentSpellHandler;
	}
	
	// TODO test - remove
	//	@SubscribeEvent
	//	public void onItemInfo(ItemTooltipEvent event) {
	//		if (event.getItemStack().getItem() == Items.EMERALD) {
	//			event.getToolTip().add(new TranslationTextComponent("tooltip.label.gem").withStyle(TextFormatting.GOLD, TextFormatting.ITALIC));
	//		}
	//	}
}
