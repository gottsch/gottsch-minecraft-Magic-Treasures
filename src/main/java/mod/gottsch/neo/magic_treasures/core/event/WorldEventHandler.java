/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2024 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.magic_treasures.core.event;

import mod.gottsch.neo.gottschcore.world.WorldInfo;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.config.Config;
import mod.gottsch.neo.magic_treasures.core.integration.MagicTreasuresIntegrations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.nio.file.Path;
import java.util.Optional;

/**
 * 
 * @author Mark Gottschling on May 10, 2024
 *
 */
// NOTE no @EventBusSubscriber: this class has no @SubscribeEvent methods, and NeoForge throws at launch if it is registered anyway
// @EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class WorldEventHandler {

	private static Path worldSavePath;
	private static boolean isLoaded = false;
	private static boolean isClientLoaded = false;

//	@SubscribeEvent(priority = EventPriority.HIGH)
//	public static void onWorldLoad(LevelEvent.Load event) {
//		MagicTreasures.LOGGER.info("In world load event");
//
//		if (WorldInfo.isServerSide((Level)event.getLevel())) {
//			/*
//			 * NOTE:
//			 *  this has to happen here or some event AFTER the FMLCommonSetup
//			 *  when all blocks, items, etc are registered and tags are read in.
//			 */
//
//			ResourceLocation dimension = WorldInfo.getDimension((Level) event.getLevel());
//			MagicTreasures.LOGGER.info("In world load event for dimension {}", dimension.toString());
//
//			/*
//			 *  cache the world save folder and pass into each registry.
//			 */
//
//				if ((!isLoaded && Config.SERVER.integration.dimensionsWhiteList.get().contains(dimension.toString()))) {
//					MagicTreasures.LOGGER.info("registering integrations");
//
//
//					isLoaded = true;
//				}
//
//		}
//	}
}
