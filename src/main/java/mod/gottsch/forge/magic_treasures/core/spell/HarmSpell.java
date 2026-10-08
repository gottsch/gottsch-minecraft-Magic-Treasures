
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

/**
 * @author Mark Gottschling on June 17, 2024
 */
public class HarmSpell extends CooldownSpell {
    public static final String TYPE = "harm";
    private static final Class<?> REGISTERED_EVENT = LivingEvent.LivingTickEvent.class;

    /**
     * @param builder
     */
    HarmSpell(Builder builder) {
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
    public boolean execute(Level level, Random random, ICoords coords, Event event, ICastSpellContext context) {
        boolean result = false;

        IJewelryHandler handler = context.getJewelry().getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);
        Player player = context.getPlayer();
        if (handler.getMana() > 0 && context.getPlayer().isAlive()) {
            // get player position
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            double range = handler.modifyRange(getRange());

            // only target mobs that are not in their post-hit invulnerability ticks (vanilla rejects most damage while
            // invulnerableTime > 10). if every mob in range was just hit, don't cast - no cost, no cooldown - and try next tick.
            // NOTE once cast, the spell always costs mana, even if the mob resists or is immune to the damage.
            List<Monster> mobs = level.getEntitiesOfClass(Monster.class, new AABB(px - range, py - range, pz - range, px + range, py + range, pz + range),
                    mob -> mob.invulnerableTime <= 10);
            if (mobs.isEmpty()) {
                return result;
            }
            MagicTreasures.LOGGER.debug("number of mobs in range -> {}", mobs.size());

            double effectAmount = handler.modifyEffectAmount(getEffectAmount());
            for (Mob mob : mobs) {
                boolean flag = mob.hurt(level.damageSources().magic(), (float) effectAmount);
                if (flag) {
                    MagicTreasures.LOGGER.debug("inflict {} hp of damage. resulting health -> {}", effectAmount, mob.getHealth());
                }
                // TODO create effects
            	// TODO add number of mobs to affect and break if reached - update: only going to affect 1 mob. else it is an Aura spell.
				break;
			}

            double c = applyCost(level, random, coords, context, handler.modifySpellCost(getSpellCost()));
//            MagicTreasures.LOGGER.debug("new cost -> {}", c);
            result = true;
        }
        return result;
    }

    @Override
    public Component getSpellDesc() {
        return Component.translatable(LangUtil.tooltip("spell.harm.rate"),
                MathUtil.r1d(getEffectAmount()),
                MathUtil.r1d(getRange()),
                MathUtil.r1d(getCooldown() / 20.0));
    }

    @Override
    public Component getSpellDesc(ItemStack jewelry) {
        return Component.translatable(LangUtil.tooltip("spell.harm.rate"),
                MathUtil.r1d(modifyEffectAmount(jewelry)),
                MathUtil.r1d(modifyRange(jewelry)),
                MathUtil.r1d(modifyCooldown(jewelry) / 20.0));
    }

    @Override
    public ChatFormatting getSpellLabelColor() {
        return ChatFormatting.RED;
    }

    public static class Builder extends Spell.Builder {
        public int amplifier;

        public Builder(ResourceLocation name, int level, IRarity rarity) {
            super(name, TYPE, level, rarity);
        }

        public HarmSpell.Builder withAmplifier(int amplifier) {
            this.amplifier = amplifier;
            return this;
        }

        @Override
        public Spell build() {
            return new HarmSpell(this);
        }
    }
}
