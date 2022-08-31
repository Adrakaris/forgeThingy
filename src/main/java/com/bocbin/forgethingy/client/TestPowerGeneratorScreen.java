package com.bocbin.forgethingy.client;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.blocks.TestPowerGenerator;
import com.bocbin.forgethingy.blocks.TestPowerGeneratorContainer;
import com.bocbin.forgethingy.utils.UtilFunctions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

// AbCoScreen is the vanilla screen that knows how to use containers
public class TestPowerGeneratorScreen extends AbstractContainerScreen<TestPowerGeneratorContainer> {

	private final ResourceLocation GUI = new ResourceLocation(ForgeThingy.MODID, "textures/gui/container/test_power_generator.png");

	public TestPowerGeneratorScreen(TestPowerGeneratorContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		renderBackground(pPoseStack);
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
		this.renderTooltip(pPoseStack, pMouseX, pMouseY);
	}

	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		// render UI background, called first
		RenderSystem.setShaderTexture(0, GUI);
		int relX = (width - imageWidth) / 2;
		int relY = (height - imageHeight) / 2;
		this.blit(pPoseStack, relX, relY, 0, 0, imageWidth, imageHeight);

		// render the fire flame
		int counter = menu.getCounter();
//		ForgeThingy.LOGGER.info("(dbg) gen counter at {}", counter);
		if (counter > 0) {
			int lastMax = menu.getLastMax();
			int k = (int) Math.floor(UtilFunctions.scale(counter, 0f, lastMax, 0f, 14f));
//			ForgeThingy.LOGGER.info("(dbg) Render -> counter {} lastMax {} k {}", counter, lastMax, k);
			this.blit(pPoseStack, relX+56, relY+40-k, 176, 12-k, 14, k+1);
//			this.blit(pPoseStack, relX+56, relY+28, 176, 0, 14, 12);
		}

	}

	@Override
	protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
		// render energy, called later
		drawString(pPoseStack, Minecraft.getInstance().font, "Energy: " + menu.getEnergy(), 10, 10, 0xffffff);
//		drawString(pPoseStack, Minecraft.getInstance().font, "> Counter: " + menu.getCounter(), 10, 20, 0xffffff);
//		drawString(pPoseStack, Minecraft.getInstance().font, "> LastMax: " + menu.getLastMax(), 10, 30, 0xffffff);
	}
}
