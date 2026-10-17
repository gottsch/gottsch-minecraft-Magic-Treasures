
package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 * 
 * @author Mark Gottschling on May 21, 2024
 *
 */
public class WitherResistanceSpell extends Spell {
	public static final String TYPE = "wither_resistance";
	private static final Class<?> REGISTERED_EVENT = LivingDamageEvent.class;

	/**
	 *
	 * @param builder
	 */
	WitherResistanceSpell(Builder builder) {
		super(builder);
	}

	@Override
	public Class<?> getRegisteredEvent() {
		return REGISTERED_EVENT;
	}

	/**
	 * NOTE: it is assumed that only the allowable events are calling this action.
	 */
	@Override
	public boolean serverUpdate(Level world, Random random, ICoords coords, Event event, ICastSpellContext context) {
		boolean result = false;

		// exit if not fire damage
		if (((LivingDamageEvent)event).getSource().is(DamageTypes.WITHER)) {
			IJewelryHandler handler = context.getJewelry().getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);

			if (handler.getMana() > 0 && context.getPlayer().isAlive()) {
				// get the source and amount
				double amount = ((LivingDamageEvent)event).getAmount();
				// calculate the new amount
				double newAmount = 0;
				double amountToSpell = amount * Math.min(1.0, modifyEffectAmount(context.getJewelry()));
				double amountToPlayer = amount - amountToSpell;
				// Treasure.logger.debug("amount to charm -> {}); amount to player -> {}", amountToCharm, amountToPlayer);
				double cost = applyCost(world, random, coords, context, amountToSpell);
//				if (cost < amountToSpell) {
//					newAmount =+ (amountToSpell - cost);
//				}
//				else {
					newAmount = amountToPlayer;
//				}
				((LivingDamageEvent)event).setAmount((float) newAmount);
				result = true;
			}    
		}
		return result;
	}

	@Override
	public Component getSpellDesc() {
		return Component.translatable(LangUtil.tooltip("spell.wither_resistance.rate"),
				LangUtil.asPercentString(Math.min(100, getEffectAmount() * 100)));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		return Component.translatable(LangUtil.tooltip("spell.wither_resistance.rate"),
				LangUtil.asPercentString(Math.min(100, modifyEffectAmount(jewelry) * 100)));
	}

	@Override
	public ChatFormatting getSpellLabelColor() {
		return ChatFormatting.DARK_GRAY;
	}

	/**
	 *
	 */
	public static class Builder extends Spell.Builder {

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, TYPE, level, rarity);
		}

		@Override
		public Spell build() {
			return  new WitherResistanceSpell(this);
		}
	}
}
