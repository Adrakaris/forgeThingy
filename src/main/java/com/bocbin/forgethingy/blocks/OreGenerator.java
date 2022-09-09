package com.bocbin.forgethingy.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OreGenerator extends Block implements EntityBlock {

	public static final String OREGEN_MESSAGE = "ore_generator.message";

	// again, it's not a full block
	// but now, it's not as wide as a full block, and so we want the black outline when you
	// hover over a block to be shrunk to match the block for each orientation of the block
	public static final VoxelShape SHAPE_DOWN = Shapes.box(0,.2,0,1,1,1);
	public static final VoxelShape SHAPE_UP = Shapes.box(0,0,0,1,.8,1);
	public static final VoxelShape SHAPE_NORTH = Shapes.box(0,0,.2,1,1,1);
	public static final VoxelShape SHAPE_SOUTH = Shapes.box(0,0,0,1,1,.8);
	public static final VoxelShape SHAPE_WEST = Shapes.box(.2,0,0,1,1,1);
	public static final VoxelShape SHAPE_EAST = Shapes.box(0,0,0,.8,1,1);

	public OreGenerator() {
		super(Properties.of(Material.METAL)
				.sound(SoundType.METAL)
				.strength(2f)
				.noOcclusion()
				.requiresCorrectToolForDrops()
		);
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		pTooltip.add(new TranslatableComponent(OREGEN_MESSAGE).withStyle(ChatFormatting.GRAY));
	}

	// get the actual outline shape
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return switch (pState.getValue(BlockStateProperties.FACING)) {
			case DOWN -> SHAPE_DOWN;
			case UP -> SHAPE_UP;
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case EAST -> SHAPE_EAST;
			case WEST -> SHAPE_WEST;
		};
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new OreGeneratorBE(pPos, pState);
	}

	// server ticker
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (!pLevel.isClientSide()) {
			return (lvl, pos, state, be) -> {
				if (be instanceof OreGeneratorBE gen) gen.tickServer();
			};
		}
		return null;
	}

	// use function
	// this is going to be an inventoryless block entity, and so the use function needs to
	// be programmed to interact from the side
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (!pLevel.isClientSide()) {
			BlockEntity be = pLevel.getBlockEntity(pPos);
			if (be instanceof OreGeneratorBE gen) {
				Direction direction = pHit.getDirection();  // which side of block is hit
				Direction facing = pState.getValue(BlockStateProperties.FACING);  // what block facing
				// if face hit is same as direction as block facing, then we hit the front of the block
				if (direction == facing) {
					// get the difference between the hit position and the block position
					// will get a relative position from the (I'm assuming) bottom left of a block
					Vec3 hit = pHit.getLocation().subtract(pPos.getX(), pPos.getY(), pPos.getZ());
					// vec3 is 3d of world space, so would be different for each face
					// so we need absolute dimension of face coordinates, 2d
					double x = getXFromHit(facing, hit);
					double y = getYFromHit(facing, hit);

					if (x < .5 && y > .5) {  // a specific portion of the block face
						gen.setCollecting(!gen.isCollecting());  // field in generator
					} else if (x > 0.5 && y > 0.5) {
						gen.setGenerating(!gen.isGenerating());
					} else if (x > 0.5 && y < 0.5) {
						ItemStack itemStack = pPlayer.getItemInHand(pHand);
						// if it is a block(Item), we get the block state and give it to the
						// entity  -- since ofc block is singleton and blockstate is instance
						if (itemStack.getItem() instanceof BlockItem bItem) {
							BlockState blockState = bItem.getBlock().defaultBlockState();
							gen.setGeneratingBlock(blockState);
						}
					}
				}
			}
		}
		return InteractionResult.SUCCESS;
	}

	// get the right FACE-RELATIVE x coordinate
	// trust the maths I guess.
	private double getYFromHit(Direction facing, Vec3 hit) {
		return switch (facing) {
			case UP, DOWN, NORTH -> 1-hit.x;
			case SOUTH -> hit.x;
			case WEST -> hit.z;
			case EAST -> 1-hit.z;
		};
	}
	private double getXFromHit(Direction facing, Vec3 hit) {
		return switch (facing) {
			case UP -> hit.z;
			case DOWN -> 1-hit.z;
			case NORTH, SOUTH, EAST, WEST -> hit.y;
		};
	}

	// add block states
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return defaultBlockState().setValue(BlockStateProperties.FACING, pContext.getNearestLookingDirection().getOpposite());
	}
}
