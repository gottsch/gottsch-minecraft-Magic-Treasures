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
package mod.gottsch.forge.magic_treasures.core.event;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.item.set.SiphonWeapon;
import mod.gottsch.forge.magic_treasures.core.particle.MagicTreasuresParticles;
import mod.gottsch.forge.magic_treasures.core.set.WornJewelry;
import mod.gottsch.forge.magic_treasures.core.spell.SpellEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Siphon: a full-strength hit with a set weapon puts mana back into the worn jewelry piece that has the least left.
 * Mana wells only refill at the anvil, so they get nothing.
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID)
public class SiphonEventHandler {
    /** a hit this charged or more counts as full strength; spam-clicking siphons nothing */
    private static final float FULL_STRENGTH = 0.9F;

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        // fires before the attack resets the charge, so the charge is the hit's
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(player.getMainHandItem().getItem() instanceof SiphonWeapon weapon)
                || !(event.getTarget() instanceof LivingEntity target)
                || target instanceof ArmorStand || !target.isAlive() || !target.isAttackable()
                || player.getAttackStrengthScale(0.5F) < FULL_STRENGTH) {
            return;
        }
        WornJewelry.lowestMana(player).ifPresent(context -> {
            double max = context.getCapability().getMaxMana();
            context.getCapability().setMana(Math.min(max, context.getCapability().getMana() + weapon.getSiphonMana()));
            WornJewelry.sync(player, context);
            SpellEffects.arc(player.level(), target, player, MagicTreasuresParticles.SPARK_BLUE.get());
        });
    }
}
