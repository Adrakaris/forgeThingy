package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BlockStateProvider {

	public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, ForgeThingy.MODID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		// generate simple block models
		// will automatically use textures based on id of block if not specified
		simpleBlock(Registration.TEST_ORE.get());
		simpleBlock(Registration.TEST_ORE_DEEPSLATE.get());
		simpleBlock(Registration.TEST_ORE_END.get());
		simpleBlock(Registration.TEST_ORE_NETHER.get());
	}
}
