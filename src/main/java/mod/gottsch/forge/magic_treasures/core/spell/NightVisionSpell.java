
package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import mod.gottsch.forge.magic_treasures.core.util.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 * @author Mark Gottschling on June 17, 2024
 *
 */
public class NightVisionSpell extends CooldownSpell {
	public static final String TYPE = "night_vision";
	private static final Class<?> REGISTERED_EVENT = LivingEvent.LivingTickEvent.class;

	// amount to amplify strength effect by
	private int amplifier = 0;

	/**
	 *
	 * @param builder
	 */
	NightVisionSpell(Builder builder) {
		super(builder);
		this.amplifier = builder.amplifier;
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

		IJewelryHandler handler = context.getJewelry().getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);

		if (handler.getMana() > 0 && context.getPlayer().isAlive()) {
			if (!context.getPlayer().hasEffect(MobEffects.NIGHT_VISION)) {
				context.getPlayer().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, handler.modifyDuration(getDuration()), getAmplifier()));
			}
			applyCost(world, random, coords, context, handler.modifySpellCost(getSpellCost()));
       		result = true;
		}    		
		return result;
	}

	@Override
	public Component getSpellDesc() {
		return Component.translatable(LangUtil.tooltip("spell.night_vision.rate"),
				MathUtil.r1d(getDuration() / 20.0),
				MathUtil.r1d(getCooldown() / 20.0));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		return Component.translatable(LangUtil.tooltip("spell.night_vision.rate"),
				MathUtil.r1d(modifyDuration(jewelry) / 20.0),
				MathUtil.r1d(modifyCooldown(jewelry) / 20.0));
	}

	@Override
	public ChatFormatting getSpellLabelColor() {
		return ChatFormatting.GRAY;
	}

	public int getAmplifier() {
		return amplifier;
	}

	public static class Builder extends Spell.Builder {
		public int amplifier;

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, TYPE, level, rarity);
		}

		public NightVisionSpell.Builder withAmplifier(int amplifier)  {
			this.amplifier = amplifier;
			return this;
		}

		@Override
		public Spell build() {
			return  new NightVisionSpell(this);
		}
	}
}
