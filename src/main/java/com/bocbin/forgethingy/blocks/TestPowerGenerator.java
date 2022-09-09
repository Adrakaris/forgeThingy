package com.bocbin.forgethingy.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestPowerGenerator extends Block implements EntityBlock {
	// all block entities Blocks must implemenet EntityBlock

	public static final String POWERGEN_MESSAGE_1 = "test_power_generator.message.1";
	public static final String POWERGEN_MESSAGE_2 = "test_power_generator.message.2";
	public static final String POWERGEN_UI_TITLE = "test_power_generator.tutorial.screen";

	// will not be a full (solid?) block, so we have a different render shape, to prevent "weird occlusion problems"
	private static final VoxelShape RENDER_SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

	public TestPowerGenerator() {
		super(
				Properties.of(Material.METAL)
						.sound(SoundType.METAL)
						.strength(2f)
						.lightLevel(state -> state.getValue(BlockStateProperties.POWERED) ? 14 : 0)
						.requiresCorrectToolForDrops()
				// blockstateproperties is vanilla, prefer to use these if possible but can create own
		);
	}

	// deprecation: shouldn't call, can override
	// get occlusion shape: see RENDER_SHAPE
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return RENDER_SHAPE;
	}

	// for a tooltip, always best practice to use translatable messages
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		pTooltip.add(new TranslatableComponent(POWERGEN_MESSAGE_1).withStyle(ChatFormatting.GRAY));
		pTooltip.add(new TranslatableComponent(POWERGEN_MESSAGE_2, Integer.toString(TestPowerGeneratorBE.GEN_RATE)).withStyle(ChatFormatting.GRAY));
	}

	// to make block support blockstateproperties
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.POWERED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext).setValue(BlockStateProperties.POWERED, false);
	}

	// --- BEs ---

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new TestPowerGeneratorBE(pPos, pState);
	}

	// need to do something evenry tick


	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pLevel.isClientSide()) {
			return null;  // nothing on client
		}
		// on server
		// the lambda implements the BlockEntityTicker functional interface
		return (pLevel1, pPos, pState1, pBlockEntity) -> {
			if (pBlockEntity instanceof TestPowerGeneratorBE tile) {
				tile.tickServer();
			}
		};
	}

	// use is called both sides
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (!pLevel.isClientSide()) {
			// need to open UI on server
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if (blockEntity instanceof TestPowerGeneratorBE) {
				MenuProvider containerProvider = new MenuProvider() {  // create a container factory server side
					@Override
					public Component getDisplayName() {
						return new TranslatableComponent(POWERGEN_UI_TITLE);
					}

					@Nullable
					@Override
					public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
						return new TestPowerGeneratorContainer(pContainerId, pPos, pPlayerInventory, pPlayer);
					}
				};
				// communicate to client
				NetworkHooks.openGui((ServerPlayer) pPlayer, containerProvider, blockEntity.getBlockPos());
			} else {
				throw new IllegalStateException("TestPowerGenerator: Named container provider is missing!");
			}
		}
		return InteractionResult.SUCCESS;
	}
}
