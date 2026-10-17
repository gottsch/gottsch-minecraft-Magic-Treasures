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
package mod.gottsch.forge.magic_treasures.core.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import mod.gottsch.forge.gottschcore.config.AbstractConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Mark Gottschling May 3, 2023
 *
 */
public class Config extends AbstractConfig {
	public static final String CATEGORY_DIV = "##############################";
	public static final String UNDERLINE_DIV = "------------------------------";

	// TODO change to the new Echelons style of config setup
	protected static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	protected static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
	
	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec SERVER_CONFIG;
	
	public static final Logging LOGGING;
	public static final ServerConfig SERVER;
	public static Config instance = new Config();

	public static final Map<String, ForgeConfigSpec.BooleanValue> enableLootModifiers = new HashMap<>();

	static {
		LOGGING = new Logging(COMMON_BUILDER);
		COMMON_CONFIG = COMMON_BUILDER.build();
		
		SERVER = new ServerConfig(SERVER_BUILDER);
		SERVER_CONFIG = SERVER_BUILDER.build();
	}

	/*
	 * 
	 */
	public static class ServerConfig {

		public Loot loot;
//		public Integration integration;

		public ServerConfig(ForgeConfigSpec.Builder builder) {
			loot = new Loot(builder);
//			integration = new Integration(builder);
		}
	}

	public static class Loot {
		public ForgeConfigSpec.BooleanValue enableVanillaLootModifiers;
		public ForgeConfigSpec.BooleanValue enableGeneralEntityLootModifier;
		public ForgeConfigSpec.BooleanValue enableZombieEntityLootModifier;
		public ForgeConfigSpec.BooleanValue enableSkeletonEntityLootModifier;
		public ForgeConfigSpec.BooleanValue enableWitherSkeletonEntityLootModifier;
		public ForgeConfigSpec.BooleanValue enableScarceEntityLootModifier;
		public ForgeConfigSpec.BooleanValue enableRareEntityLootModifier;
		public ForgeConfigSpec.BooleanValue enableEpicEntityLootModifier;

		public ForgeConfigSpec.BooleanValue enableUncommonChestLootModifier;
		public ForgeConfigSpec.BooleanValue enableScarceChestLootModifier;
		public ForgeConfigSpec.BooleanValue enableRareChestLootModifier;
		public ForgeConfigSpec.BooleanValue enableEpicChestLootModifier;
		public ForgeConfigSpec.BooleanValue enableLegendaryChestLootModifier;
		public ForgeConfigSpec.BooleanValue enableMythicalChestLootModifier;

		public ForgeConfigSpec.BooleanValue enableFishingJunkLootModifier;
		public ForgeConfigSpec.BooleanValue enableFishingTreasureLootModifier;

		public Loot(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV, " Loot properties", CATEGORY_DIV)
					.push("loot");

			enableVanillaLootModifiers = builder
					.comment(" Enable/Disable global loot modifiers that injects Magic Treasures loot into vanilla loot tables.",
							"NOTE if false, all individual settings are disabled.")
					.define("enableVanillaLootModifiers", true);

			enableGeneralEntityLootModifier = builder
					.comment(" Enable/Disable General entity loot modifier.")
					.define("enableGeneralEntityLootModifier", true);

			enableZombieEntityLootModifier = builder
					.comment(" Enable/Disable Zombie entity loot modifier.")
					.define("enableZombieEntityLootModifier", true);

			enableSkeletonEntityLootModifier = builder
					.comment(" Enable/Disable Skeleton entity loot modifier.")
					.define("enableSkeletonEntityLootModifier", true);

			enableWitherSkeletonEntityLootModifier = builder
					.comment(" Enable/Disable Wither Skeleton entity loot modifier.")
					.define("enableWitherSkeletonEntityLootModifier", true);

			enableScarceEntityLootModifier = builder
					.comment(" Enable/Disable Scarce entity loot modifier.")
					.define("enableScarceEntityLootModifier", true);

			enableRareEntityLootModifier = builder
					.comment(" Enable/Disable Rare entity loot modifier.")
					.define("enableRareEntityLootModifier", true);

			enableEpicEntityLootModifier = builder
					.comment(" Enable/Disable Epic entity loot modifier.")
					.define("enableEpicEntityLootModifier", true);

