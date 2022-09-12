package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.Reg;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTags extends ItemTagsProvider {
	public ItemTags(DataGenerator p_126530_, BlockTagsProvider p_126531_, ExistingFileHelper helper) {
		super(p_126530_, p_126531_, ForgeThingy.MODID, helper);
	}

	@Override
	protected void addTags() {
		tag(Tags.Items.ORES)
				.add(Reg.TEST_ORE_ITEM.get())
				.add(Reg.TEST_ORE_NETHER_ITEM.get())
				.add(Reg.TEST_ORE_END_ITEM.get())
				.add(Reg.TEST_ORE_DEEPSLATE_ITEM.get());

		tag(Reg.TAG_TEST_ORE_ITEM)  // custom tag
				.add(Reg.TEST_ORE_ITEM.get())
				.add(Reg.TEST_ORE_NETHER_ITEM.get())
				.add(Reg.TEST_ORE_END_ITEM.get())
				.add(Reg.TEST_ORE_DEEPSLATE_ITEM.get());

		tag(Tags.Items.INGOTS)
				.add(Reg.TEST_INGOT.get());

		tag(Tags.Items.RAW_MATERIALS)  // raw ores and stuff
				.add(Reg.RAW_TEST_ORE.get());

		tag(Tags.Items.STORAGE_BLOCKS)
				.add(Reg.TEST_INGOT_BLOCK_ITEM.get());
	}

	@Override
	public String getName() {
		return "Forge Thingy Tags";
	}
}
