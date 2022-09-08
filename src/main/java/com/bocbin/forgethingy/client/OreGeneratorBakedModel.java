package com.bocbin.forgethingy.client;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.blocks.OreGenerator;
import com.bocbin.forgethingy.blocks.OreGeneratorBE;
import com.bocbin.forgethingy.utils.ClientTools;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import static java.lang.Boolean.TRUE;
import static com.bocbin.forgethingy.utils.ClientTools.v3;

public class OreGeneratorBakedModel implements IDynamicBakedModel {
	// responsible for generating a static geometry in a dynamic way
	// IDynamicBakedModel can change

	private final ModelState modelState;
	private final Function<Material, TextureAtlasSprite> spriteGetter;
	// want to cache quads, since it's hard work to calculate and easier to store for later
	private final Map<ModelKey, List<BakedQuad>> quadCache = new HashMap<>();
	private final ItemOverrides overrides;
	private final ItemTransforms itemTransforms;

	public OreGeneratorBakedModel(ModelState modelState, Function<Material, TextureAtlasSprite> spriteGetter,
								  ItemOverrides itemOverrides, ItemTransforms itemTransforms) {
		this.modelState = modelState;
		this.spriteGetter = spriteGetter;
		// the next two are for item model
		this.overrides = itemOverrides;
		this.itemTransforms = itemTransforms;
		// want to cache the quads, since getQuads can be threaded and thus remaking would otherwise
		// cause issues
		generateQuadCache();

	}

