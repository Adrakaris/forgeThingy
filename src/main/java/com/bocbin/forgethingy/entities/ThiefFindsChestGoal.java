package com.bocbin.forgethingy.entities;

import com.bocbin.forgethingy.utils.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Random;

// want to steal items from a chest and drop them onto the ground
public class ThiefFindsChestGoal extends MoveToBlockGoal {

	private final ThiefEntity thief;
	private final Random random = new Random();

	private int stealingCounter = 20;

	public ThiefFindsChestGoal(ThiefEntity pMob, double pSpeedModifier) {
		super(pMob, pSpeedModifier, 16);
		this.thief = pMob;
	}

	// reset task internal state
	// basically, if player approaches, that's a higher priority to avoid the player, and thus
	// the stop must be called here so that the avoid player action can start
	@Override
	public void stop() {
		super.stop();
		thief.setStealing(false);
		// close chest
		BlockEntity be = mob.level.getBlockEntity(blockPos);
		if (be instanceof ChestBlockEntity) {
			// check ChestBlockEntity for the triggerEvent function
			mob.level.blockEvent(blockPos, be.getBlockState().getBlock(), 1, 0);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (isReachedTarget()) {
			BlockEntity be = mob.level.getBlockEntity(blockPos);
			if (be instanceof ChestBlockEntity chest) {
				if (thief.isStealing()) {
					// wait 20 ticks to steal an item and drop it
					stealingCounter--;
					if (stealingCounter <= 0) {
						stealingCounter = 20;
						ItemStack stack = extractRandomItem(chest);
						if (!stack.isEmpty()) {
							Tools.spawnInWorld(mob.level, blockPos.above(), stack);
						}
					}
				} else {
					// send event to open chest and begin stealing
					mob.level.blockEvent(blockPos, be.getBlockState().getBlock(), 1, 1);
					stealingCounter = 20;
					thief.setStealing(true);
				}
			}
		}
	}

	private ItemStack extractRandomItem(ChestBlockEntity chest) {
		return chest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).map(
				handler -> {
					for (int i = 0; i < handler.getSlots(); i++) {
						ItemStack stack = handler.getStackInSlot(i);
						if (!stack.isEmpty()) {
							if (random.nextFloat() < .3f) {
								return handler.extractItem(i,1,false);
							}
						}
					}
					return ItemStack.EMPTY;
				}
		).orElse(ItemStack.EMPTY);
	}

	@Override
	protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
		// must be overriden
		if (!pLevel.isEmptyBlock(pPos.above())) {
			return false;
		} else {
			BlockState blockState = pLevel.getBlockState(pPos);
			return blockState.is(Blocks.CHEST);
		}
	}
}
