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

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

/**
 * Particle helpers for spell feedback. A passive spell the player can't see working reads as broken.
 *
 * @author Mark Gottschling on 10/9/2026
 */
public final class SpellEffects {

    private SpellEffects() {}

    /** A point at chest height, where arcs and bursts look anchored to the body. */
    public static Vec3 chest(Entity entity) {
        return entity.position().add(0, entity.getBbHeight() * 0.6, 0);
    }

    /**
     * A jagged string of particles from {@code a} to {@code b}. Each node is nudged off the straight
     * line so the bolt zig-zags, most in the middle and pinned at both ends (after GMM's Electric
     * Skeleton arc). Does nothing on the client.
     */
    public static void arc(Level level, Vec3 a, Vec3 b, ParticleOptions particle) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        RandomSource random = serverLevel.getRandom();
        // about 3 nodes per block, so a short (melee-range) arc stays a thin line instead of a blob of sparks,
        // and the zig-zag shrinks with it
        double length = a.distanceTo(b);
        int steps = Math.max(3, (int) Math.round(length * 3));
        double maxJitter = Math.min(0.35D, length * 0.12D);
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double jitter = maxJitter * Math.sin(Math.PI * t);
            serverLevel.sendParticles(particle,
                    a.x + (b.x - a.x) * t + (random.nextDouble() - 0.5D) * jitter,
                    a.y + (b.y - a.y) * t + (random.nextDouble() - 0.5D) * jitter,
                    a.z + (b.z - a.z) * t + (random.nextDouble() - 0.5D) * jitter,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    /** {@link #arc} between two entities' chests. */
    public static void arc(Level level, Entity from, Entity to, ParticleOptions particle) {
        arc(level, chest(from), chest(to), particle);
    }

    /** A scatter of particles around a point: the flash of a spell landing. Does nothing on the client. */
    public static void burst(Level level, Vec3 center, ParticleOptions particle, int count, double spread) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, center.x, center.y, center.z, count, spread, spread, spread, 0.0D);
        }
    }

    /** {@link #burst} sized to an entity, at its chest. */
    public static void burst(Level level, Entity entity, ParticleOptions particle, int count) {
        burst(level, chest(entity), particle, count, entity.getBbWidth() * 0.4);
    }

    /**
     * A flat ring of particles around an entity at chest height, like a shield ripple. Reads clearly even
     * when a mob is right beside the player, where an arc would be a blob. Does nothing on the client.
     */
    public static void ring(Level level, Entity entity, ParticleOptions particle, double radius, int count) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 c = chest(entity);
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            serverLevel.sendParticles(particle, c.x + Math.cos(angle) * radius, c.y, c.z + Math.sin(angle) * radius,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
