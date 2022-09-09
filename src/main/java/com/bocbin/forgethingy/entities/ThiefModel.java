package com.bocbin.forgethingy.entities;

import com.bocbin.forgethingy.ForgeThingy;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;

public class ThiefModel extends HumanoidModel<ThiefEntity> {

	public static final String BODY = "body";

	public static ModelLayerLocation THIEF_LAYER = new ModelLayerLocation(new ResourceLocation(ForgeThingy.MODID, "thief"), BODY);

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = createMesh(CubeDeformation.NONE,  0.6f);
		return LayerDefinition.create(meshDefinition, 64, 32);
	}

	public ThiefModel(ModelPart part) {
		super(part);
	}
}
