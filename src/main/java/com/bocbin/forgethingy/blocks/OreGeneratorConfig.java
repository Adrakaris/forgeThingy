package com.bocbin.forgethingy.blocks;

import net.minecraftforge.common.ForgeConfigSpec;

import static java.lang.Integer.MAX_VALUE;

public class OreGeneratorConfig {
	// static fields, could be put in config files
	public static ForgeConfigSpec.IntValue COLLECT_DELAY;  // = 10;
	public static ForgeConfigSpec.IntValue INGOTS_PER_ORE;  // = 8;
	public static ForgeConfigSpec.IntValue ENERGY_CAP;  // = 100_000;
	public static ForgeConfigSpec.IntValue ENERGY_RECEIVE;  // = 1000;
	public static ForgeConfigSpec.IntValue ENERGY_USE;  // = 500;

	public static void registerServerConfig(ForgeConfigSpec.Builder builder) {
		builder.comment("Settings for the Ore Generator").push("ore_generator");
		// add comment and make new section ore_generator

		COLLECT_DELAY = builder.comment("Delay (in ticks) before trying to collect items (default 10)")
						.defineInRange("oreGenCollectDelay", 10, 1, MAX_VALUE);
		INGOTS_PER_ORE = builder.comment("Number of ingots which are required to generate an ore (default 8)")
						.defineInRange("oreGenIngotsPerOre", 8, 1, MAX_VALUE);
		ENERGY_CAP = builder.comment("Energy stored by block (default 100 000)")
						.defineInRange("oreGenEnergyCap", 100_000, 1, MAX_VALUE);
		ENERGY_RECEIVE = builder.comment("Energy the block is able to receive per side per tick (default 1000)")
						.defineInRange("oreGenEnergyReceive", 1000, 1, MAX_VALUE);
		ENERGY_USE = builder.comment("Energy the block uses per tick (default 500)")
						.defineInRange("oreGenEnergyUse", 500, 1, MAX_VALUE);

		builder.pop();
	}
}
