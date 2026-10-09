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
package mod.gottsch.neo.magic_treasures.core.client;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.client.particle.ArcaneSparkParticle;
import mod.gottsch.neo.magic_treasures.core.particle.MagicTreasuresParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/**
 * Client-only mod-bus registrations.
 *
 * @author Mark Gottschling on 10/9/2026
 */
@EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MagicTreasuresClientSetup {

	@SubscribeEvent
	public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
		MagicTreasuresParticles.sparks().forEach(spark -> event.registerSpriteSet(spark.get(), ArcaneSparkParticle.Provider::new));
	}
}
