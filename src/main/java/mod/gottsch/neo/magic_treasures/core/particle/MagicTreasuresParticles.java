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
package mod.gottsch.neo.magic_treasures.core.particle;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

/**
 * Spell feedback particles. Each spark is the same {@code ArcaneSparkParticle} with its own recolored
 * {@code spark_<color>} texture set.
 *
 * @author Mark Gottschling on 10/9/2026
 */
public class MagicTreasuresParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLES =
			DeferredRegister.create(Registries.PARTICLE_TYPE, MagicTreasures.MOD_ID);

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK_VIOLET = spark("violet");
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK_CRIMSON = spark("crimson");
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK_BLUE = spark("blue");
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK_CYAN = spark("cyan");
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK_GOLD = spark("gold");
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK_ORANGE = spark("orange");
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK_ASH = spark("ash");

	private static DeferredHolder<ParticleType<?>, SimpleParticleType> spark(String color) {
		return PARTICLES.register("spark_" + color, () -> new SimpleParticleType(false) {});
	}

	/** Every spark type, for registering their sprite sets on the client. */
	public static List<Supplier<SimpleParticleType>> sparks() {
		return List.of(SPARK_VIOLET, SPARK_CRIMSON, SPARK_BLUE, SPARK_CYAN, SPARK_GOLD, SPARK_ORANGE, SPARK_ASH);
	}

	public static void register(IEventBus bus) {
		PARTICLES.register(bus);
	}
}
