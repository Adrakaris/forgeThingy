package com.bocbin.forgethingy.entities;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.entities.ThiefEntity;
import com.bocbin.forgethingy.entities.ThiefModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

// displayer of model of entity
public class ThiefRenderer extends HumanoidMobRenderer<ThiefEntity, ThiefModel> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ForgeThingy.MODID, "textures/entity/thief.png");

	public ThiefRenderer(EntityRendererProvider.Context pContext) {
		super(pContext, new ThiefModel(pContext.bakeLayer(ThiefModel.THIEF_LAYER)), 1f);
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(ThiefEntity pEntity) {
		return TEXTURE;
	}
}
