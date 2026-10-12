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
package mod.gottsch.neo.magic_treasures.core.recipe;

import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.item.GenericJewelry;
import mod.gottsch.neo.magic_treasures.core.item.JewelryRecipeScroll;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryMaterialDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryRules;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryStyleDef;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 2.0 crafting: a recipe scroll + 4 of a material's {@code ingredient} -> the generic jewelry of the scroll's type, in
 * that material and the regular style, with no gem. Every material in the {@code jewelry_material} registry that has
 * an ingredient is craftable, a pack's included, with no recipe file per material.
 * <p>
 * The ingredients may be different items, as long as each matches the same material. If several materials match
 * (two packs both using {@code c:ingots/iron}), the first by id that the rules allow wins.
 * <p>
 * <b>An ordinary recipe for the same grid wins.</b> Vanilla takes the first matching recipe from an unordered
 * collection, so two recipes for one grid would be a coin flip (the 1.x shapeless recipes were, until M9 deleted
 * them). Stepping aside lets a pack replace one material's crafting with a recipe of its own.
 *
 * @author Mark Gottschling on 10/12/2026
 */
public class JewelryRecipe extends CustomRecipe {
    private static final int INGREDIENTS = 4;

    public JewelryRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return find(input).isPresent() && !otherRecipeMatches(input, level);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return find(input).orElse(ItemStack.EMPTY);
    }

    /**
     * The result, if the grid holds exactly one recipe scroll and four items that all match one material's
     * ingredient, and that material may be made as the scroll's type in the regular style. Otherwise empty.
     */
    private static Optional<ItemStack> find(CraftingInput input) {
        if (input.ingredientCount() != INGREDIENTS + 1) {
            return Optional.empty();
        }
        String type = null;
        List<ItemStack> ingredients = new ArrayList<>(INGREDIENTS);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (type == null && stack.getItem() instanceof JewelryRecipeScroll scroll) {
                type = scroll.getJewelryType();
            } else {
                ingredients.add(stack);
            }
        }
        if (type == null || ingredients.size() != INGREDIENTS) {
            return Optional.empty();
        }
        Optional<GenericJewelry> item = item(type);
        Optional<Holder.Reference<JewelryStyleDef>> style = JewelryDataIndex.style(JewelryRules.REGULAR);
        if (item.isEmpty() || style.isEmpty()) {
            return Optional.empty();
        }
        for (Holder.Reference<JewelryMaterialDef> material : JewelryDataIndex.materials().values()) {
            if (material.value().ingredient().map(ingredient -> ingredients.stream().allMatch(ingredient)).orElse(false)
                    && JewelryRules.allows(type, material, style.get(), Optional.empty())) {
                return Optional.of(item.get().stack(new JewelryIdentity(type, material.key().location(),
                        Optional.empty(), JewelryRules.REGULAR, Optional.empty())));
            }
        }
        return Optional.empty();
    }

    /**
     * Whether an ordinary (non-special) crafting recipe also matches. Special recipes are skipped, so this never
     * calls itself, and only runs once the grid already looks like scroll + 4 ingredients.
     */
    private static boolean otherRecipeMatches(CraftingInput input, Level level) {
        return level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(holder -> !holder.value().isSpecial())
                .anyMatch(holder -> holder.value().matches(input, level));
    }

    private static Optional<GenericJewelry> item(String type) {
        return MagicTreasuresItems.GENERIC_JEWELRY.stream().map(item -> (GenericJewelry) item.get())
                .filter(item -> item.getJewelryType().equals(type)).findFirst();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= INGREDIENTS + 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MagicTreasuresRecipes.JEWELRY.get();
    }
}
