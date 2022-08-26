package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.setup.Reg;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
	public Recipes(DataGenerator p_125973_) {
		super(p_125973_);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		buildTableRecipes(consumer);
		buildSmeltingRecipes(consumer);
		buildBlastingRecipes(consumer);
	}

	private void buildTableRecipes(Consumer<FinishedRecipe> consumer) {

	}

	private void buildSmeltingRecipes(Consumer<FinishedRecipe> consumer) {

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(Reg.TAG_TEST_ORE_ITEM),
						Reg.TEST_INGOT.get(),
						1.3f, 200)  // xp, time
				.unlockedBy("has_ore", has(Reg.TAG_TEST_ORE_ITEM))
				.save(consumer, "test_ingot1");
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(Reg.RAW_TEST_ORE.get()),
				Reg.TEST_INGOT.get(),
				1.2f, 200)
				.unlockedBy("has_chunk", has(Reg.RAW_TEST_ORE.get()))
				.save(consumer, "test_ingot2");
	}

	private void buildBlastingRecipes(Consumer<FinishedRecipe> consumer) {

		SimpleCookingRecipeBuilder.blasting(Ingredient.of(Reg.TAG_TEST_ORE_ITEM),
						Reg.TEST_INGOT.get(),
						1.3f, 100)
				.unlockedBy("has_ore", has(Reg.TAG_TEST_ORE_ITEM))
				.save(consumer, "test_ingot_blast1");
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(Reg.RAW_TEST_ORE.get()),
						Reg.TEST_INGOT.get(),
						1.2f, 100)
				.unlockedBy("has_chunk", has(Reg.RAW_TEST_ORE.get()))
				.save(consumer, "test_ingot_blast2");
	}
}
