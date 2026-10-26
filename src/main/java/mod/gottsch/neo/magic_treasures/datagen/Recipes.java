
package mod.gottsch.neo.magic_treasures.datagen;

import net.minecraft.resources.ResourceLocation;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.tag.MagicTreasuresTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Consumer;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.Items;
import mod.gottsch.neo.magic_treasures.core.recipe.MagicTreasuresRecipes;
import mod.gottsch.neo.magic_treasures.core.recipe.GemExtractionRecipe;
import mod.gottsch.neo.magic_treasures.core.recipe.JewelryRecipe;

/**
 * 
 * @author Mark Gottschling on May 4, 2024
 *
 */
public class Recipes extends RecipeProvider {
		private static String CRITERIA = "criteria";

	public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

		@Override
		protected void buildRecipes(RecipeOutput recipe) {
			MagicTreasures.LOGGER.debug("build recipes is running...");

			// jewelry pliers
			ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MagicTreasuresItems.JEWELRY_PLIERS.get())
					.pattern(" N ")
					.pattern("I I")
					.define('N', Tags.Items.NUGGETS_IRON)
					.define('I', Tags.Items.INGOTS_IRON)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
					.save(recipe);

			// jewelry + pliers -> the jewelry's gem
			SpecialRecipeBuilder.special(GemExtractionRecipe::new).save(recipe, MagicTreasures.MOD_ID + ":gem_extraction");

			// recipe scroll + 4 of any material's ingredient -> generic jewelry (materials from the registry); the 15
			// 1.x shapeless recipes went in M9
			SpecialRecipeBuilder.special(JewelryRecipe::new).save(recipe, MagicTreasures.MOD_ID + ":jewelry");

			SimpleCookingRecipeBuilder.smelting(Ingredient.of(MagicTreasuresTags.Items.RAW_SILVER),
							RecipeCategory.MISC,
							MagicTreasuresItems.SILVER_INGOT.get(), 1.0f, 200)
					.group("silver_ingot")
					.unlockedBy("has_ore", inventoryTrigger(ItemPredicate.Builder.item().of(MagicTreasuresItems.RAW_SILVER.get()).build()))
					.save(recipe);

			// NOTE in 1.20.1 this recipe was only a leftover file in src/generated (no datagen code produced it)
			SimpleCookingRecipeBuilder.blasting(Ingredient.of(MagicTreasuresTags.Items.RAW_SILVER),
							RecipeCategory.MISC,
							MagicTreasuresItems.SILVER_INGOT.get(), 1.0f, 100)
					.group("silver_ingot")
					.unlockedBy("has_ore", inventoryTrigger(ItemPredicate.Builder.item().of(MagicTreasuresItems.RAW_SILVER.get()).build()))
					.save(recipe, ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "silver_ingot_from_blasting"));

			// TODO Treasure2 doesn't use Tags for key recipes (ruby, sapphire)
			// so need to add the recipes individually

		}
}
