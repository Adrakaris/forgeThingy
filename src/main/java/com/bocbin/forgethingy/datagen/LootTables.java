package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.setup.Reg;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

	public LootTables(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void addTables() {

		// the silk touch table is to support both silk touch and fortune
		// mcjty made the base loot table provider
		lootTables.put(
				Reg.TEST_ORE.get(),
				createOreDropTable("test_ore", Reg.TEST_ORE.get(), Reg.RAW_TEST_ORE.get())  // table which has default ore drop values
				// i.e. 1 to 1+fortune level
		);
		lootTables.put(
				Reg.TEST_ORE_DEEPSLATE.get(),
				createOreDropTable("test_ore", Reg.TEST_ORE.get(), Reg.RAW_TEST_ORE.get())
		);
		lootTables.put(
				Reg.TEST_ORE_NETHER.get(),
				createSilkTouchTable("test_ore_nether", Reg.TEST_ORE_NETHER.get(), Reg.RAW_TEST_ORE.get(),
						1, 3, 0.5f, 0)  // table which has a random fortune roll
		);
		lootTables.put(
				Reg.TEST_ORE_END.get(),
				createSilkTouchTable("test_ore_end", Reg.TEST_ORE_END.get(), Reg.RAW_TEST_ORE.get(),
						1, 3, 0.5f, 0)
		);

		lootTables.put(
				Reg.TEST_POWERGENERATOR.get(),
				createStandardTable("test_power_generator", Reg.TEST_POWERGENERATOR.get(), Reg.TEST_POWERGENERATOR_BE.get())
		);
		lootTables.put(
				Reg.ORE_GENERATOR.get(),
				createStandardTable("ore_generator", Reg.ORE_GENERATOR.get(), Reg.ORE_GENERATOR_BE.get())
		);
	}
}
