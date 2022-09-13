package com.bocbin.forgethingy.client;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.blocks.TestPowerGeneratorBE;
import com.bocbin.forgethingy.blocks.TestPowerGeneratorConfig;
import com.bocbin.forgethingy.setup.Reg;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static java.lang.Boolean.TRUE;

public class TestPowerGeneratorRenderer implements BlockEntityRenderer<TestPowerGeneratorBE> {
	// block entity renderer
	// texture

	public static final ResourceLocation HALO = new ResourceLocation(ForgeThingy.MODID, "effect/halo");
	// unlike model loader, we have to manually put the textures on the atlas
	// happens in ClientSetup

	public TestPowerGeneratorRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(TestPowerGeneratorBE powergen, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
		// get powered, only want to render a star if powered
		// unlike baked models, you can directly access the block entity here
		Boolean powered = powergen.getBlockState().getValue(BlockStateProperties.POWERED);
		if (powered != TRUE) {
			return;
		}

		int brightness = LightTexture.FULL_BRIGHT;
		// slight animation effect based on current time: make star pulsate
		float s = (System.currentTimeMillis() % 1000) / 1000f;
		if (s > 0.5f) {
			s = 1.0f - s;
		}
		// Double -> double -> float  because java
		float scale = 0.1f + s * (float)(double)(TestPowerGeneratorConfig.STAR_SCALE.get());

		// get sprite
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(HALO);
		// posestack basically translates between world and camera view
		pPoseStack.pushPose();  // when modifying posestack, must first always push, then finally pop
		pPoseStack.translate(0.5, 1.5, 0.5);
		Quaternion rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
		pPoseStack.mulPose(rotation);  // make single quad, which always faces the camera
		// get a buffer for a custom render type to make our vertices on
		VertexConsumer buffer = pBufferSource.getBuffer(CustomRenderType.ADD);
		// make quad
		Matrix4f matrix = pPoseStack.last().pose();
		buffer.vertex(matrix, -scale, -scale, 0f).color(1f, 1f, 1f, 0.3f).uv(sprite.getU0(), sprite.getV0()).uv2(brightness).normal(1,0,0).endVertex();
		buffer.vertex(matrix, -scale, scale, 0f).color(1f, 1f, 1f, 0.3f).uv(sprite.getU0(), sprite.getV1()).uv2(brightness).normal(1,0,0).endVertex();
		buffer.vertex(matrix, scale, scale, 0f).color(1f, 1f, 1f, 0.3f).uv(sprite.getU1(), sprite.getV1()).uv2(brightness).normal(1,0,0).endVertex();
		buffer.vertex(matrix, scale, -scale, 0f).color(1f, 1f, 1f, 0.3f).uv(sprite.getU1(), sprite.getV0()).uv2(brightness).normal(1,0,0).endVertex();
		// final pop
		pPoseStack.popPose();

	}

	public static void register() {
		BlockEntityRenderers.register(Reg.TEST_POWERGENERATOR_BE.get(), TestPowerGeneratorRenderer::new);
	}



}
