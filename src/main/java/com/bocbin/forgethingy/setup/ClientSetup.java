package com.bocbin.forgethingy.setup;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.client.TestPowerGeneratorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
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
		});

	}
}
