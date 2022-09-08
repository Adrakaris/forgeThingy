package com.bocbin.forgethingy.setup;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.client.OreGeneratorModelLoader;
import com.bocbin.forgethingy.client.TestPowerGeneratorRenderer;
import com.bocbin.forgethingy.client.TestPowerGeneratorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ForgeThingy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

	public static void init(final FMLClientSetupEvent event) {
		// need to couple our power generator GUI to our block entity
		// we use enqueuework since we need to do it on the main thread, since our registration storage
		// is a thread-unsafe hashmap
		event.enqueueWork(() -> {
			MenuScreens.register(Reg.TEST_POWERGENERATOR_CONTAINER.get(), TestPowerGeneratorScreen::new);
			ItemBlockRenderTypes.setRenderLayer(Reg.TEST_POWERGENERATOR.get(), RenderType.translucent());
			TestPowerGeneratorRenderer.register();
		});

	}

	// event for adding baked models
	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event) {
		// register our baked model
		ModelLoaderRegistry.registerLoader(OreGeneratorModelLoader.OREGEN_LOADER, new OreGeneratorModelLoader());
	}

	// event for manually stitching textures onto the atlas
	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event) {
		if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			return;
		}
		event.addSprite(TestPowerGeneratorRenderer.HALO);
	}
}
