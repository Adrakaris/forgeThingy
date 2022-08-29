package com.bocbin.forgethingy.datagen;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.setup.Reg;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BlockStateProvider {

	public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, ForgeThingy.MODID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		// generate simple block models
		// will automatically use textures based on id of block if not specified
		simpleBlock(Reg.TEST_ORE.get());
		simpleBlock(Reg.TEST_ORE_DEEPSLATE.get());
		simpleBlock(Reg.TEST_ORE_END.get());
		simpleBlock(Reg.TEST_ORE_NETHER.get());
		registerPowerGenerator();
	}

	//region power generator
	private void registerPowerGenerator() {
		// need to make a custom model, in code
		BlockModelBuilder frame = models().getBuilder("block/test_power_generator/main");
		// its parent must be a cube so we can get it to go 3d inside the inventory
		frame.parent(models().getExistingFile(mcLoc("cube")));

		// copied because f that
		// frame
		cube(frame, 0f, 0f, 0f, 1f, 16f, 1f);
		cube(frame, 15f, 0f, 0f, 16f, 16f, 1f);
		cube(frame, 0f, 0f, 15f, 1f, 16f, 16f);
		cube(frame, 15f, 0f, 15f, 16f, 16f, 16f);

		cube(frame, 1f, 0f, 0f, 15f, 1f, 1f);
		cube(frame, 1f, 15f, 0f, 15f, 16f, 1f);
		cube(frame, 1f, 0f, 15f, 15f, 1f, 16f);
		cube(frame, 1f, 15f, 15f, 15f, 16f, 16f);

		cube(frame, 0f, 0f, 1f, 1f, 1f, 15f);
		cube(frame, 15f, 0f, 1f, 16f, 1f, 15f);
		cube(frame, 0f, 15f, 1f, 1f, 16f, 15f);
		cube(frame, 15f, 15f, 1f, 16f, 16f, 15f);

		cube(frame, 1f, 1f, 1f, 15f, 15f, 15f);  // centre

		frame.texture("window", modLoc("block/test_power_generator_window"));  // sets the key window NO HASH
		// to use the window texture
		// of which I'm not sure why it does this
		frame.texture("particle", modLoc("block/test_power_generator_off"));  // set the breaking particle texture

		createPowerGeneratorModel(Reg.TEST_POWERGENERATOR.get(), frame);
	}

	private void cube(BlockModelBuilder builder, float fx, float fy, float fz, float tx, float ty, float tz) {
		builder.element()
				.from(fx, fy, fz)
				.to(tx, ty, tz)
				.allFaces(((direction, faceBuilder) -> faceBuilder.texture("#window")))
				.end();
		// #window is an identifier for a texture that we want to define later
	}

	private void createPowerGeneratorModel(Block block, BlockModelBuilder frame) {
		// two variants: off and on
		// off
		// these are created for the DOWN facing position
		BlockModelBuilder genOff = models().getBuilder("block/test_power_generator/off")
				.element().from(3,3,3).to(13,13,13).face(Direction.DOWN).texture("#single").end().end()
				.texture("single", modLoc("block/test_power_generator_off"));
		BlockModelBuilder genOn = models().getBuilder("block/test_power_generator/on")
				.element().from(3,3,3).to(13,13,13).face(Direction.DOWN).texture("#single").end().end()
				.texture("single", modLoc("block/test_power_generator_on"));

		// combine frame and inside
		// This datagen is fucked up
		// can we not just use blockbench or something
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

		builder.part().modelFile(frame).addModel();
		// now to make it for every face
		BlockModelBuilder[] vars = new BlockModelBuilder[] {genOff, genOn};
		for (int i = 0; i < 2; i++) {
			builder.part().modelFile(vars[i]).addModel().condition(BlockStateProperties.POWERED, i == 1);
			builder.part().modelFile(vars[i]).rotationX(180).addModel().condition(BlockStateProperties.POWERED, i == 1);
			builder.part().modelFile(vars[i]).rotationX(90).addModel().condition(BlockStateProperties.POWERED, i == 1);
			builder.part().modelFile(vars[i]).rotationX(270).addModel().condition(BlockStateProperties.POWERED, i == 1);
			builder.part().modelFile(vars[i]).rotationY(90).rotationX(90).addModel().condition(BlockStateProperties.POWERED, i == 1);
			builder.part().modelFile(vars[i]).rotationY(270).rotationX(90).addModel().condition(BlockStateProperties.POWERED, i == 1);
		}

	}
	//endregion
}
