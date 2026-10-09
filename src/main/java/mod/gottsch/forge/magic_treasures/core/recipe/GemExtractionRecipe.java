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
package mod.gottsch.forge.magic_treasures.core.recipe;

import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.item.JewelryPliers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Crafting-grid gem extraction: jewelry with a gem + Jewelry Pliers -> the gem. The jewelry is destroyed
 * (spells and all); the pliers come back with 1 durability used (see {@link JewelryPliers}).
 * <p>
 * This is the "keep the gem" half of Torchlight 2-style gem removal. The other half, "keep the jewelry,
 * lose the gem", is the anvil with shears ({@code AnvilEventHandler}).
 *
 * @author Mark Gottschling on 10/9/2026
 */
public class GemExtractionRecipe extends CustomRecipe {

    public GemExtractionRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return !findGem(container).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return findGem(container);
    }

    /**
     * The gem set in the grid's jewelry, if the grid holds exactly one piece of jewelry with a gem and one
     * pair of pliers, and nothing else. Otherwise EMPTY.
     */
    private static ItemStack findGem(CraftingContainer container) {
        ItemStack jewelry = ItemStack.EMPTY;
        boolean hasPliers = false;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!hasPliers && stack.getItem() instanceof JewelryPliers) {
                hasPliers = true;
            } else if (jewelry.isEmpty() && stack.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).isPresent()) {
                jewelry = stack;
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (!hasPliers || jewelry.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return jewelry.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).resolve()
                .filter(IJewelryHandler::hasStone)
                .map(handler -> BuiltInRegistries.ITEM.get(handler.getStone()))
                .filter(item -> item != Items.AIR)
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MagicTreasuresRecipes.GEM_EXTRACTION.get();
    }
}
