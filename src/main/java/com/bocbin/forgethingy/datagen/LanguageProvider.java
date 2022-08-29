package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.blocks.TestPowerGenerator;
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
		add(TestPowerGenerator.POWERGEN_MESSAGE_1, "Solid fuel fired power generator");
		add(TestPowerGenerator.POWERGEN_MESSAGE_2, "Generates %s RF per tick.");
		add(TestPowerGenerator.POWERGEN_UI_TITLE, "Test Power Generator");

		// blocks
		add(Reg.TEST_POWERGENERATOR.get(), "Test Power Generator");

		add(Reg.TEST_ORE.get(), "Test Ore");
		add(Reg.TEST_ORE_DEEPSLATE.get(), "Deepslate Test Ore");
		add(Reg.TEST_ORE_NETHER.get(), "Nether Test Ore");
		add(Reg.TEST_ORE_END.get(), "End Test Ore");

		// ingots
		add(Reg.RAW_TEST_ORE.get(), "Raw Test Ore");
		add(Reg.TEST_INGOT.get(), "Test Ingot");
	}
}
