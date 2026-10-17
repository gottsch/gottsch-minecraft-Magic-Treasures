
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

/**
 * Fired on LivingHurtEvent, so the original amount of damage INTENDED (ie not actual Damage) to be
 * inflicted on Player is reflected back on mob.
 * reflection: value = # of uses, duration = range, percent = % of damage reflected
 * @author Mark Gottschling on Apr 30, 2020
 *
 */
public class ReflectionSpell extends CooldownSpell {
	public static String REFLECTION_TYPE = "reflection";

	private static final Class<?> REGISTERED_EVENT = LivingHurtEvent.class;

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
		IJewelryHandler handler = jewelry.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);

		if (handler.getMana() > 0 && player.isAlive()) {
			if (((LivingHurtEvent)event).getEntity() instanceof Player) {
				// get player position
				double px = player.getX();
				double py = player.getY();
				double pz = player.getZ();

				// get the source and amount
				double amount = ((LivingHurtEvent)event).getAmount();
				// calculate the new amount
				double reflectedAmount = amount * modifyEffectAmount(jewelry);
				double range = modifyRange(jewelry);
				List<Mob> mobs = world.getEntitiesOfClass(Mob.class, new AABB(px - range, py - range, pz - range, px + range, py + range, pz + range));
				// hurt the mob with reflected amount
				mobs.forEach(mob -> {
					boolean flag = mob.hurt(world.damageSources().magic(), (float) reflectedAmount);
					MagicTreasures.LOGGER.debug("reflected damage {} onto mob -> {} was successful -> {}", reflectedAmount, mob.getName(), flag);
				});

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