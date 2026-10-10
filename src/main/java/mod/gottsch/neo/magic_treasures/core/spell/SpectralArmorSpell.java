
package mod.gottsch.neo.magic_treasures.core.spell;

import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.Event;

import java.util.Random;
import mod.gottsch.neo.magic_treasures.core.particle.MagicTreasuresParticles;

/**
 * 
 * @author Mark Gottschling on May 12, 2024
 *
 */
public class
SpectralArmorSpell extends Spell {
	public static String SPECTRAL_ARMOR_TYPE = "spectral_armor";

	private static final Class<?> REGISTERED_EVENT = LivingDamageEvent.Pre.class;

	/**
	 *
	 * @param builder
	 */
	SpectralArmorSpell(Builder builder) {
		super(builder);
	}

	/**
	 * Required so sub-classes can call super with a compatible Builder
	 * @param builder
	 */
	protected SpectralArmorSpell(Spell.Builder builder) {
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

	// TODO extend CooldownSpell class and update
	@Override
	public boolean serverUpdate(Level world, Random random, ICoords coords, Event event, ICastSpellContext context) {
		boolean result = false;
		ItemStack jewelry = context.getJewelry();
		Player player = context.getPlayer();
		IJewelryHandler handler = JewelryHandler.get(jewelry).orElseThrow(IllegalStateException::new);
		if (context.getEntity() instanceof CooldownSpellEntity) {
			CooldownSpellEntity spellEntity = (CooldownSpellEntity) context.getEntity();
			double cooldown = modifyCooldown(jewelry);
			// check if supports cooldown or if world time has exceeded the entity cooldown end time
			if(cooldown <= 0.0 || (world.getGameTime() > spellEntity.getCooldownExpireTime())) {
				if (hasMana(handler, context) && player.isAlive()) {
					// TODO only execute if damage is coming from a Mob ie not Fire, Fall etc
					if (((LivingDamageEvent.Pre)event).getEntity() instanceof Player) {
						// get the source and amount
						double amount = ((LivingDamageEvent.Pre)event).getNewDamage();
						if (amount > 0D) {
							// NOTE each effectAmount integer reduces by 4% just like vanilla armor, ie Leather Chest = 3 points or 12% reduction
							// calculate the new amount
							double newAmount = amount - (amount * modifyEffectAmount(jewelry) * 0.04);
							// cost eval
							double cost = applyCost(world, random, coords, context, modifySpellCost(jewelry));
							MagicTreasures.LOGGER.debug("cost (mana) incurred to jewelry -> {}", cost);

							// update the newAmount with what comes back from cost eval
							((LivingDamageEvent.Pre)event).setNewDamage((float) newAmount);

							// set cooldown expire time if cooldown is activated
							if (cooldown > 0.0) {
								((CooldownSpellEntity) context.getEntity()).setCooldownExpireTime(Long.valueOf(world.getGameTime()).doubleValue() + cooldown);
							}
							SpellEffects.burst(world, player, MagicTreasuresParticles.SPARK_ASH.get(), 8);
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
		return Component.translatable(LangUtil.tooltip("spell.spectral_armor.rate"),
				LangUtil.asPercentString(getEffectAmount() * 4.0));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		return Component.translatable(LangUtil.tooltip("spell.spectral_armor.rate"),
				LangUtil.asPercentString(modifyEffectAmount(jewelry) * 4.0));
	}

	@Override
	public ChatFormatting getSpellLabelColor() {
		return ChatFormatting.BLUE;
	}

	/*
	 * 
	 */
	public static class Builder extends Spell.Builder {

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, SPECTRAL_ARMOR_TYPE, level, rarity);
		}

		@Override
		public Spell build() {
			return  new SpectralArmorSpell(this);
		}
	}
}
