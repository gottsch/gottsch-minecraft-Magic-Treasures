
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.Event;

import java.util.Random;
import java.util.List;

/**
 * @author Mark Gottschling on June 4, 2024
 *
 */
public class SpeedSpell extends CooldownSpell {
	public static final String TYPE = "speed";
	private static final Class<?> REGISTERED_EVENT = PlayerTickEvent.Post.class;

	// amount to amplify strength effect by
	private int amplifier = 0;

	/**
	 *
	 * @param builder
	 */
	SpeedSpell(Builder builder) {
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

		IJewelryHandler handler = JewelryHandler.get(context.getJewelry()).orElseThrow(IllegalStateException::new);

		if (hasMana(handler, context) && context.getPlayer().isAlive()) {
			if (!context.getPlayer().hasEffect(MobEffects.MOVEMENT_SPEED)) {
				context.getPlayer().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, handler.modifyDuration(getDuration()), getAmplifier()));
			}
			applyCost(world, random, coords, context, handler.modifySpellCost(getSpellCost()));
       		result = true;
		}    		
		return result;
	}

	@Override
	public Component getSpellDesc() {
		return Component.translatable(LangUtil.tooltip("spell.speed.rate"),
				MathUtil.r1d(getDuration() / 20.0),
				MathUtil.r1d(getCooldown() / 20.0));
	}

	@Override
	public List<Component> getStatChips(ItemStack jewelry) {
		return List.of(chip("duration", seconds(modifyDuration(jewelry)), ChatFormatting.AQUA),
			chip("cooldown", seconds(modifyCooldown(jewelry)), ChatFormatting.AQUA));
	}

	@Override
	public Component getSpellDesc(ItemStack jewelry) {
		return Component.translatable(LangUtil.tooltip("spell.speed.rate"),
				MathUtil.r1d(modifyDuration(jewelry) / 20.0),
				MathUtil.r1d(modifyCooldown(jewelry) / 20.0));
	}

	@Override
	public ChatFormatting getSpellLabelColor() {
		return ChatFormatting.RED;
	}

	public int getAmplifier() {
		return amplifier;
	}

	public static class Builder extends Spell.Builder {
		public int amplifier;

		public Builder(ResourceLocation name, int level, IRarity rarity) {
			super(name, TYPE, level, rarity);
		}

		public SpeedSpell.Builder withAmplifier(int amplifier)  {
			this.amplifier = amplifier;
			return this;
		}

		@Override
		public Spell build() {
			return  new SpeedSpell(this);
		}
	}
}
