/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.magic_treasures.core.network;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.spell.ISpell;
import mod.gottsch.forge.magic_treasures.core.spell.SpellContext;
import mod.gottsch.forge.magic_treasures.core.spell.SpellEntity;
import mod.gottsch.forge.magic_treasures.core.spell.SpellRegistry;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
///////////////////////////////////
// Comment out when running DataGen until I figure out why it's not working with Curios
///////////////////////////////////
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Mark Gottschling on Feb 17, 2020
 *
 */
public class SpellUpdateS2C {
	private static final String CURIOS_ID = "curios";

	private boolean valid;
	private UUID playerId; 								//1
	private String spellName; 							//2
//	private SpellEntity spellEntity;					//3
	private InteractionHand hand;					//4
	private String slot;										//5
	private String slotProviderId;						//6
	private int slotIndex;									//6b
	private int index; 										//7
	private double mana;									//8
	private int uses;										//9
//	private int itemDamage;
	
	/**
	 * 
	 * @param playerId
	 */
	public SpellUpdateS2C(UUID playerId, SpellContext context) {
		valid = true;
		this.playerId = playerId;					//1
		this.spellName = context.getEntity().getSpell().getName().toString();	//2
//		this.spellEntity = context.getEntity();	//3
		this.hand = context.getHand();			//4
		this.slot = Objects.toString(context.getSlot(), "");				//5
		this.slotProviderId = context.getSlotProviderId();									//6
		this.slotIndex = context.getSlotIndex();									//6b
		this.index = context.getIndex();			//7
		// jewelry state properties
		this.mana = context.getCapability().getMana();									//8
		this.uses = context.getCapability().getUses();										//9
//		this.itemDamage = context.getItemStack().getDamageValue();

	}
	
	/**
	 * 
	 */
	public SpellUpdateS2C() {
		valid = false;
	}
	
	/**
	 * 
	 * @param buf
	 * @return
	 */
	public static SpellUpdateS2C decode(FriendlyByteBuf buf) {
		SpellUpdateS2C message = new SpellUpdateS2C();
		try {
			message.setPlayerId(buf.readUUID());	//1
			message.setSpellName(buf.readUtf());	//2

			Optional<ISpell> optionalSpell = SpellRegistry.get(ModUtil.asLocation(message.getSpellName()));
			if (optionalSpell.isEmpty()) {
				throw new RuntimeException(String.format("Unable to find spell %s in registry.", message.getSpellName()));
			}
//			SpellEntity entity = optionalSpell.get().entity();
//			entity.setSpell(optionalSpell.get());

	    	// get class specific entity data
//	    	if (entity instanceof ICooldownCharmEntity) {
//	    		((ICooldownCharmEntity) entity).setCooldownEnd(buf.readDouble());
//	    	}
//			message.setSpellEntity(entity);											//3
			String handAsString = buf.readUtf();
			if (!handAsString.isEmpty()) {
				message.hand = InteractionHand.valueOf(handAsString);	//4
			}
			message.setSlot(buf.readUtf());											//5
			message.setSlotProviderId(buf.readUtf());							//6
			message.setSlotIndex(buf.readInt());									//6b
			message.setIndex(buf.readInt());										//7
			message.setMana(buf.readDouble());									//8
			message.setUses(buf.readInt());											//9
//			message.setItemDamage(buf.readInt());
		}
		catch(Exception e) {
			MagicTreasures.LOGGER.error("An error occurred attempting to read message: ", e);
			return message;
		}
		message.setValid( true);
		return message;
	}
	
	/**
	 * 
	 * @param buf
	 */
	public void encode(FriendlyByteBuf buf) {
		if (!isValid()) {
			return;
		}
		buf.writeUUID(getPlayerId());		//1
		buf.writeUtf(getSpellName());	//2

	    // write specific entity data
//	    if (entity instanceof ICooldownCharmEntity) {
//	    	buf.writeDouble(((ICooldownCharmEntity) entity).getCooldownEnd());
//	    }
//
		// NOTE entity is NOT written to buffer									//3
		String handAsString = "";
		if (hand != null) {
			handAsString = hand.name();
		}
		buf.writeUtf(handAsString);													//4
		buf.writeUtf(Objects.toString(slot, ""));					//5
		buf.writeUtf(Objects.toString(slotProviderId, ""));	//6
		buf.writeInt(slotIndex);															//6b
		buf.writeInt(index);																//7
		buf.writeDouble(this.mana);													//8
		buf.writeInt(this.uses);															//9
//		buf.writeInt(this.itemDamage);
	}

	/**
	 * 
	 * @param message
	 * @param ctxSupplier
	 */
	public static void handle(final SpellUpdateS2C message, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		ctx.setPacketHandled(true);

		if (sideReceived != LogicalSide.CLIENT) {
			MagicTreasures.LOGGER.warn("SpellUpdateS2C received on wrong side -> {}", ctx.getDirection().getReceptionSide());
			return;
		}
		if (!message.isValid()) {
			MagicTreasures.LOGGER.warn("SpellUpdateS2C was invalid -> {}", message.toString());
			return;
		}

		// we know for sure that this handler is only used on the client side, so it is ok to assume
		//  that the ctx handler is a client, and that Minecraft exists.
		// Packets received on the server side must be handled differently!  See MessageHandlerOnServer

		Optional<Level> clientLevel = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (clientLevel.isEmpty()) {
			MagicTreasures.LOGGER.warn("SpellUpdateS2C context could not provide a ClientLevel.");
			return;
		}

		// This code creates a new task which will be executed by the client during the next tick
		//  In this case, the task is to call messageHandlerOnClient.processMessage(worldclient, message)
		ctx.enqueueWork(() -> processMessage(clientLevel.get(), message));
	}

