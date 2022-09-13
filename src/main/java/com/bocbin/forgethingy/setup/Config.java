package com.bocbin.forgethingy.setup;

import com.bocbin.forgethingy.blocks.OreGeneratorConfig;
import com.bocbin.forgethingy.blocks.TestPowerGeneratorConfig;
import com.bocbin.forgethingy.worldgen.ores.TestOreConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {

	public static void register() {
		registerServerConfigs();
		registerClientConfigs();
		registerCommonConfigs();
	}

	private static void registerClientConfigs() {
		// configs are in toml, using forgeconfigspec.builder we can easily make them
		ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
		/*
		 * Client config is for configuration affecting the ONLY client state such as graphical options.
		 * Only loaded on the client side. Stored in the global config directory. Not synced. Suffix is
		 * "-client" by default.
		 */
		TestPowerGeneratorConfig.registerClientConfig(CLIENT_BUILDER);

		// finally
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
	}

	private static void registerCommonConfigs() {
		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
		/*
		 * Common mod config for configuration that needs to be loaded on both environments. Loaded on both
		 * servers and clients. Stored in the global config directory. NOT SYNCED. Suffix is "-common" by default.
		 *
		 * Stored globally
		 * For to do with worldgen
		 */
		TestOreConfig.registerConmmonConfig(COMMON_BUILDER);
		// finally
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
	}

	private static void registerServerConfigs() {
		ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
		/*
		 * Server type config is configuration that is associated with a server instance. Only loaded during
		 * server startup. Stored in a server/save specific "serverconfig" directory. Synced to clients during
		 * connection. Suffix is "-server" by default.
		 *
		 * (Most commonly used one, since its synced to the client)
		 * Stored per world (and per server)
		 * (not useful to put in configs that affect world configuraton)
		 * (defaultconfigs folder can be copied to each world on creation)
		 */
		OreGeneratorConfig.registerServerConfig(SERVER_BUILDER);
		TestPowerGeneratorConfig.registerServerConfig(SERVER_BUILDER);

		// finally
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
	}
}
