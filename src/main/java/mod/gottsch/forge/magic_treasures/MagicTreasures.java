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
package mod.gottsch.forge.magic_treasures;

import com.electronwill.nightconfig.core.CommentedConfig;
import mod.gottsch.forge.magic_treasures.core.config.Config;
import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresCreativeModeTabs;
import mod.gottsch.forge.magic_treasures.core.network.MagicTreasuresNetworking;
import mod.gottsch.forge.magic_treasures.core.setup.CommonSetup;
import mod.gottsch.forge.magic_treasures.core.setup.Registration;
import mod.gottsch.forge.magic_treasures.core.spell.MagicTreasuresSpells;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

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
	public MagicTreasures() {
		// TODO change to the new Echelons style of config setup
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

		// force load of static blocks
		MagicTreasuresSpells.init();

		// Register the setup method for modloading
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// register the deferred registries
		Registration.init(eventBus);

		eventBus.addListener(MagicTreasuresNetworking::common);
		// TODO anything that is registering magic things only, like jewelry material tiers, in common setup can and needs to be called before Registration.init()
		eventBus.addListener(CommonSetup::init);
		eventBus.addListener(this::config);
	}

	/*
	 * NOTE Curios slots (necklace, ring x2, bracelet, belt) are registered by datapack in
	 * data/magictreasures/curios/slots and data/magictreasures/curios/entities, not by IMC.
	 */

	private void config(final ModConfigEvent event) {
		if (event.getConfig().getModId().equals(MOD_ID)) {
			if (event.getConfig().getType() == ModConfig.Type.SERVER) {
				IConfigSpec<?> spec = event.getConfig().getSpec();
				// get the toml config data
				CommentedConfig commentedConfig = event.getConfig().getConfigData();

				if (spec == Config.SERVER_CONFIG) {
					Config.mapEnableLootModifiers(commentedConfig);
				}
			}
		}
	}
}
