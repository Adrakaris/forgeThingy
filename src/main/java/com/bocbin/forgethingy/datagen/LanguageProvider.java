package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.ModSetup;
import com.bocbin.forgethingy.setup.Registration;
import net.minecraft.data.DataGenerator;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {

	public LanguageProvider(DataGenerator gen, String locale) {
		super(gen, ForgeThingy.MODID, locale);
	}

	@Override
	protected void addTranslations() {
		add("itemGroup." + ModSetup.TAB_NAME, "Forge Thingy");

		add(Registration.TEST_ORE.get(), "Test Ore");
		add(Registration.TEST_ORE_DEEPSLATE.get(), "Deepslate Test Ore");
		add(Registration.TEST_ORE_NETHER.get(), "Nether Test Ore");
		add(Registration.TEST_ORE_END.get(), "End Test Ore");
	}
}
