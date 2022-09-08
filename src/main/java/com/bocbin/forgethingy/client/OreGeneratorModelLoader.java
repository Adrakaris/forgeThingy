package com.bocbin.forgethingy.client;

import com.bocbin.forgethingy.ForgeThingy;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class OreGeneratorModelLoader implements IModelLoader<OreGeneratorModelLoader.OreGeneratorModelGeometry> {

	// key
	public static final ResourceLocation OREGEN_LOADER = new ResourceLocation(ForgeThingy.MODID, "ore_generator_loader");
	// face textures
	public static final ResourceLocation OREGEN_FRONT_POWERED = new ResourceLocation(ForgeThingy.MODID, "block/ore_generator_front_powered");
	public static final ResourceLocation OREGEN_FRONT = new ResourceLocation(ForgeThingy.MODID, "block/ore_generator_front");
	public static final ResourceLocation OREGEN_SIDE = new ResourceLocation(ForgeThingy.MODID, "block/ore_generator_side");
	public static final ResourceLocation OREGEN_BUTTON_ON = new ResourceLocation(ForgeThingy.MODID, "block/ore_generator_on");
	public static final ResourceLocation OREGEN_BUTTON_OFF = new ResourceLocation(ForgeThingy.MODID, "block/ore_generator_off");
	// we also need to define (client side model) materials (automatically) for them
	// a material is a resourcelocation for the texture and the texture atlas
	// (since more efficient for OpenGL to have as few textures as possible, so better to combine all textures into
	// one texture atlas
	public static final Material MATERIAL_FRONT_POWERED = ForgeHooksClient.getBlockMaterial(OREGEN_FRONT_POWERED);
	public static final Material MATERIAL_FRONT = ForgeHooksClient.getBlockMaterial(OREGEN_FRONT);
	public static final Material MATERIAL_SIDE = ForgeHooksClient.getBlockMaterial(OREGEN_SIDE);
	public static final Material MATERIAL_BUTTON_ON = ForgeHooksClient.getBlockMaterial(OREGEN_BUTTON_ON);
	public static final Material MATERIAL_BUTTON_OFF = ForgeHooksClient.getBlockMaterial(OREGEN_BUTTON_OFF);

	@Override
	public void onResourceManagerReload(ResourceManager pResourceManager) {
	}

	@Override
	public OreGeneratorModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		return new OreGeneratorModelGeometry();
	}

	// specific class for model geometry
	public static class OreGeneratorModelGeometry implements IModelGeometry<OreGeneratorModelGeometry> {

		@Override
		public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
			return new OreGeneratorBakedModel(modelTransform, spriteGetter, overrides, owner.getCameraTransforms());
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			// baked model system works with "materials", i.e. textures of things
			return List.of(MATERIAL_FRONT, MATERIAL_FRONT_POWERED, MATERIAL_SIDE, MATERIAL_BUTTON_ON, MATERIAL_BUTTON_OFF);
		}
	}
}
