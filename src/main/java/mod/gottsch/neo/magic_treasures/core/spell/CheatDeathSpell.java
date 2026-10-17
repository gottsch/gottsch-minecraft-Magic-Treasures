
package mod.gottsch.neo.magic_treasures.core.spell;

import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import mod.gottsch.neo.magic_treasures.core.util.MathUtil;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * 
 * @author Mark Gottschling on May 21, 2024
 *
 */
public class CheatDeathSpell extends CooldownSpell {

	public static String TYPE = "cheat_death";

	private static final Class<?> REGISTERED_EVENT = LivingDamageEvent.Pre.class;

	/**
	 *
	 * @param builder
	 */
	CheatDeathSpell(Builder builder) {
		super(builder);
	}

	/**
	 * Required so sub-classes can call super with a compatible Builder
	 * @param builder
	 */
	protected CheatDeathSpell(Spell.Builder builder) {
		super(builder);
	}

	@Override
	public Class<?> getRegisteredEvent() {
		return REGISTERED_EVENT;
	}

	@Override
	public boolean execute(Level world, Random random, ICoords coords, Event event, ICastSpellContext context) {
		boolean result = false;
		ItemStack jewelry = context.getJewelry();
		Player player = context.getPlayer();
		CooldownSpellEntity entity = (CooldownSpellEntity) context.getEntity();
		IJewelryHandler handler = JewelryHandler.get(jewelry).orElseThrow(IllegalStateException::new);

		if (hasMana(handler, context) && player.isAlive()) {
			if (((LivingDamageEvent.Pre)event).getEntity() instanceof Player) {
				// get the source and amount
				double damage = ((LivingDamageEvent.Pre)event).getNewDamage();
				if (damage > 0D && damage > player.getHealth()) {

					// set player's health to amount
					player.setHealth((float) handler.modifyEffectAmount(getEffectAmount()));

					// cost eval
					double cost = applyCost(world, random, coords, context, modifySpellCost(jewelry));

					// reduce damage to 0
					((LivingDamageEvent.Pre)event).setNewDamage(0F);

					SpellEffects.burst(world, SpellEffects.chest(player), MagicTreasuresParticles.SPARK_GOLD.get(), 30, 0.8);
					world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
					result = true;
				}
			}
		}
		return result;
	}

	@Override
	public Component getSpellDesc() {
		return Component.translatable(LangUtil.tooltip("spell.cheat_death.rate"),
				MathUtil.r1d(getCooldown()/20.0));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		return Component.translatable(LangUtil.tooltip("spell.cheat_death.rate"),
				MathUtil.r1d(modifyCooldown(jewelry)/20.0));
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
			super(name, TYPE, level, rarity);
		}

		@Override
		public Spell build() {
			return  new CheatDeathSpell(this);
		}
	}
}