			enableUncommonChestLootModifier = builder
					.comment(" Enable/Disable Uncommon chest loot modifier.")
					.define("enableUncommonChestLootModifier", true);

			enableScarceChestLootModifier = builder
					.comment(" Enable/Disable Scarce chest loot modifier.")
					.define("enableScarceChestLootModifier", true);

			enableRareChestLootModifier = builder
					.comment(" Enable/Disable Rare chest loot modifier.")
					.define("enableRareChestLootModifier", true);

			enableEpicChestLootModifier = builder
					.comment(" Enable/Disable Epic chest loot modifier.")
					.define("enableEpicChestLootModifier", true);

			enableLegendaryChestLootModifier = builder
					.comment(" Enable/Disable Legendary chest loot modifier.")
					.define("enableLegendaryChestLootModifier", true);

			enableMythicalChestLootModifier = builder
					.comment(" Enable/Disable Mythical chest loot modifier.")
					.define("enableMythicalChestLootModifier", true);

			enableFishingJunkLootModifier = builder
					.comment(" Enable/Disable Fishing Junk loot modifier.")
					.define("enableFishingJunkChestLootModifier", true);

			enableFishingTreasureLootModifier = builder
					.comment(" Enable/Disable Fishing Treasure loot modifier.")
					.define("enableFishingTreasureChestLootModifier", true);

			builder.pop();
		}
	}

	public static void mapEnableLootModifiers(CommentedConfig commentedConfig) {
		// map config values
		enableLootModifiers.put("magictreasures:entities/general", Config.SERVER.loot.enableGeneralEntityLootModifier);
		enableLootModifiers.put("magictreasures:entities/zombie", Config.SERVER.loot.enableZombieEntityLootModifier);
		enableLootModifiers.put("magictreasures:entities/skeleton", Config.SERVER.loot.enableSkeletonEntityLootModifier);
		enableLootModifiers.put("magictreasures:entities/wither_skeleton", Config.SERVER.loot.enableWitherSkeletonEntityLootModifier);

		enableLootModifiers.put("magictreasures:entities/scarce", Config.SERVER.loot.enableScarceEntityLootModifier);
		enableLootModifiers.put("magictreasures:entities/rare", Config.SERVER.loot.enableRareEntityLootModifier);
		enableLootModifiers.put("magictreasures:entities/epic", Config.SERVER.loot.enableEpicEntityLootModifier);

		enableLootModifiers.put("uncommon", Config.SERVER.loot.enableUncommonChestLootModifier);
		enableLootModifiers.put("scarce", Config.SERVER.loot.enableScarceChestLootModifier);
		enableLootModifiers.put("rare", Config.SERVER.loot.enableRareChestLootModifier);
		enableLootModifiers.put("epic", Config.SERVER.loot.enableEpicChestLootModifier);
		enableLootModifiers.put("legendary", Config.SERVER.loot.enableLegendaryChestLootModifier);
		enableLootModifiers.put("mythical", Config.SERVER.loot.enableMythicalChestLootModifier);
	}

	/*
	 *
	 */
//	public static class Integration {
//		public ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionsWhiteList;
//		public ForgeConfigSpec.BooleanValue  enableCurios;
//
//		public Integration(final ForgeConfigSpec.Builder builder)	 {
//			builder.comment(CATEGORY_DIV, " Integration properties", CATEGORY_DIV)
//					.push("integration");
//
//			dimensionsWhiteList = builder
//					.comment(" Permitted Dimensions for Magical Treasures execution.",
//							" This setting does not use any wildcards (*). You must explicitly set the dimensions that are allowed.",
//							" ex. minecraft:overworld")
//					.defineList("dimensionsWhiteList", Arrays.asList(new String []{"minecraft:overworld"}), s -> s instanceof String);
//
//
//			enableCurios = builder
//					.comment("Enable/Disable Curios integration.", "Enabled by default, but Curios must be installed as well.")
//					.define("Enable Curios Integration:", true);
//
//			builder.pop();
//		}
//	}
	
	@Override
	public String getLogsFolder() {
		return Config.LOGGING.folder.get();
	}
	
	public void setLogsFolder(String folder) {
		Config.LOGGING.folder.set(folder);
	}
	
	@Override
	public String getLoggingLevel() {
		return Config.LOGGING.level.get();
	}
}
