package com.bocbin.forgethingy.blocks;

import com.bocbin.forgethingy.setup.Reg;
import com.bocbin.forgethingy.utils.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

// tracks both the inventory of the block and the inventory of the player
public class TestPowerGeneratorContainer extends AbstractContainerMenu {
	// important since with a container it opens on both client side and server side
	// container is bridge between client and server

	private BlockEntity blockEntity;
	private Player player;
	private IItemHandler playerInventory;

	public TestPowerGeneratorContainer(int containerID, BlockPos blockPos, Inventory inv, Player player) {
		// vanilla method to reference inventory
		// NOTE: NEVER use inventory itself, always use forge item capability
		// but since this is vanilla we get an Inventory class

		super(Reg.TEST_POWERGENERATOR_CONTAINER.get(), containerID);
		blockEntity = player.getCommandSenderWorld().getBlockEntity(blockPos);
		this.player = player;
		this.playerInventory = new InvWrapper(inv);  // this is the wrapper that turns the player inv into a forge capability

		if (blockEntity != null) {
			blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
				addSlot(new SlotItemHandler(handler, 0, 56, 47));
			});
		}
		layoutPlayerInventorySlots(8, 84);
		// container comms btwn server and client, on client
		// slots is how items is across, but we need energy too
		trackPower();
	}

	// setup syncing of power from server to client
	private void trackPower() {
		// DataSlot is a thing that supports sending over SHORTS (16b)
		// our power is 32b, and so we need two slots
		addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return getEnergy() & 0xffff;  // minor 16 bits
			}

			@Override
			public void set(int pValue) {
				blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
					int energyStored = handler.getEnergyStored() & 0xffff0000;
					((CustomEnergyStorage) handler).setEnergy(energyStored + (pValue & 0xffff));
				});
			}
		});
		addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return (getEnergy() >> 16) & 0xffff;  // major 16 bits
			}

			@Override
			public void set(int pValue) {
				blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
					int energyStored = handler.getEnergyStored() & 0x0000ffff;
					((CustomEnergyStorage) handler).setEnergy(energyStored | (pValue << 16));
				});
			}
		});
	}

	// client calls
	public int getEnergy() {
		return blockEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
	}

	// if player moves or block is broken whilst ui is broken
	// make sure inv will disappear
	@Override
	public boolean stillValid(Player pPlayer) {
		return stillValid(
				ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
				player,
				Reg.TEST_POWERGENERATOR.get()
		);
	}

	// shift click
	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		// need manual logic, as need to account for shifting from player into generator,
		// or from generator to player
		ItemStack itemBuffer = ItemStack.EMPTY;  // initialise
		Slot slot = this.slots.get(pIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			itemBuffer = stack.copy();
			if (pIndex == 0) {
				// our generator slot index is 0
				// player index is 1 to 37
				boolean ok = this.moveItemStackTo(stack, 1, 37, true);
				if (!ok) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(stack, itemBuffer);
			} else {
				// elsewhere, i.e. from inv to generator
				if (ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0) {
					// if burnable, to furnace
					boolean ok = this.moveItemStackTo(stack, 0, 1, false);
					if (!ok) {
						return ItemStack.EMPTY;
					}
				} else if (pIndex < 28) {
					// otherwise, move to hotbar if not
					boolean ok = this.moveItemStackTo(stack, 28, 37, false);
					if (!ok) return ItemStack.EMPTY;
				} else if (pIndex < 37 && !this.moveItemStackTo(stack, 1, 28, false)) {
					// and move from hotbar if so
					return ItemStack.EMPTY;
				}
			}

			if (stack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (stack.getCount() == itemBuffer.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(pPlayer, stack);
		}

		return itemBuffer;
	}

	// player inventory slot generation

	private void layoutPlayerInventorySlots(int left, int top) {
		// player inv
		addSlotBox(playerInventory, 9, left, top, 9, 18, 3, 18);
		// hotbar
		addSlotRange(playerInventory, 0, left, 142, 9, 18);
	}

	private int addSlotBox(IItemHandler itemHandler, int index, int x, int y, int horizSlots, int dx, int verSlots, int dy) {
		for (int i = 0; i < verSlots; i++) {
			index = addSlotRange(itemHandler, index, x, y, horizSlots, dx);
			y += dy;
			index++;
		}
		return index;
	}

	private int addSlotRange(IItemHandler itemHandler, int index, int x, int y, int amount, int dx) {
		for (int i = 0; i < amount; i++) {
			addSlot(new SlotItemHandler(itemHandler, index, x, y));
			x += dx;
			index++;
		}
		return index;
	}
}
