
package mod.gottsch.neo.magic_treasures.core.spell;


import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import mod.gottsch.neo.magic_treasures.core.util.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.Event;

import java.util.Random;
import mod.gottsch.neo.magic_treasures.core.particle.MagicTreasuresParticles;

/**
 * Fired on LivingIncomingDamageEvent, so the original amount of damage INTENDED (ie not actual Damage) to be
 * inflicted on Player is reflected back on mob.
 * reflection: value = # of uses, duration = range, percent = % of damage reflected
 * @author Mark Gottschling on Apr 30, 2020
 *
 */
public class ReflectionSpell extends CooldownSpell {
	public static String REFLECTION_TYPE = "reflection";

	private static final Class<?> REGISTERED_EVENT = LivingIncomingDamageEvent.class;

	/**
	 *
	 * @param builder
	 */
	ReflectionSpell(Builder builder) {
		super(builder);
	}

	public Class<?> getRegisteredEvent() {
		return REGISTERED_EVENT;
	}

	@Override
	public boolean execute(Level world, Random random, ICoords coords, Event event, ICastSpellContext context) {
		boolean result = false;
		ItemStack jewelry = context.getJewelry();
		Player player = context.getPlayer();
		IJewelryHandler handler = JewelryHandler.get(jewelry).orElseThrow(IllegalStateException::new);

		if (handler.getMana() > 0 && player.isAlive()) {
			if (((LivingIncomingDamageEvent)event).getEntity() instanceof Player) {
				double amount = ((LivingIncomingDamageEvent)event).getAmount();
				double reflectedAmount = amount * modifyEffectAmount(jewelry);
				double range = modifyRange(jewelry);
				// reflect onto the mob that caused the damage (the shooter, for a projectile) if it is within range.
				// damage with no attacker (falls, fire, drowning) or from a player does not trigger it and costs nothing.
				if (!(((LivingIncomingDamageEvent)event).getSource().getEntity() instanceof Mob attacker) || !attacker.isAlive()
						|| player.distanceToSqr(attacker) > range * range) {
					return result;
				}
				boolean flag = attacker.hurt(world.damageSources().magic(), (float) reflectedAmount);
				MagicTreasures.LOGGER.debug("reflected damage {} onto mob -> {} was successful -> {}", reflectedAmount, attacker.getName(), flag);
				SpellEffects.arc(world, player, attacker, MagicTreasuresParticles.SPARK_CYAN.get());

				applyCost(world, random, coords, context, Math.min(modifySpellCost(jewelry), reflectedAmount));

				result = true;
			}
		}
		return result;
	}

	@Override
	public Component getSpellDesc() {
		return Component.translatable(LangUtil.tooltip("spell.reflection.rate"),
				LangUtil.asPercentString(getEffectAmount() * 100),
				MathUtil.r1d(getCooldown()/20.0));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		return Component.translatable(LangUtil.tooltip("spell.reflection.rate"),
				LangUtil.asPercentString(modifyEffectAmount(jewelry) * 100),
				MathUtil.r1d(modifyCooldown(jewelry)/20.0));
	}

	@Override
	public ChatFormatting getSpellLabelColor() {
		return ChatFormatting.YELLOW;
	}

	/**
	 *
	 */
	public static class Builder extends Spell.Builder {

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, REFLECTION_TYPE, level, rarity);
		}

		@Override
		public Spell build() {
			return  new ReflectionSpell(this);
		}
	}
}