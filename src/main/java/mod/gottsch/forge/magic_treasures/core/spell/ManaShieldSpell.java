
package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import mod.gottsch.forge.magic_treasures.core.util.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 * 
 * @author Mark Gottschling on Apr 30, 2020
 *
 */
public class ManaShieldSpell extends Spell {
	public static String SHIELDING_TYPE = "mana_shield";

	// before armor and potions
	private static final Class<?> REGISTERED_EVENT = LivingDamageEvent.class;

	/**
	 *
	 * @param builder
	 */
	ManaShieldSpell(Builder builder) {
		super(builder);
	}

	/**
	 * Required so sub-classes can call super with a compatible Builder
	 * @param builder
	 */
	protected ManaShieldSpell(Spell.Builder builder) {
		super(builder);
	}

	@Override
	public Class<?> getRegisteredEvent() {
		return REGISTERED_EVENT;
	}

	@Override
	public SpellEntity entity() {
		return new CooldownSpellEntity(this);
	}

	@Override
	public boolean serverUpdate(Level world, Random random, ICoords coords, Event event, ICastSpellContext context) {
		boolean result = false;
		ItemStack jewelry = context.getJewelry();
		IJewelryHandler handler = jewelry.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);

		// TODO extend CooldownSpell class and update
		if (context.getEntity() instanceof CooldownSpellEntity spellEntity) {
            double cooldown = handler.modifyCooldown(getCooldown());

			// check if supports cooldown or if world time has exceeded the entity cooldown end time
			if(cooldown <= 0.0 || (world.getGameTime() > spellEntity.getCooldownExpireTime())) {
				if (handler.getMana() > 0 && context.getPlayer().isAlive()) {
					if (((LivingDamageEvent)event).getEntity() instanceof Player) {
						// get the source and amount
						double amount = ((LivingDamageEvent)event).getAmount();
						if (amount > 0D) {
							// calculate the new amount
							double amountToSpell = amount * handler.modifyEffectAmount(getEffectAmount());
							double amountToPlayer = amount - amountToSpell;
							double newAmount = amountToPlayer;
							MagicTreasures.LOGGER.debug("amount to jewelry -> {} amount to player -> {}", amountToSpell, amountToPlayer);
							// cost eval
							double cost = applyCost(world, random, coords, context, amountToSpell);
							MagicTreasures.LOGGER.debug("cost (mana) incurred to jewelry -> {}", cost);
							if (cost < amountToSpell) {
								newAmount =+ (amountToSpell - cost);
							}
							MagicTreasures.LOGGER.debug("new amount to player -> {}", amountToPlayer);
							// update the newAmount with what comes back from cost eval
							((LivingDamageEvent)event).setAmount((float) newAmount);

							// set cooldown expire time if cooldown is activated
							if (cooldown > 0.0) {
								((CooldownSpellEntity) context.getEntity()).setCooldownExpireTime(Long.valueOf(world.getGameTime()).doubleValue() + cooldown);
							}
							result = true;
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public Component getSpellDesc() {
		return Component.translatable(LangUtil.tooltip("spell.mana_shield.rate"),
				LangUtil.asPercentString(getEffectAmount() * 100),
				MathUtil.r1d(getFrequency()/20.0));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		return Component.translatable(LangUtil.tooltip("spell.mana_shield.rate"),
				LangUtil.asPercentString(modifyEffectAmount(jewelry) * 100),
				MathUtil.r1d(modifyFrequency(jewelry)/20.0));
	}

	@Override
	public ChatFormatting getSpellLabelColor() {
		return ChatFormatting.DARK_BLUE;
	}

	
	/*
	 * 
	 */
	public static class Builder extends Spell.Builder {

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, SHIELDING_TYPE, level, rarity);
		}

		@Override
		public Spell build() {
			return  new ManaShieldSpell(this);
		}
	}
}
