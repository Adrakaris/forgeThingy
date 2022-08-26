package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.ModSetup;
import com.bocbin.forgethingy.setup.Reg;
import net.minecraft.data.DataGenerator;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {

	public LanguageProvider(DataGenerator gen, String locale) {
		super(gen, ForgeThingy.MODID, locale);
	}

	@Override
	protected void addTranslations() {
		add("itemGroup." + ModSetup.TAB_NAME, "Forge Thingy");

		// blocks
		add(Reg.TEST_ORE.get(), "Test Ore");
		add(Reg.TEST_ORE_DEEPSLATE.get(), "Deepslate Test Ore");
		add(Reg.TEST_ORE_NETHER.get(), "Nether Test Ore");
		add(Reg.TEST_ORE_END.get(), "End Test Ore");

		// ingots
		add(Reg.RAW_TEST_ORE.get(), "Raw Test Ore");
		add(Reg.TEST_INGOT.get(), "Test Ingot");
	}
}
