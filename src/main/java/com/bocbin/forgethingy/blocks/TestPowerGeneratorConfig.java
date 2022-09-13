package com.bocbin.forgethingy.blocks;

import net.minecraftforge.common.ForgeConfigSpec;

import static java.lang.Integer.MAX_VALUE;

public class TestPowerGeneratorConfig {
	public static ForgeConfigSpec.IntValue CAPACITY; // = 100_000;
	public static ForgeConfigSpec.IntValue GEN_RATE; // = 80;
	public static ForgeConfigSpec.IntValue TRANSFER_RATE; // = 240;

	// size of the star that gets rendered on top
	public static ForgeConfigSpec.DoubleValue STAR_SCALE;

	public static void registerServerConfig(ForgeConfigSpec.Builder builder) {
		builder.comment("Settings for the Power Generator").push("test_power_generator");

		CAPACITY = builder.comment("Internal capacity in FE (default 100,000)")
						.defineInRange("PowerGenCapacity", 100_000, 1, MAX_VALUE);
		GEN_RATE = builder.comment("Generation rate in FE/t (default 80)")
						.defineInRange("powerGenRate", 80, 1, MAX_VALUE);
		TRANSFER_RATE = builder.comment("Transfer rate per side in RF/t (default 240)")
						.defineInRange("powerGenTransferRate", 240, 1, MAX_VALUE);

		builder.pop();
	}

	public static void registerClientConfig(ForgeConfigSpec.Builder builder) {
		builder.comment("Client only settings for the Power Generator").push("test_power_generator");

		STAR_SCALE = builder.comment("How large in size the star is that is rendered when generating")
						.defineInRange("powerGenStarScale", 0.3, 0.00001, 100.0);

		builder.pop();
	}
}
