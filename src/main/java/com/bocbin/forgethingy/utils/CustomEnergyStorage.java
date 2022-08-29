package com.bocbin.forgethingy.utils;

import net.minecraftforge.energy.EnergyStorage;

/**
 * Energy storage class created by McJty (and then just casually copy pasted over with no explanantion, thanks)
 * Apparently it has a few more options
 */
public class CustomEnergyStorage extends EnergyStorage {

	public CustomEnergyStorage(int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
	}

	// Override this to (for example) call setChanged() on your block entity
	protected void onEnergyChanged() {
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		// 判断: simulate tells the block to run the tick, turned off after done
		int received = super.receiveEnergy(maxReceive, simulate);
		if (received > 0 && !simulate) {
			onEnergyChanged();
		}
		return received;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int extracted = super.extractEnergy(maxExtract, simulate);
		if (extracted > 0 && !simulate) {
			onEnergyChanged();
		}
		return extracted;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
		onEnergyChanged();
	}

	public void addEnergy(int energy) {
		this.energy += energy;
		if (this.energy > getMaxEnergyStored()) {
			this.energy = getMaxEnergyStored();
		}
		onEnergyChanged();
	}

	public void removeEnergy(int energy) {
		this.energy -= energy;
		if (this.energy < 0) {
			this.energy = 0;
		}
		onEnergyChanged();
	}
}
