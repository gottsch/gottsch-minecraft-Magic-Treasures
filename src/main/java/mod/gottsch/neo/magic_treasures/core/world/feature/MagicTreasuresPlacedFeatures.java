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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

/**
 *
 * @author Mark Gottschling Jul 12, 2024
 *
 */
public class MagicTreasuresPlacedFeatures {
	public static final ResourceKey<PlacedFeature> TOPAZ_PLACED_KEY = createKey("topaz_placed");
	public static final ResourceKey<PlacedFeature> ONYX_PLACED_KEY = createKey("onyx_placed");
	public static final ResourceKey<PlacedFeature> JADEITE_PLACED_KEY = createKey("jadeite_placed");
	public static final ResourceKey<PlacedFeature> RUBY_PLACED_KEY = createKey("ruby_placed");
	public static final ResourceKey<PlacedFeature> SAPPHIRE_PLACED_KEY = createKey("sapphire_placed");
	public static final ResourceKey<PlacedFeature> SILVER_PLACED_KEY = createKey("silver_placed");


	/**
	 * 
	 * @param context
	 */
	public static void bootstrap(BootstrapContext<PlacedFeature> context) {
		HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

		/*
		 * placement
		 * NOTE: the aboveBottom values are OFFSETS, ex -64 + x = placement depth
		 * @54 -> -64 + 54 = -10, @ 84 -> -64 + 84 = 20, therefor the range is from y = -10 to 20
		 */
		register(context, TOPAZ_PLACED_KEY,
				configuredFeatures.getOrThrow(MagicTreasuresConfiguredFeatures.OVERWORLD_TOPAZ_ORE_KEY),
				MagicTreasuresOrePlacement.commonOrePlacement(1, // veins per chunk
						HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(34), VerticalAnchor.aboveBottom(94))));

		register(context, ONYX_PLACED_KEY,
				configuredFeatures.getOrThrow(MagicTreasuresConfiguredFeatures.OVERWORLD_ONYX_ORE_KEY),
				MagicTreasuresOrePlacement.commonOrePlacement(1, // veins per chunk
						HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(34), VerticalAnchor.aboveBottom(94))));

		register(context, JADEITE_PLACED_KEY,
				configuredFeatures.getOrThrow(MagicTreasuresConfiguredFeatures.OVERWORLD_JADEITE_ORE_KEY),
				MagicTreasuresOrePlacement.commonOrePlacement(1, // veins per chunk
						HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(24), VerticalAnchor.aboveBottom(84))));

		register(context, RUBY_PLACED_KEY,
				configuredFeatures.getOrThrow(MagicTreasuresConfiguredFeatures.OVERWORLD_RUBY_ORE_KEY),
				MagicTreasuresOrePlacement.commonOrePlacement(1, // veins per chunk
						HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(14), VerticalAnchor.aboveBottom(70))));
		
		register(context, SAPPHIRE_PLACED_KEY,
				configuredFeatures.getOrThrow(MagicTreasuresConfiguredFeatures.OVERWORLD_SAPPHIRE_ORE_KEY),
				MagicTreasuresOrePlacement.commonOrePlacement(1, // veins per chunk
						HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(14), VerticalAnchor.aboveBottom(70))));

		register(context, SILVER_PLACED_KEY,
				configuredFeatures.getOrThrow(MagicTreasuresConfiguredFeatures.OVERWORLD_SILVER_ORE_KEY),
				MagicTreasuresOrePlacement.commonOrePlacement(1, // veins per chunk
						HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(34), VerticalAnchor.aboveBottom(90))));

	}

	private static ResourceKey<PlacedFeature> createKey(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, name));
	}

	private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
			Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
		context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
	}

	private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
			Holder<ConfiguredFeature<?, ?>> configuration, PlacementModifier... modifiers) {
		register(context, key, configuration, List.of(modifiers));
	}
}
