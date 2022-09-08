package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.Reg;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {

	public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, ForgeThingy.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		// everything in minecraft has identifier, ResourceLocation
		// which takes the ModID, and then some identifier name (forgethingy:raw_test_ore)
		// 		-- modloc, sometimes like a texture resource location (forgethingy:block/raw_test_ore)
		// mcLoc is a shortcut for minecraft: ...

		//region regstering block item item models
		// takes a block model as parent (modLoc)
		withExistingParent(Reg.TEST_ORE_ITEM.get().getRegistryName().getPath(), modLoc("block/test_ore"));
		withExistingParent(Reg.TEST_ORE_DEEPSLATE_ITEM.get().getRegistryName().getPath(), modLoc("block/test_ore_deepslate"));
		withExistingParent(Reg.TEST_ORE_NETHER_ITEM.get().getRegistryName().getPath(), modLoc("block/test_ore_nether"));
		withExistingParent(Reg.TEST_ORE_END_ITEM.get().getRegistryName().getPath(), modLoc("block/test_ore_end"));

		withExistingParent(Reg.TEST_POWERGENERATOR_ITEM.get().getRegistryName().getPath(), modLoc("block/test_power_generator/main"));
		withExistingParent(Reg.ORE_GENERATOR_ITEM.get().getRegistryName().getPath(), modLoc("block/ore_generator"));
		//endregion

		//region registering item models
		// no existing block to base texture of, os have to specify texture
		singleTexture(Reg.RAW_TEST_ORE.get().getRegistryName().getPath(),
				mcLoc("item/generated"),  // default pattern for normal items
				"layer0",  // "texturekey" - the parent minecraft:item/generated supports layers, for stuff like enchants
				modLoc("item/raw_test_ore"));
		singleTexture(Reg.TEST_INGOT.get().getRegistryName().getPath(),
				mcLoc("item/generated"), "layer0", modLoc("item/test_ingot"));

		//endregion
	}
}
