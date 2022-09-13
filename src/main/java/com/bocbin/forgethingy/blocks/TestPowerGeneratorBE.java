package com.bocbin.forgethingy.blocks;

import com.bocbin.forgethingy.setup.Reg;
import com.bocbin.forgethingy.utils.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class TestPowerGeneratorBE extends BlockEntity {

	// using "Capabilities" in be -- forge only
	// want interoperability between mods, which is what forge does
	// support two types of block entities (item and energy)
	private final ItemStackHandler itemHandler = createHandler();  // represents IInventory
	private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);  // the actual capability
	// lazy optional -- only call when first needed
	// energy storage
	private final CustomEnergyStorage energyStorage = createEnergy();
	private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);
//	private final ContainerData data = new ContainerData() {
//		@Override
//		public int get(int pIndex) {
//			return switch (pIndex) {
//				case 0 -> counter;
//				case 1 -> lastMax;
//				default -> 0;
//			};
//		}
//
//		@Override
//		public void set(int pIndex, int pValue) {
//			switch (pIndex) {
//				case 0 -> counter = pValue;
//				case 1 -> lastMax = pValue;
//			}
//		}
//
//		@Override
//		public int getCount() {
//			return 2;
//		}
//	};

	private int counter;

	private int lastMax;  // last maximum burn time

	public TestPowerGeneratorBE(BlockPos pPos, BlockState pBlockState) {
		super(Reg.TEST_POWERGENERATOR_BE.get(), pPos, pBlockState);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();  // good practice
		handler.invalidate();
		energy.invalidate();
	}

	public void tickServer() {
		// all the logic
		if (counter > 0) {
			energyStorage.addEnergy(TestPowerGeneratorConfig.GEN_RATE.get());
			counter--;
			setChanged();
		}

		// stop consuming if max energy
		if (counter <= 0 && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
			ItemStack stack = itemHandler.getStackInSlot(0);
			int burnTime = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
			if (burnTime > 0) {
				itemHandler.extractItem(0, 1, false);
				counter = lastMax = burnTime;
				setChanged();
			}
		}

		BlockState blockState = level.getBlockState(worldPosition);
		if (blockState.getValue(BlockStateProperties.POWERED) != (counter > 0)) {
			level.setBlock(
					worldPosition,
					blockState.setValue(BlockStateProperties.POWERED, counter > 0),
					Block.UPDATE_ALL  // 0b11: make block update, send to clients
			);
		}

		sendOutPower();
		// crucial -- forge energy is push power, not pull power
	}

	private void sendOutPower() {
		AtomicInteger storedEnergy = new AtomicInteger(energyStorage.getEnergyStored());  // must be atomic
		if (storedEnergy.get() > 0) {
			for (Direction dir : Direction.values()) {
				BlockEntity be = level.getBlockEntity(worldPosition.relative(dir));
				if (be != null) {
					// if we send out energy in south side, it will be inserted into other blocks' north side
					boolean loopContinue = be.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite())
							.map(otherHandler -> {
								if (otherHandler.canReceive()) {
									// how much we actually sent
									int received = otherHandler.receiveEnergy(Math.min(storedEnergy.get(), TestPowerGeneratorConfig.TRANSFER_RATE.get()), false);
									// change our local atomic stored energy
									storedEnergy.addAndGet(-received);
									energyStorage.removeEnergy(received);
									setChanged();
									// return true if we still have storage
									// otherwise false, and so we stop checking since no power
									return storedEnergy.get() > 0;
								} else {
									return true;
								}
							}).orElse(true);
					if (!loopContinue) {
						return;
					}
				}
			}
		}
	}

	//region to make sure block can be saved (like, inv saved when broken ig)

	@Override
	public void load(CompoundTag pTag) {
//		ForgeThingy.LOGGER.info("(DBG) Reloading data of Generator");

		if (pTag.contains("Inventory")) {
			itemHandler.deserializeNBT(pTag.getCompound("Inventory"));
//			this.setChanged();
		}
		if (pTag.contains("Energy")) {
			energyStorage.deserializeNBT(pTag.get("Energy"));
//			this.setChanged();
		}
		if (pTag.contains("Info")) {
			counter = pTag.getCompound("Info").getInt("Counter");
			lastMax = pTag.getCompound("Info").getInt("LastMax");
//			this.setChanged();
		}
		super.load(pTag);  // important

	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
//		ForgeThingy.LOGGER.info("(DBG) Starting Serialisation of Generator");
		// IMPORTANT: THE NBT TAGS MUST HAVE CAPITAL LETTERS
		// save everything inside tag
		pTag.put("Inventory", itemHandler.serializeNBT());
		pTag.put("Energy", energyStorage.serializeNBT());

		// assuming this is so we can do more in the tag
		CompoundTag infoTag = new CompoundTag();
		infoTag.putInt("Counter", counter);
		infoTag.putInt("LastMax", lastMax);
		pTag.put("Info", infoTag);
//		this.setChanged();
	}

	//endregion


	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getLastMax() {
		return lastMax;
	}

	public void setLastMax(int lastMax) {
		this.lastMax = lastMax;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		// this method must be fast
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			// hopper on top will call the getCapability to get the item handler
			return handler.cast();
		}
		if (cap == CapabilityEnergy.ENERGY) {
			return energy.cast();
		}
		return super.getCapability(cap, side);
	}

	public ItemStackHandler createHandler() {
		// size of inventory
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				// when a block entity is changed, it needs to be explicitly marked so Minecraft can record the change
				setChanged();
			}

			@Override
			public boolean isItemValid(int slot, @NotNull ItemStack stack) {
				// only allow items with burn time
				return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
			}

			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				if (ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) <= 0) {
					return stack;  // nothing inserted
				}
				return super.insertItem(slot, stack, simulate);
			}
		};
	}

	public CustomEnergyStorage createEnergy() {
		return new CustomEnergyStorage(TestPowerGeneratorConfig.CAPACITY.get(), TestPowerGeneratorConfig.TRANSFER_RATE.get()) {
			@Override
			protected void onEnergyChanged() {
				setChanged();
			}
		};
	}


}
