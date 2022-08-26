package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTags extends BlockTagsProvider {

	public BlockTags(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, ForgeThingy.MODID, helper);
	}

	@Override
	protected void addTags() {
		// to indicate mineable
		tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE)
				.add(Registration.TEST_ORE.get())
				.add(Registration.TEST_ORE_NETHER.get())
				.add(Registration.TEST_ORE_END.get())
				.add(Registration.TEST_ORE_DEEPSLATE.get());

		tag(net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL)
				.add(Registration.TEST_ORE.get())
				.add(Registration.TEST_ORE_NETHER.get())
				.add(Registration.TEST_ORE_END.get())
				.add(Registration.TEST_ORE_DEEPSLATE.get());

		// forge specific tag
		tag(Tags.Blocks.ORES)
				.add(Registration.TEST_ORE.get())
				.add(Registration.TEST_ORE_NETHER.get())
				.add(Registration.TEST_ORE_END.get())
				.add(Registration.TEST_ORE_DEEPSLATE.get());
	}

	@Override
	public String getName() {
		return "Forge Thingy Tags";
	}
}
