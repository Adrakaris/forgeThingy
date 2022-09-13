package com.bocbin.forgethingy.worldgen.ores;

import net.minecraftforge.common.ForgeConfigSpec;

public class TestOreConfig {
	public static ForgeConfigSpec.IntValue OVERWORLD_VEINSIZE;  // = 5;
	public static ForgeConfigSpec.IntValue OVERWORLD_AMOUNT;  // = 3;
	public static ForgeConfigSpec.IntValue DEEPSLATE_VEINSIZE;  // = 6;
	public static ForgeConfigSpec.IntValue DEEPSLATE_AMOUNT;  // = 3;
	public static ForgeConfigSpec.IntValue NETHER_VEINSIZE;  // = 5;
	public static ForgeConfigSpec.IntValue NETHER_AMOUNT;  // = 3;
	public static ForgeConfigSpec.IntValue END_VEINSIZE;  // = 10;
	public static ForgeConfigSpec.IntValue END_AMOUNT;  // = 4;


	// worldgen, server config doesnt exist, so use common
	public static void registerConmmonConfig(ForgeConfigSpec.Builder builder) {
		builder.comment("Generation settings for the Test Ore").push("test_ore");

		OVERWORLD_VEINSIZE = builder.comment("Stone test ore max vein size (default 5)")
						.defineInRange("testOreStoneVeinsize", 5, 1, 1024);
		OVERWORLD_AMOUNT = builder.comment("Stone test ore spawn tries (default 3)")
						.defineInRange("testOreStoneTries", 3, 1, 128);
		DEEPSLATE_VEINSIZE = builder.comment("Deepslate test ore max vein size (default 6)")
						.defineInRange("testOreDeepslateVeinsize", 6, 1, 1024);
		DEEPSLATE_AMOUNT = builder.comment("Deepslate test ore spawn tries (default 3)")
						.defineInRange("testOreDeeslateTries", 3, 1, 128);
		NETHER_VEINSIZE = builder.comment("Nether test ore vein size (default 5)")
						.defineInRange("testOreNetherVeinsize", 5, 1, 1024);
		NETHER_AMOUNT = builder.comment("Nether test ore spawn tries (default 3)")
						.defineInRange("testOreNetherTries", 3, 1, 128);
		END_VEINSIZE = builder.comment("End test ore vein size (default 10)")
				.defineInRange("testOreNetherVeinsize", 10, 1, 1024);
		END_AMOUNT = builder.comment("End test ore spawn tries (default 4)")
				.defineInRange("testOreNetherTries", 4, 1, 128);

		builder.pop();
	}
}
