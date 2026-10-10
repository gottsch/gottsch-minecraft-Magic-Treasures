
package mod.gottsch.neo.magic_treasures.core.spell;

import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.Event;

import java.util.Random;
import mod.gottsch.neo.magic_treasures.core.particle.MagicTreasuresParticles;
import java.util.List;

/**
 * This is essentially the same as Mana Shield except it resists fire damage only and doesn't have a cooldown.
 * @author Mark Gottschling on Dec 27, 2020
 *
 */
public class FireResistanceSpell extends Spell {
	public static final String FIRE_RESISTENCE_TYPE = "fire_resistance";
	private static final Class<?> REGISTERED_EVENT = LivingDamageEvent.Pre.class;

	/**
	 *
	 * @param builder
	 */
	FireResistanceSpell(Builder builder) {
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

		LivingDamageEvent.Pre damageEvent = (LivingDamageEvent.Pre)event;
		// exit if not fire damage
		if (!damageEvent.getSource().is(DamageTypes.IN_FIRE)
			&& !damageEvent.getSource().is(DamageTypes.ON_FIRE)) {
			return result;
		}
		IJewelryHandler handler = JewelryHandler.get(context.getJewelry()).orElseThrow(IllegalStateException::new);

		if (hasMana(handler, context) && context.getPlayer().isAlive()) {
			// get the source and amount
			double amount = ((LivingDamageEvent.Pre)event).getNewDamage();
			// calculate the new amount
			double newAmount = 0;
			double amountToSpell = amount * Math.min(1.0, modifyEffectAmount(context.getJewelry()));
			double amountToPlayer = amount - amountToSpell;
			// Treasure.logger.debug("amount to charm -> {}); amount to player -> {}", amountToCharm, amountToPlayer);
			double cost = applyCost(world, random, coords, context, amountToSpell);
//			if (cost < amountToSpell) {
//				newAmount =+ (amountToSpell - cost);
//			}
//			else {
				newAmount = amountToPlayer;
//			}
			((LivingDamageEvent.Pre)event).setNewDamage((float) newAmount);
			SpellEffects.burst(world, context.getPlayer(), MagicTreasuresParticles.SPARK_ORANGE.get(), 5);
			result = true;
		}    		
		return result;
	}

	@Override
	public Component getSpellDesc() {
		return Component.translatable(LangUtil.tooltip("spell.fire_resistance.rate"),
				LangUtil.asPercentString(Math.min(100, getEffectAmount() * 100)));
	}

	@Override
	public List<Component> getStatChips(ItemStack jewelry) {
		return List.of(chip("resist", percent(Math.min(1.0, modifyEffectAmount(jewelry))), ChatFormatting.RED));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		return Component.translatable(LangUtil.tooltip("spell.fire_resistance.rate"),
				LangUtil.asPercentString(Math.min(100, modifyEffectAmount(jewelry) * 100)));
	}

	@Override
	public ChatFormatting getSpellLabelColor() {
		return ChatFormatting.RED;
	}

	
	public static class Builder extends Spell.Builder {

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, FIRE_RESISTENCE_TYPE, level, rarity);
		}

		@Override
		public Spell build() {
			return  new FireResistanceSpell(this);
		}
	}
}
