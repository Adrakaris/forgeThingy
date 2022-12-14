package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.blocks.OreGenerator;
import com.bocbin.forgethingy.blocks.TestPowerGenerator;
import com.bocbin.forgethingy.setup.ModSetup;
import com.bocbin.forgethingy.setup.Reg;
import net.minecraft.data.DataGenerator;

public class EnglishLanguageProvider extends net.minecraftforge.common.data.LanguageProvider {

	public EnglishLanguageProvider(DataGenerator gen) {
		super(gen, ForgeThingy.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add("itemGroup." + ModSetup.TAB_NAME, "Forge Thingy");
		add(TestPowerGenerator.POWERGEN_MESSAGE_1, "Solid fuel fired power generator");
		add(TestPowerGenerator.POWERGEN_MESSAGE_2, "Generates %s RF per tick.");
		add(TestPowerGenerator.POWERGEN_UI_TITLE, "Test Power Generator");
		add(OreGenerator.OREGEN_MESSAGE, "Generates 1 ore from any 8 ingots");

		// blocks
		add(Reg.TEST_POWERGENERATOR.get(), "Test Power Generator");
		add(Reg.ORE_GENERATOR.get(), "Ore Generator");

		add(Reg.TEST_ORE.get(), "Test Ore");
		add(Reg.TEST_ORE_DEEPSLATE.get(), "Deepslate Test Ore");
		add(Reg.TEST_ORE_NETHER.get(), "Nether Test Ore");
		add(Reg.TEST_ORE_END.get(), "End Test Ore");

		add(Reg.TEST_INGOT_BLOCK.get(), "Test Ingot Block");

		// ingots
		add(Reg.RAW_TEST_ORE.get(), "Raw Test Ore");
		add(Reg.TEST_INGOT.get(), "Test Ingot");

		// etc
		add(Reg.THIEF_SPAWN_EGG.get(), "Spawn Thief");
		add(Reg.THIEF.get(), "Thief");
	}
}
