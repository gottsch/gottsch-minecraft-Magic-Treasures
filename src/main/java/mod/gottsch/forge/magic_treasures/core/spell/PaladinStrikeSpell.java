
package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.spell.cost.CostEvaluator;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import mod.gottsch.forge.magic_treasures.core.util.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 * @author Mark Gottschling on May 16, 2024
 *
 */
public class PaladinStrikeSpell extends CooldownSpell {
	private static final float LIFE_AMOUNT = 2.0F;
	private static final float MIN_HEALTH_TO_CAST = 3.0F;

	public static String TYPE = "paladin_strike";
	private static final Class<?> REGISTERED_EVENT = LivingHurtEvent.class;

	// the amount of health it costs in addition to mana
	private double lifeCost;

	/**
	 *
	 * @param builder
	 */
	PaladinStrikeSpell(Builder builder) {
		super(builder);
		this.lifeCost = builder.lifeCost;
	}

	@Override
	public Class<?> getRegisteredEvent() {
		return REGISTERED_EVENT;
	}

	/**
	 * NOTE: it is assumed that only the allowable events are calling this action.
	 */
	@Override
	public boolean execute(Level world, Random random, ICoords coords, Event event, ICastSpellContext context) {
		boolean result = false;
		ItemStack jewelry = context.getJewelry();
		Player player = context.getPlayer();
		IJewelryHandler handler = jewelry.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);

		if (handler.getMana() > 0 && player.isAlive()) {
			DamageSource source = ((LivingHurtEvent) event).getSource();
			if (source.getDirectEntity() instanceof Player) {

				if (player.getHealth() > MIN_HEALTH_TO_CAST) {
					// get the source and amount
					double sourceAmount = ((LivingHurtEvent)event).getAmount();
					// calculate lifeCost
					double lifeCost = Math.min(getLifeCost(), player.getHealth() - (player.getMaxHealth() / 2));
					// calculate the damage amount based on lifeCost and modifiers
					double damageAmount = sourceAmount + (lifeCost * modifyEffectAmount(jewelry));

					// increase damage amount
					((LivingHurtEvent)event).setAmount((float) damageAmount);

					applyCost(world, random, coords, context, modifySpellCost(jewelry));
					result = true;
					MagicTreasures.LOGGER.debug("life strike damage {} onto mob -> {} ", damageAmount, source.getDirectEntity().getName());
				}
			}
		}
		return result;
	}

	@Override
	public Component getSpellDesc() {
		double amount = lifeCost * getEffectAmount();
		return Component.translatable(LangUtil.tooltip("spell.paladin_strike.rate"),
				MathUtil.r1d(amount),
				MathUtil.r1d(getCooldown()/20.0),
				MathUtil.r1d(getSpellCost()),
				MathUtil.r1d(getLifeCost()));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		double amount = lifeCost * modifyEffectAmount(jewelry);
		return Component.translatable(LangUtil.tooltip("spell.paladin_strike.rate"),
				MathUtil.r1d(amount),
				MathUtil.r1d(modifyCooldown(jewelry)/20.0),
				MathUtil.r1d((modifySpellCost(jewelry))),
				MathUtil.r1d(getLifeCost()));
	}

	@Override
	public ChatFormatting getSpellLabelColor() {
		return ChatFormatting.YELLOW;
	}

	
	public static class Builder extends Spell.Builder {

		public double lifeCost = LIFE_AMOUNT;

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, TYPE, level, rarity);
			this.costEvaluator = new PaladinStrikeCostEvaluator();
		}

		public Builder withLifeCost(double lifeCost)  {
			this.lifeCost = lifeCost;
			return this;
		}

//		@Override
//		public PaladinStrikeSpell.Builder with(Consumer<PaladinStrikeSpell.Builder> builder)  {
//			builder.accept(this);
//			return this;
//		}
		
		@Override
		public Spell build() {
			return new PaladinStrikeSpell(this);
		}
	}
	
	public static class PaladinStrikeCostEvaluator extends CostEvaluator {
		/**
		 * @param amount the cost amount requested
		 * @return the actual cost incurred
		 */
		@Override
		public double apply(Level world, Random random, ICoords coords, ICastSpellContext context, double amount) {
			Player player = context.getPlayer();;
			SpellEntity entity = context.getEntity();

			// calculate cost and reduce mana as normal
			double cost = super.apply(world, random, coords, context, amount);

			// reduce player's health by life cost
			if (entity.getSpell() instanceof PaladinStrikeSpell spell) {
				// calculate lifeCost
				double lifeCost = Math.min(spell.getLifeCost(), player.getHealth() - (player.getMaxHealth() / 2));
				// update player health
				player.setHealth((float) Mth.clamp(player.getHealth() - lifeCost, 0.0F, player.getMaxHealth()));
			}
			return cost;
		}
	}

	public double getLifeCost() {
		return lifeCost;
	}

	public void setLifeCost(double lifeCost) {
		this.lifeCost = lifeCost;
	}

	@Override
	public String toString() {
		return "PaladinStrikeSpell{" +
				"lifeCost=" + lifeCost +
				"} " + super.toString();
	}
}
