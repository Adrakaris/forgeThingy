package com.bocbin.forgethingy;

import com.bocbin.forgethingy.setup.ClientSetup;
import com.bocbin.forgethingy.setup.ModSetup;
import com.bocbin.forgethingy.setup.Reg;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ForgeThingy.MODID)
public class ForgeThingy {
	// Directly reference a slf4j logger
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MODID = "forgethingy";

	public ForgeThingy() {

		// registering the "deferred registery"
		Reg.init();

		// register mod loading events
		IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
		// two event buses: mod event for modloading and forge event for in game
		modbus.addListener(ModSetup::init);  // common setup
		// add a cloent only listener
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientSetup::init));  // client setup

//		// Register the setup method for modloading
//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
//		// Register the enqueueIMC method for modloading
//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
//		// Register the processIMC method for modloading
//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
//		// Register ourselves for server and other game events we are interested in
//		MinecraftForge.EVENT_BUS.register(this);
	}

	// MCJTY tutorial: he simplified the main mod class and removed this pregenerated code

//	private void setup(final FMLCommonSetupEvent event) {
//		// some preinit code
//		LOGGER.info("HELLO FROM PREINIT");
//		LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
//	}
//
//	private void enqueueIMC(final InterModEnqueueEvent event) {
//		// Some example code to dispatch IMC to another mod
//		InterModComms.sendTo("examplemod", "helloworld", () -> {
//			LOGGER.info("Hello world from the MDK");
//			return "Hello world";
//		});
//	}
//
//	private void processIMC(final InterModProcessEvent event) {
//		// Some example code to receive and process InterModComms from other mods
//		LOGGER.info("Got IMC {}", event.getIMCStream().
//				map(m -> m.messageSupplier().get()).
//				collect(Collectors.toList()));
//	}
//
//	// You can use SubscribeEvent and let the Event Bus discover methods to call
//	@SubscribeEvent
//	public void onServerStarting(ServerStartingEvent event) {
//		// Do something when the server starts
//		LOGGER.info("HELLO from server starting");
//	}
//
//	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
//	// Event bus for receiving Registry Events)
//	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//	public static class RegistryEvents {
//		@SubscribeEvent
//		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
//			// Register a new block here
//			LOGGER.info("HELLO from Register Block");
//		}
//	}

}
