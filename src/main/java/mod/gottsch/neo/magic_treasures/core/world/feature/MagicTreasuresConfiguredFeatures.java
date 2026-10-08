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
package mod.gottsch.neo.magic_treasures.core.world.feature;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.block.MagicTreasuresBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.List;

/**
 * 
 * @author Mark Gottschling on May 10, 2024
 *
 */
public class MagicTreasuresConfiguredFeatures {
	private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MagicTreasures.MOD_ID);

	public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_TOPAZ_ORE_KEY = registerKey("topaz_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ONYX_ORE_KEY = registerKey("onyx_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_JADEITE_ORE_KEY = registerKey("jadeite_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_RUBY_ORE_KEY = registerKey("ruby_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_SAPPHIRE_ORE_KEY = registerKey("sapphire_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_SILVER_ORE_KEY = registerKey("silver_ore");


	/**
	 *
	 * @param context
	 */
	public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
		RuleTest ruleTest1 = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
		RuleTest ruleTest2 = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

		List<OreConfiguration.TargetBlockState> topazOres = List.of(OreConfiguration.target(ruleTest1,
						MagicTreasuresBlocks.TOPAZ_ORE.get().defaultBlockState()),
				OreConfiguration.target(ruleTest2, MagicTreasuresBlocks.DEEPSLATE_TOPAZ_ORE.get().defaultBlockState()));

		List<OreConfiguration.TargetBlockState> onyxOres = List.of(OreConfiguration.target(ruleTest1,
						MagicTreasuresBlocks.ONYX_ORE.get().defaultBlockState()),
				OreConfiguration.target(ruleTest2, MagicTreasuresBlocks.DEEPSLATE_ONYX_ORE.get().defaultBlockState()));

		List<OreConfiguration.TargetBlockState> jadeiteOres = List.of(OreConfiguration.target(ruleTest1,
						MagicTreasuresBlocks.JADEITE_ORE.get().defaultBlockState()),
				OreConfiguration.target(ruleTest2, MagicTreasuresBlocks.DEEPSLATE_JADEITE_ORE.get().defaultBlockState()));

		List<OreConfiguration.TargetBlockState> rubyOres = List.of(OreConfiguration.target(ruleTest1,
						MagicTreasuresBlocks.RUBY_ORE.get().defaultBlockState()),
				OreConfiguration.target(ruleTest2, MagicTreasuresBlocks.DEEPSLATE_RUBY_ORE.get().defaultBlockState()));

		List<OreConfiguration.TargetBlockState> sapphireOres = List.of(OreConfiguration.target(ruleTest1,
						MagicTreasuresBlocks.SAPPHIRE_ORE.get().defaultBlockState()),
				OreConfiguration.target(ruleTest2, MagicTreasuresBlocks.DEEPSLATE_SAPPHIRE_ORE.get().defaultBlockState()));

		List<OreConfiguration.TargetBlockState> silverOres = List.of(OreConfiguration.target(ruleTest1,
						MagicTreasuresBlocks.SILVER_ORE.get().defaultBlockState()),
				OreConfiguration.target(ruleTest2, MagicTreasuresBlocks.DEEPSLATE_SILVER_ORE.get().defaultBlockState()));

		register(context, OVERWORLD_TOPAZ_ORE_KEY, Feature.ORE, new OreConfiguration(topazOres, 3));
		register(context, OVERWORLD_ONYX_ORE_KEY, Feature.ORE, new OreConfiguration(onyxOres, 3));
		register(context, OVERWORLD_JADEITE_ORE_KEY, Feature.ORE, new OreConfiguration(jadeiteOres, 3));
		register(context, OVERWORLD_RUBY_ORE_KEY, Feature.ORE, new OreConfiguration(rubyOres, 3));
		register(context, OVERWORLD_SAPPHIRE_ORE_KEY, Feature.ORE, new OreConfiguration(sapphireOres, 3));
		register(context, OVERWORLD_SILVER_ORE_KEY, Feature.ORE, new OreConfiguration(silverOres, 4));

	}

	/**
	 *
	 */
	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		FEATURES.register(bus);
	}

	public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MagicTreasures.MOD_ID, name));
	}

	private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
																						  ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
		context.register(key, new ConfiguredFeature<>(feature, configuration));
	}

}
