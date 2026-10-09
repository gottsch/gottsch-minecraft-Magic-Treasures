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
package mod.gottsch.neo.magic_treasures;

import mod.gottsch.neo.magic_treasures.core.config.Config;
import mod.gottsch.neo.magic_treasures.core.setup.CommonSetup;
import mod.gottsch.neo.magic_treasures.core.setup.Registration;
import mod.gottsch.neo.magic_treasures.core.spell.MagicTreasuresSpells;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Mark Gottschling on May 3, 2023
 *
 */
@Mod(value = MagicTreasures.MOD_ID)
public class MagicTreasures {
	// logger
	public static Logger LOGGER = LogManager.getLogger(MagicTreasures.MOD_ID);

	public static final String MOD_ID = "magictreasures";

	/**
	 * 
	 */
	public MagicTreasures(IEventBus eventBus, ModContainer container) {
		// TODO change to the new Echelons style of config setup
		container.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		container.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
		container.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);

		// force load of static blocks
		MagicTreasuresSpells.init();

		// register the deferred registries
		Registration.init(eventBus);

		// TODO anything that is registering magic things only, like jewelry material tiers, in common setup can and needs to be called before Registration.init()
		eventBus.addListener(CommonSetup::init);
		eventBus.addListener(ModConfigEvent.Loading.class, this::onConfig);
		eventBus.addListener(ModConfigEvent.Reloading.class, this::onConfig);
	}

	/*
	 * NOTE Curios slots (necklace, ring, bracelet, belt) are registered by datapack in
	 * data/magictreasures/curios/slots and data/magictreasures/curios/entities, not by IMC.
	 *
	 * NOTE there is no custom networking: jewelry state is a data component, which syncs with the stack
	 * (vanilla inventory sync for hands/hotbar, Curios' own stack sync for curio slots).
	 */

	private void onConfig(final ModConfigEvent event) {
		if (event.getConfig().getSpec() == Config.SERVER_CONFIG) {
			Config.mapEnableLootModifiers();
		}
	}
}
