package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.Reg;
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
				.add(Reg.TEST_ORE.get())
				.add(Reg.TEST_ORE_NETHER.get())
				.add(Reg.TEST_ORE_END.get())
				.add(Reg.TEST_ORE_DEEPSLATE.get())
				.add(Reg.TEST_POWERGENERATOR.get())
				.add(Reg.ORE_GENERATOR.get());

		tag(net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL)
				.add(Reg.TEST_ORE.get())
				.add(Reg.TEST_ORE_NETHER.get())
				.add(Reg.TEST_ORE_END.get())
				.add(Reg.TEST_ORE_DEEPSLATE.get())
				.add(Reg.TEST_POWERGENERATOR.get())
				.add(Reg.ORE_GENERATOR.get());

		// forge specific tag
		tag(Tags.Blocks.ORES)
				.add(Reg.TEST_ORE.get())
				.add(Reg.TEST_ORE_NETHER.get())
				.add(Reg.TEST_ORE_END.get())
				.add(Reg.TEST_ORE_DEEPSLATE.get());

		tag(Reg.TAG_TEST_ORE)
				.add(Reg.TEST_ORE.get())
				.add(Reg.TEST_ORE_NETHER.get())
				.add(Reg.TEST_ORE_END.get())
				.add(Reg.TEST_ORE_DEEPSLATE.get());
	}

	@Override
	public String getName() {
		return "Forge Thingy Tags";
	}
}
