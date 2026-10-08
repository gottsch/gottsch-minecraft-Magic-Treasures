/*
 * This file is part of  Treasure2.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neo.magic_treasures.core.world.feature;

import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

/**
 * 
 * @author Mark Gottschling on May 10, 2024
 *
 */
public class MagicTreasuresOrePlacement {
	
	   public static List<PlacementModifier> orePlacement(PlacementModifier placementModifier, PlacementModifier p_195348_) {
		      return List.of(placementModifier, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
		   }

		   public static List<PlacementModifier> commonOrePlacement(int veinsPerChunk, PlacementModifier modifier) {
		      return orePlacement(CountPlacement.of(veinsPerChunk), modifier);
		   }

		   public static List<PlacementModifier> rareOrePlacement(int veinsPerChunk, PlacementModifier modifier) {
		      return orePlacement(RarityFilter.onAverageOnceEvery(veinsPerChunk), modifier);
		   }
}
