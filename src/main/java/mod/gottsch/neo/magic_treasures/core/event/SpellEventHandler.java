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
package mod.gottsch.neo.magic_treasures.core.event;

import mod.gottsch.neo.gottschcore.spatial.Coords;
import mod.gottsch.neo.gottschcore.world.WorldInfo;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.spell.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;

import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import mod.gottsch.neo.magic_treasures.core.advancement.MagicTreasuresCriteria;
import mod.gottsch.neo.magic_treasures.core.item.ManaWell;
import mod.gottsch.neo.magic_treasures.core.set.SetEquipment;

/**
 * 
 * @author Mark Gottschling on May 3, 2024
 *
 */
public class SpellEventHandler {
	/** shared by every cast: spells only run on the server thread, so one instance is safe */
	private final Random random = new Random();


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
	 *
	 * Event mapping from Forge 1.20.1 (spells match on the event class via ISpell.getRegisteredEvent()):
	 *   LivingTickEvent (players)  -> PlayerTickEvent.Post
	 *   LivingHurtEvent            -> LivingIncomingDamageEvent  (before armor, like LivingHurtEvent)
	 *   LivingDamageEvent          -> LivingDamageEvent.Pre      (after armor/absorption; getNewDamage/setNewDamage)
	 */

	/**
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void checkSpellsInteraction(PlayerTickEvent.Post event) {
		// do something to player every update tick:
		if (event.getEntity() instanceof ServerPlayer player) {
			processSpells(event, player);
			if (player.tickCount % 20 == 0) {
				SetEquipment.tick(player);
			}
			// once a second is plenty for an advancement; the trigger is a no-op once it is granted
			if (player.tickCount % 20 == 0 && getEquipmentSpellHandler() != null
					&& getEquipmentSpellHandler().isWearingFullSet(player)) {
				MagicTreasuresCriteria.JEWELRY.get().trigger(player, MagicTreasuresCriteria.FULL_SET);
			}
		}
	}

	/**
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void checkSpellsInteractionWithDamage(LivingDamageEvent.Pre event) {
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
	public void checkSpellsInteractionWithAttack(LivingIncomingDamageEvent event) {
		if (WorldInfo.isClientSide(event.getEntity().level())) {
			return;
		}

		// NeoForge fires LivingIncomingDamageEvent BEFORE the invulnerability-ticks check (Forge's LivingHurtEvent fired
		// after it), so skip hits that vanilla is about to reject - otherwise attack spells would cost mana for nothing.
		// (vanilla still lets a bigger hit through for the difference during these ticks; those are skipped too.)
		if (event.getEntity().invulnerableTime > 10 && !event.getSource().is(DamageTypeTags.BYPASSES_COOLDOWN)) {
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
			if (JewelryHandler.get(heldStack).isPresent()) {
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
		IJewelryHandler handler = JewelryHandler.get(itemStack).orElseThrow(IllegalSelectorException::new);

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
	 * Mana wells the player can draw from: held in either hand, or worn in an equipment slot.
	 */
	private List<ItemStack> gatherManaWells(ServerPlayer player) {
		List<ItemStack> wells = new ArrayList<>(2);
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.getItem() instanceof ManaWell) {
				wells.add(stack);
			}
		}
		if (getEquipmentSpellHandler() != null) {
			wells.addAll(getEquipmentSpellHandler().getManaWells(player));
		}
		return wells;
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
		if (contexts.isEmpty()) {
			return;
		}
		final List<String> executeOnceSpellTypes = new ArrayList<>(5);
		Coords coords = new Coords(player.position());
		List<ItemStack> manaWells = gatherManaWells(player);

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
			ICastSpellContext castContext = new CastSpellContext(context.getItemStack(), manaWells, context.getEntity(), player);
			if (context.getEntity().getSpell().serverUpdate(player.level(), random, coords, event, castContext)) {
//				MagicTreasures.LOGGER.debug("spell {} successfully updated.", spell.getName().toString());
				processUsage(player.level(), player, event, context);
				// NOTE no client message: the jewelry data component changed, so the stack syncs to the client itself
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
		JewelryHandler.get(stack).ifPresent(handler -> {
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
