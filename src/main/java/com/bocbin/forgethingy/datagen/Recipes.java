package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.setup.Reg;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

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
		ShapedRecipeBuilder.shaped(Reg.TEST_POWERGENERATOR.get())
				.pattern("iii")
				.pattern("ifi")
				.pattern("RnR")
				.define('i', Reg.TEST_INGOT.get())
				.define('f', Blocks.FURNACE)
				.define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
				.define('n', Tags.Items.INGOTS_IRON)
				.group("forgethingy")
				.unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Reg.TEST_INGOT.get()))
				.save(consumer);

		ShapedRecipeBuilder.shaped(Reg.ORE_GENERATOR.get())
				.pattern(" i ")
				.pattern("iDi")
				.pattern("hic")
				.define('i', Reg.TEST_INGOT.get())
				.define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.define('h', Items.HOPPER)
				.define('c', Items.COMPARATOR)
				.unlockedBy("mysterious", InventoryChangeTrigger.TriggerInstance.hasItems(Reg.TEST_INGOT.get()))
				.save(consumer);
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