	// a quad is a rectangle - face on shape
	@NotNull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData extraData) {
		// get extra parameter data and block state, and side it's looking from
		// whenever a block is rendered, this is called SEVEN times
		// once with Direction = null, then once for each side
		// for quad culling optimisations
		// you can use the random as well

		// check are we the solid render type
		// solid render type is where all solid geometry is rendered, and where we want the baked model
		RenderType layer = MinecraftForgeClient.getRenderType();

		// we only want to render entire model if side is null, otherwise ignored (i.e. fuck the optimisations)
		if (side != null || (layer != null && !layer.equals(RenderType.solid()))) {
			return Collections.emptyList();
		}

		boolean generating = TRUE == extraData.getData(OreGeneratorBE.GENERATING);  // TRUE == also implicitly checks for null and null is false
		boolean collecting = TRUE == extraData.getData(OreGeneratorBE.COLLECTING);
		boolean working = TRUE == extraData.getData(OreGeneratorBE.WORKING);

		List<BakedQuad> quads = getQuadsForGeneratingBlock(state, rand, extraData, layer);

		ModelKey key = new ModelKey(generating, collecting, working, modelState);
		quads.addAll(quadCache.get(key));
		// this combines the base block model with the target (generating) block model.
		return quads;
	}

	private void generateQuadCache() {

		quadCache.put(new ModelKey(false, false, false, modelState), generateQuads(false, false, false));
		quadCache.put(new ModelKey(false, false, true, modelState), generateQuads(false, false, true));
		quadCache.put(new ModelKey(false, true, false, modelState), generateQuads(false, true, false));
		quadCache.put(new ModelKey(false, true, true, modelState), generateQuads(false, true, true));
		quadCache.put(new ModelKey(true, false, false, modelState), generateQuads(true, false, false));
		quadCache.put(new ModelKey(true, false, true, modelState), generateQuads(true, false, true));
		quadCache.put(new ModelKey(true, true, false, modelState), generateQuads(true, true, false));
		quadCache.put(new ModelKey(true, true, true, modelState), generateQuads(true, true, true));
	}

	@NotNull
	private List<BakedQuad> generateQuads(boolean generating, boolean collecting, boolean working) {
		// death
		var quads = new ArrayList<BakedQuad>();
		// left, right, and half
		float l = 0;
		float r = 1;
		float h = .5f;

		float p = 13f / 16f;  // relative position of panel (the block is only 13 px deep)
		float bl = 1f / 16f;  // left side of button
		float br = 7f / 16f;  // right side of button

		Transformation rotation = modelState.getRotation();

		TextureAtlasSprite textureSide = spriteGetter.apply(OreGeneratorModelLoader.MATERIAL_SIDE);
		TextureAtlasSprite textureFrontPowered = spriteGetter.apply(OreGeneratorModelLoader.MATERIAL_FRONT_POWERED);
		TextureAtlasSprite textureFront = spriteGetter.apply(OreGeneratorModelLoader.MATERIAL_FRONT);
		TextureAtlasSprite textureOn = spriteGetter.apply(OreGeneratorModelLoader.MATERIAL_BUTTON_ON);
		TextureAtlasSprite textureOff = spriteGetter.apply(OreGeneratorModelLoader.MATERIAL_BUTTON_OFF);

		// base block
		quads.add(ClientTools.createQuad(v3(r,p,r), v3(r,p,l), v3(l,p,l), v3(l,p,r), rotation, working ? textureFrontPowered : textureFront));
		quads.add(ClientTools.createQuad(v3(l,l,l), v3(r,l,l), v3(r,l,r), v3(l,l,r), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(r,p,r), v3(r,l,r), v3(r,l,l), v3(r,p,l), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(l,p,l), v3(l,l,l), v3(l,l,r), v3(l,p,r), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(r,p,l), v3(r,l,l), v3(l,l,l), v3(l,p,l), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(l,p,r), v3(l,l,r), v3(r,l,r), v3(r,p,r), rotation, textureSide));

		// collecting button
		float s = collecting ? 14f/16f : r;  // pressed / not pressed
		float off = 0;
		quads.add(ClientTools.createQuad(v3(br, s, br+off), v3(br, s, bl+off), v3(bl, s, bl+off), v3(bl, s, br+off), rotation, collecting ? textureOn : textureOff));
		quads.add(ClientTools.createQuad(v3(br, s, br+off), v3(br, p, br+off), v3(br, p, bl+off), v3(br, s, bl+off), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(bl, s, bl+off), v3(bl, p, bl+off), v3(bl, p, br+off), v3(bl, s, br+off), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(br, s, bl+off), v3(br, p, bl+off), v3(bl, p, bl+off), v3(bl, s, bl+off), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(bl, s, br+off), v3(bl, p, br+off), v3(br, p, br+off), v3(br, s, br+off), rotation, textureSide));

		// generating
		s = generating ? 14f/16f : r;
		off = h;
		quads.add(ClientTools.createQuad(v3(br, s, br+off), v3(br, s, bl+off), v3(bl, s, bl+off), v3(bl, s, br+off), rotation, generating ? textureOn : textureOff));
		quads.add(ClientTools.createQuad(v3(br, s, br+off), v3(br, p, br+off), v3(br, p, bl+off), v3(br, s, bl+off), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(bl, s, bl+off), v3(bl, p, bl+off), v3(bl, p, br+off), v3(bl, s, br+off), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(br, s, bl+off), v3(br, p, bl+off), v3(bl, p, bl+off), v3(bl, s, bl+off), rotation, textureSide));
		quads.add(ClientTools.createQuad(v3(bl, s, br+off), v3(bl, p, br+off), v3(br, p, br+off), v3(br, s, br+off), rotation, textureSide));

		return quads;
		// holy shit.

		// I mean if you look at `createQuad` in clientTools this is entirely mad
	}

	private List<BakedQuad> getQuadsForGeneratingBlock(BlockState state, Random rand, IModelData extraData, RenderType layer) {
		// get the quads for rendering the ore
		List<BakedQuad> quads = new ArrayList<>();
		BlockState targetBlock = extraData.getData(OreGeneratorBE.TARGET_BLOCK);
		if (targetBlock != null && !(targetBlock.getBlock() instanceof OreGenerator)) {
			if (layer == null || ItemBlockRenderTypes.canRenderInLayer(targetBlock, layer)) {
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(targetBlock);

				// rotate it and scale to position
				try {
					Direction facing = state == null ? Direction.DOWN : state.getValue(BlockStateProperties.FACING);
					Transformation rotation = modelState.getRotation();
					Transformation translate = transformTargetBlock(facing, rotation);
					QuadTransformer transformer = new QuadTransformer(translate);

					for (Direction d : Direction.values()) {
						// for all sides of block, we get quads and add to list of quads
						List<BakedQuad> modelQuads = model.getQuads(targetBlock, d, rand, EmptyModelData.INSTANCE);

						for (BakedQuad quad : modelQuads) {
							quads.add(transformer.processOne(quad));
						}  // with the correct rotation
					}
				} catch (Exception ignored) {
					// ignored, apparently
				}
			}
		}
		return quads;
	}

	/**
	 * Generate a transform that will transform the ore block that we're generating to a smaller version
	 * that fits nicely into our front panel.
	 */
	@NotNull
	private Transformation transformTargetBlock(Direction facing, Transformation rotation) {
		// Note: when composing a transformation like this you have to imagine these transformations in reverse order.
		// 	So this routine makes most sense if you read it from end to beginning

		// Facing refers to the front face of our block. So dX, dY, dZ are the offsets pointing
		// in that direction. We want to move our model slightly to the front and top-left corner
		float dx = facing.getStepX();
		float dy = facing.getStepY();
		float dz = facing.getStepZ();
		// Correct depending on face. After this dX, dY, dZ will be the offset perpendicular to
		// the direction of our face
		switch (facing) {
			case DOWN -> { dx=1; dy=0; dz=-1; }
			case UP -> { dx=1; dy=0; dz=1; }
			case NORTH -> { dx=1; dy=1; dz=0; }
			case SOUTH -> { dx=-1; dy=1; dz=0; }
			case WEST -> { dx=0; dy=1; dz=-1; }
			case EAST -> { dx=0; dy=1; dz=1; }
		}

		// Calculate the first translation (before scaling/rotating). Basically we move in three steps:
		//   - Move in the direction that our front face is facing (divided by 4)
		//   - Move perpendicular to that direction (also divided by 4)
		//   - Move half a block so that rotation and scaling will happen relative to the center
		float stepX = facing.getStepX() / 4f + dx / 4f + .5f;
		float stepY = facing.getStepY() / 4f + dy / 4f + .5f;
		float stepZ = facing.getStepZ() / 4f + dz / 4f + .5f;

		// As the final step (remember, read from end to start) we position our correctly rotated and scaled
		// block to the top-left corner of our main block
		Transformation translate = new Transformation(Matrix4f.createTranslateMatrix(stepX, stepY, stepZ));

		// Now our block is correctly positioned where we want to rotate and scale it
		translate = translate.compose(new Transformation(Matrix4f.createScaleMatrix(0.2f, 0.2f, 0.2f)));
		translate = translate.compose(rotation);  // main model rotation (for right facing

		// This will happen first: translate our subblock so it's center is at 0,0,0. That way scaling and
		// rotating will be correct and not change the position
		translate = translate.compose(new Transformation(Matrix4f.createTranslateMatrix(-0.5f, -0.5f, -0.5f)));

		return translate;
	}



	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return spriteGetter.apply(OreGeneratorModelLoader.MATERIAL_SIDE);
	}

	@Override
	public ItemOverrides getOverrides() {
		return overrides;
	}

	@Override
	public ItemTransforms getTransforms() {
		return itemTransforms;
	}
}
