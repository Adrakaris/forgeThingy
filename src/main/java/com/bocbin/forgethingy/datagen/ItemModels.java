package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {

	public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, ForgeThingy.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		// regstering block item item models
		// takes a block model as parent (modLoc)
		withExistingParent(Registration.TEST_ORE_ITEM.get().getRegistryName().getPath(), modLoc("block/test_ore"));
		withExistingParent(Registration.TEST_ORE_DEEPSLATE_ITEM.get().getRegistryName().getPath(), modLoc("block/test_ore_deepslate"));
		withExistingParent(Registration.TEST_ORE_NETHER_ITEM.get().getRegistryName().getPath(), modLoc("block/test_ore_nether"));
		withExistingParent(Registration.TEST_ORE_END_ITEM.get().getRegistryName().getPath(), modLoc("block/test_ore_end"));

	}
}
