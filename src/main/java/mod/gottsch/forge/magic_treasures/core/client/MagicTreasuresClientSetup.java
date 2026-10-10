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
package mod.gottsch.forge.magic_treasures.core.client;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.client.hud.JewelryHud;
import mod.gottsch.forge.magic_treasures.core.client.particle.ArcaneSparkParticle;
import mod.gottsch.forge.magic_treasures.core.client.tooltip.ClientIconTitleTooltip;
import mod.gottsch.forge.magic_treasures.core.client.tooltip.RichTooltips;
import mod.gottsch.forge.magic_treasures.core.client.tooltip.IconTitleTooltip;
import mod.gottsch.forge.magic_treasures.core.particle.MagicTreasuresParticles;
import mod.gottsch.forge.magic_treasures.core.set.SetEquipment;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-only mod-bus registrations.
 *
 * @author Mark Gottschling on 10/9/2026
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MagicTreasuresClientSetup {

	@SubscribeEvent
	public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
		MagicTreasuresParticles.sparks().forEach(spark -> event.registerSpriteSet(spark.get(), ArcaneSparkParticle.Provider::new));
	}

	@SubscribeEvent
	public static void onRegisterTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(IconTitleTooltip.class, ClientIconTitleTooltip::new);
		RichTooltips.registerFactories(event);
		// spell chips show the set bonuses of the player wearing the jewelry
		SetEquipment.setTooltipPlayer(() -> Minecraft.getInstance().player);
	}

	@SubscribeEvent
	public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll(JewelryHud.ID, new JewelryHud());
	}
}
