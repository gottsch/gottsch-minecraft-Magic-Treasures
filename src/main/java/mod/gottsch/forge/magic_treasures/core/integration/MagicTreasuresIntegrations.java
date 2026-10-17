/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.magic_treasures.core.integration;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.config.Config;
import mod.gottsch.forge.magic_treasures.core.event.HotbarEquipmentSpellHandler;
import mod.gottsch.forge.magic_treasures.core.event.IEquipmentSpellHandler;
import mod.gottsch.forge.magic_treasures.core.event.SpellEventHandler;
import mod.gottsch.forge.treasure2.api.TreasureApi;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

/**
 * 
 * @author Mark Gottschling on Sep 16, 2023
 *
 */
public class MagicTreasuresIntegrations {
	private static final String className = "mod.gottsch.forge.magic_treasures.core.event.CuriosEquipmentSpellHandler";

	public static void registerCuriosIntegration() {
		IEquipmentSpellHandler equipmentSpellHandler = null;
		if (ModList.get().isLoaded("curios")) {
			MagicTreasures.LOGGER.debug("curios IS loaded");
			try {
				equipmentSpellHandler =
						(IEquipmentSpellHandler) Class.forName(className).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				MagicTreasures.LOGGER.warn("Unable to load Curios compatibility class -> {}", className);
			}
		}
		if (equipmentSpellHandler == null) {
			MagicTreasures.LOGGER.debug("equipmentHandler is null");
			equipmentSpellHandler = new HotbarEquipmentSpellHandler();
		}
		// TODO might have to register this earlier and set the spell handler here
		MinecraftForge.EVENT_BUS.register(new SpellEventHandler(equipmentSpellHandler));
	}

	public static void registerTreasure2Integration() {
		if (ModList.get().isLoaded("treasure2")) {
			MagicTreasures.LOGGER.debug("treasure2 IS loaded");
			MagicTreasures.LOGGER.debug("registering t2 injectable loot tables");
			TreasureApi.registerLootTables(MagicTreasures.MOD_ID);
		}
	}
}
