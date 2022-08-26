package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.Registration;
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
				.add(Registration.TEST_ORE_ITEM.get())
				.add(Registration.TEST_ORE_NETHER_ITEM.get())
				.add(Registration.TEST_ORE_END_ITEM.get())
				.add(Registration.TEST_ORE_DEEPSLATE_ITEM.get());
	}

	@Override
	public String getName() {
		return "Forge Thingy Tags";
	}
}