	/**
	 *
	 * @param level
	 * @param message
	 */
	private static void processMessage(Level level, SpellUpdateS2C message) {
		MagicTreasures.LOGGER.debug("received spell message -> {}", message);
		try {
			Player player = level.getPlayerByUUID(message.getPlayerId());
			if (player != null) {
				MagicTreasures.LOGGER.debug("valid player -> {}", message.getPlayerId());
				// check hands first
				if (message.getHand() != null) {
					MagicTreasures.LOGGER.debug("hand handler - valid hand -> {}", message.getHand());
					// get the item for the hand
					ItemStack heldItemStack = player.getItemInHand(message.getHand());
					// determine what is being held in hand
					if (heldItemStack != null) {
						MagicTreasures.LOGGER.debug("holding item -> {}", ModUtil.getName(heldItemStack.getItem()));
						updateJewelry(player, heldItemStack, message);
					}
				}

				else if (CURIOS_ID.equals(message.getSlotProviderId())) {
					///////////////////////////////////
					// Comment out when running DataGen until I figure out why it's not working with Curios
					///////////////////////////////////
					MagicTreasures.LOGGER.debug("curios handler - updating slot spell...");
					LazyOptional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player);
					curiosHandler.ifPresent(itemHandler -> {
						Optional<ICurioStacksHandler> stacksOptional = itemHandler.getStacksHandler(message.getSlot());
						stacksOptional.ifPresent(stacksHandler -> {
							MagicTreasures.LOGGER.debug("# of slots in curios handler -> {}", stacksHandler.getStacks().getSlots());
							if (message.getSlotIndex() >= 0 && message.getSlotIndex() < stacksHandler.getStacks().getSlots()) {
								ItemStack curiosStack = stacksHandler.getStacks().getStackInSlot(message.getSlotIndex());
								updateJewelry(player, curiosStack, message);
							}
						});
					});
				}
				else {
					MagicTreasures.LOGGER.debug("hotbar handler");
					// get the item from the hotbar
					ItemStack hotbarStack = player.getInventory().getItem(Integer.valueOf(message.getSlot()));
					if (hotbarStack != null) {
						MagicTreasures.LOGGER.debug("hotbar item -> {}", ModUtil.getName(hotbarStack.getItem()));
						updateJewelry(player, hotbarStack, message);
					}
				}
			}
		}
		catch(Exception e) {
			MagicTreasures.LOGGER.error("Unexpected error ->", e);
		}
	}

	/**
	 *
	 * @param itemStack
	 * @param message
	 */
	private static void updateJewelry(Player player, ItemStack itemStack, SpellUpdateS2C message) {
		itemStack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).ifPresent(handler -> {
			// get the spell that is being sent
			ResourceLocation spellName = new ResourceLocation(message.getSpellName());
			// get the spell at the index
			List<SpellEntity> entityList = (List<SpellEntity>) handler.getSpells();
			if (!entityList.isEmpty() && entityList.size() > message.getIndex()) {
				SpellEntity entity = entityList.get(message.getIndex());
				MagicTreasures.LOGGER.debug("looking for spell -> {} at index -> {}", entity, message.getIndex());
				// ensure the spell matches the spell name from the message
				if (entity != null && entity.getSpell().getName().equals(spellName)) {
					MagicTreasures.LOGGER.debug("found spell, updating...");
					// update jewelry properties
//					itemStack.setDamageValue(itemStack.getDamageValue() - 1);
					entity.getSpell().clientUpdate(itemStack, entity, message);
				}
			}
		});
	}

	/**
	 * @return if the message is valid or not.
	 */
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}

	public String getSpellName() {
		return spellName;
	}

	public void setSpellName(String spellName) {
		this.spellName = spellName;
	}

//	public SpellEntity getSpellEntity() {
//		return spellEntity;
//	}
//
//	public void setSpellEntity(SpellEntity spellEntity) {
//		this.spellEntity = spellEntity;
//	}

	public InteractionHand getHand() {
		return hand;
	}

	public void setHand(InteractionHand hand) {
		this.hand = hand;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getSlotProviderId() {
		return slotProviderId;
	}

	public void setSlotProviderId(String slotProviderId) {
		this.slotProviderId = slotProviderId;
	}

	public int getSlotIndex() {
		return slotIndex;
	}

	public void setSlotIndex(int slotIndex) {
		this.slotIndex = slotIndex;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getMana() {
		return mana;
	}

	public void setMana(double mana) {
		this.mana = mana;
	}

	public int getUses() {
		return uses;
	}

	public void setUses(int uses) {
		this.uses = uses;
	}

	//	public int getItemDamage() {
//		return itemDamage;
//	}
//
//	public void setItemDamage(int itemDamage) {
//		this.itemDamage = itemDamage;
//	}

	@Override
	public String toString() {
		return "SpellUpdateS2C{" +
				"valid=" + valid +
				", playerId=" + playerId +
				", spellName='" + spellName + '\'' +
				", hand=" + hand +
				", slot='" + slot + '\'' +
				", slotProviderId='" + slotProviderId + '\'' +
				", slotIndex=" + slotIndex +
				", index=" + index +
				", mana=" + mana +
				", uses=" + uses +
//				", itemDamage=" + itemDamage +
				'}';
	}
}
