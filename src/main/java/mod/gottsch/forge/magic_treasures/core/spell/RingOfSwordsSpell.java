/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * Magic Treasures is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Magic Treasures is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Magic Treasures.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.forge.magic_treasures.core.entity.OrbitingSword;
import mod.gottsch.forge.magic_treasures.core.particle.MagicTreasuresParticles;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import mod.gottsch.forge.magic_treasures.core.util.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

/**
 * Ring of Swords (passive): when a hostile mob comes within range and none of the wearer's swords are out, the
 * jewelry summons a ring of spectral swords that circles the wearer for the spell's duration. One cast pays once,
 * on summon.
 * <p>
 * Stats: effect = damage per hit, range = trigger range (it also scales the orbit's radius), duration = how long
 * the swords last. The cooldown runs from the summon and is longer than the duration, so a ring never replaces a
 * live one.
 *
 * @author Mark Gottschling on 10/16/2026
 */
public class RingOfSwordsSpell extends CooldownSpell {
    public static final String TYPE = "ring_of_swords";
    private static final Class<?> REGISTERED_EVENT = LivingEvent.LivingTickEvent.class;
    /** orbit radius at the spell's base range; a jewelry's range factor scales it too */
    private static final double BASE_RADIUS = 1.6;

    private final int swords;
    private final double swordHp;

    RingOfSwordsSpell(Builder builder) {
        super(builder);
        this.swords = builder.swords;
        this.swordHp = builder.swordHp;
    }

    @Override
    public Class<?> getRegisteredEvent() {
        return REGISTERED_EVENT;
    }

    @Override
    public boolean execute(Level level, Random random, ICoords coords, Event event, ICastSpellContext context) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        IJewelryHandler handler = JewelryHandler.get(context.getJewelry()).orElseThrow(IllegalStateException::new);
        Player player = context.getPlayer();
        if (!hasMana(handler, context) || !player.isAlive()) {
            return false;
        }

        double range = range(context);
        List<Monster> mobs = level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(range));
        if (mobs.isEmpty()) {
            return false;
        }
        // one ring at a time: a jewelry with a long duration and a short cooldown could otherwise stack them
        double radius = BASE_RADIUS * (getRange() > 0 ? range / getRange() : 1.0);
        boolean swordsOut = !level.getEntitiesOfClass(OrbitingSword.class, player.getBoundingBox().inflate(radius + 2.0),
                sword -> sword.getOwner() == player).isEmpty();
        if (swordsOut) {
            return false;
        }

        int duration = duration(context);
        float damage = (float) effectAmount(context);
        for (int i = 0; i < swords; i++) {
            OrbitingSword.summon(serverLevel, player, i, swords, (float) radius, duration, damage, (float) swordHp);
        }
        playEffects(serverLevel, player, radius);
        applyCost(level, random, coords, context, handler.modifySpellCost(getSpellCost()));
        return true;
    }

    /** a ring of cyan sparks where the swords appear, and a ringing blade sound */
    private void playEffects(ServerLevel level, Player player, double radius) {
        SpellEffects.ring(level, player, MagicTreasuresParticles.SPARK_CYAN.get(), radius, 24);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS, 1.0F, 1.4F);
    }

    @Override
    public Component getSpellDesc() {
        return Component.translatable(LangUtil.tooltip("spell.ring_of_swords.rate"),
                swords,
                MathUtil.r1d(getEffectAmount()),
                MathUtil.r1d(getDuration() / 20.0));
    }

    @Override
    public Component getSpellDesc(ItemStack jewelry) {
        return Component.translatable(LangUtil.tooltip("spell.ring_of_swords.rate"),
                swords,
                MathUtil.r1d(modifyEffectAmount(jewelry)),
                MathUtil.r1d(modifyDuration(jewelry) / 20.0));
    }

    @Override
    public List<Component> getStatChips(ItemStack jewelry) {
        return List.of(chip("swords", number(swords), ChatFormatting.WHITE),
                chip("damage", number(tooltipEffectAmount(jewelry)), ChatFormatting.RED),
                chip("duration", seconds(tooltipDuration(jewelry)), ChatFormatting.AQUA),
                chip("cooldown", seconds(tooltipCooldown(jewelry)), ChatFormatting.AQUA));
    }

    @Override
    public ChatFormatting getSpellLabelColor() {
        return ChatFormatting.RED;
    }

    public int getSwords() {
        return swords;
    }

    public double getSwordHp() {
        return swordHp;
    }

    public static class Builder extends Spell.Builder {
        public int swords = 4;
        public double swordHp = 8.0;

        public Builder(ResourceLocation name, int level, IRarity rarity) {
            super(name, TYPE, level, rarity);
        }

        public RingOfSwordsSpell.Builder withSwords(int swords) {
            this.swords = swords;
            return this;
        }

        public RingOfSwordsSpell.Builder withSwordHp(double swordHp) {
            this.swordHp = swordHp;
            return this;
        }

        @Override
        public Spell build() {
            return new RingOfSwordsSpell(this);
        }
    }
}
