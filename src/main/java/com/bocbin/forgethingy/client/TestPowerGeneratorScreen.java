package com.bocbin.forgethingy.client;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.blocks.TestPowerGenerator;
import com.bocbin.forgethingy.blocks.TestPowerGeneratorContainer;
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
	}

	@Override
	protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
		// render energy, called later
		drawString(pPoseStack, Minecraft.getInstance().font, "Energy: " + menu.getEnergy(), 10, 10, 0xffffff);
	}
}
