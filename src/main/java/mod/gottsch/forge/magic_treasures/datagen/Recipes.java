
package mod.gottsch.forge.magic_treasures.datagen;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.forge.magic_treasures.core.tag.MagicTreasuresTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * 
 * @author Mark Gottschling on May 4, 2024
 *
 */
public class Recipes extends RecipeProvider {
		private static String CRITERIA = "criteria";

	public Recipes(PackOutput output) {
		super(output);
	}

		@Override
		protected void buildRecipes(Consumer<FinishedRecipe> recipe) {
			MagicTreasures.LOGGER.debug("build recipes is running...");

			// recipe scrolls
			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("iron_ring")).findFirst().get().get())
					.requires(MagicTreasuresItems.RING_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.INGOTS_IRON), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.RING_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("copper_ring")).findFirst().get().get())
					.requires(MagicTreasuresItems.RING_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.INGOTS_COPPER), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.RING_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("silver_ring")).findFirst().get().get())
					.requires(MagicTreasuresItems.RING_RECIPE.get())
					.requires(Ingredient.of(MagicTreasuresTags.Items.INGOTS_SILVER), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.RING_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("gold_ring")).findFirst().get().get())
					.requires(MagicTreasuresItems.RING_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.INGOTS_GOLD), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.RING_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("bone_ring")).findFirst().get().get())
					.requires(MagicTreasuresItems.RING_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.BONES), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.RING_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("iron_necklace")).findFirst().get().get())
					.requires(MagicTreasuresItems.NECKLACE_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.INGOTS_IRON), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.NECKLACE_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("copper_necklace")).findFirst().get().get())
					.requires(MagicTreasuresItems.NECKLACE_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.INGOTS_COPPER), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.NECKLACE_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("silver_necklace")).findFirst().get().get())
					.requires(MagicTreasuresItems.NECKLACE_RECIPE.get())
					.requires(Ingredient.of(MagicTreasuresTags.Items.INGOTS_SILVER), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.NECKLACE_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("gold_necklace")).findFirst().get().get())
					.requires(MagicTreasuresItems.NECKLACE_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.INGOTS_GOLD), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.NECKLACE_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("bone_necklace")).findFirst().get().get())
					.requires(MagicTreasuresItems.NECKLACE_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.BONES), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.NECKLACE_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("iron_bracelet")).findFirst().get().get())
					.requires(MagicTreasuresItems.BRACELET_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.INGOTS_IRON), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.BRACELET_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("copper_bracelet")).findFirst().get().get())
					.requires(MagicTreasuresItems.BRACELET_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.INGOTS_COPPER), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.BRACELET_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("silver_bracelet")).findFirst().get().get())
					.requires(MagicTreasuresItems.BRACELET_RECIPE.get())
					.requires(Ingredient.of(MagicTreasuresTags.Items.INGOTS_SILVER), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.BRACELET_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("gold_bracelet")).findFirst().get().get())
					.requires(MagicTreasuresItems.BRACELET_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.INGOTS_GOLD), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.BRACELET_RECIPE.get()))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MagicTreasuresItems.STANDARD_JEWELRY.stream().filter(o -> o.getId().getPath().equalsIgnoreCase("bone_bracelet")).findFirst().get().get())
					.requires(MagicTreasuresItems.BRACELET_RECIPE.get())
					.requires(Ingredient.of(Tags.Items.BONES), 4)
					.unlockedBy(CRITERIA, InventoryChangeTrigger.TriggerInstance.hasItems(MagicTreasuresItems.BRACELET_RECIPE.get()))
					.save(recipe);

			SimpleCookingRecipeBuilder.smelting(Ingredient.of(MagicTreasuresTags.Items.RAW_SILVER),
							RecipeCategory.MISC,
							MagicTreasuresItems.SILVER_INGOT.get(), 1.0f, 200)
					.group("silver_ingot")
					.unlockedBy("has_ore", inventoryTrigger(ItemPredicate.Builder.item().of(MagicTreasuresItems.RAW_SILVER.get()).build()))
					.save(recipe);

			// TODO Treasure2 doesn't use Tags for key recipes (ruby, sapphire)
			// so need to add the recipes individually

		}
}
