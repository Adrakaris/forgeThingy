package com.bocbin.forgethingy.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Tools {

	public static void spawnInWorld(Level level, BlockPos blockPos, ItemStack remaining) {
		if (!remaining.isEmpty()) {
			ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5, remaining);
			itemEntity.setPickUpDelay(40);
			itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0,1,0));
			level.addFreshEntity(itemEntity);
		}
	}
}
