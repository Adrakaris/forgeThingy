package com.bocbin.forgethingy.blocks;

import com.bocbin.forgethingy.setup.Reg;
import com.bocbin.forgethingy.utils.CustomEnergyStorage;
import com.bocbin.forgethingy.utils.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class OreGeneratorBE extends BlockEntity {

	/*
	Models:
	We have so far made regular simple block models,
	and more complex but non-directional block models,
	and they are all STATIC

	However this generator changes in game, and so we want the model to be in Java.
	That's why we have to use "Baked Models" instead of JSON
	Still a static model - once generated, remains generated, not rendered dynamic.
	The special effect though in the ore generator is dynamic.
	 */

	// supporting baked model
	// need way to communicate states of BE to the model:
	// have model properties
	public static final ModelProperty<BlockState> TARGET_BLOCK = new ModelProperty<>();  // target block
	public static final ModelProperty<Boolean> GENERATING = new ModelProperty<>();  // whether generating mode is on
	public static final ModelProperty<Boolean> COLLECTING = new ModelProperty<>();
	public static final ModelProperty<Boolean> WORKING = new ModelProperty<>();

	public static final int INPUT_SLOTS = 5;
	public static final int OUTPUT_SLOTS = 1;

	// actual relevant changing fields
	private boolean generating = false;  // mode
	private boolean collecting = false;
	private  BlockState generatingBlock;
	private boolean working = false;  // doing

	// collecting
	private int collectingTicker = 0;
	// AABB is "axis aligned bounding box
	// we need this because we throw items at the box to insert them
	private AABB collectingBox = null;

	// for generation
	private int genCounter = 0;

	// very handlery
	// three item handlers:
	// (1) extract from bottom (2) insert everywhere else (3) the prior two combined
	private final ItemStackHandler inputItems = createInputItemHandler();
	private final LazyOptional<IItemHandler> inputItemsHandler = LazyOptional.of(() -> inputItems);
	private final ItemStackHandler outputItems = createOutoutItemHander();
	private final LazyOptional<IItemHandler> outputItemsHander = LazyOptional.of(() -> outputItems);
	private final LazyOptional<IItemHandler> combinedHandler = LazyOptional.of(this::createCombinedHandler);

	// energy handler
	private final CustomEnergyStorage energy = createEnergy();
	private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);

	public OreGeneratorBE(BlockPos pPos, BlockState pBlockState) {



		super(Reg.ORE_GENERATOR_BE.get(), pPos, pBlockState);
	}

	public void tickServer() {
		if (collecting) {
			collectingTicker--;  // don't want to collect items every tick - delay of 10 ticks
			if (collectingTicker <= 0) {
				collectingTicker = OreGeneratorConfig.COLLECT_DELAY.get();
				collectItems();
			}
		}

		boolean isWorking = false;
		if (generating) {
			isWorking = generateOres();
		}
		if (isWorking != working) {
			working = isWorking;
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
		}

	}

	private void collectItems() {
		// AABB is "axis aligned bounding box
		// we need this because we throw items at the block to insert them
		if (collectingBox == null) {
			collectingBox = new AABB(getBlockPos()).inflate(0.5);
			// inflate - grow by 0.5 radius on all sides
		}
		// items on ground are ItemEntities
		List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, collectingBox,
				itemEntity -> {
			ItemStack itemStack = itemEntity.getItem();
			return itemStack.is(Tags.Items.INGOTS);  // we only want to get items which are ingots
				});

		for (ItemEntity itemEntity : entities) {
			ItemStack item = itemEntity.getItem();
			// generic insert item function, does not take a slot, just inserts into first available one
			ItemStack remainder = ItemHandlerHelper.insertItem(inputItems, item, false);
			if (remainder.isEmpty()) {
				itemEntity.kill();
			} else {
				itemEntity.setItem(remainder);
			}
		}
	}

	private boolean generateOres() {
		// no ore selected
		if (generatingBlock == null) {
			return false;
		}
		// not enough energy
		if (energy.getEnergyStored() < OreGeneratorConfig.ENERGY_USE.get()) {
			return false;
		}

		boolean isWorking = false;
		// for every slot in the input
		for (int i = 0; i < inputItems.getSlots(); i++) {
			ItemStack item = inputItems.getStackInSlot(i);
			if (!item.isEmpty()) {
				energy.removeEnergy(OreGeneratorConfig.ENERGY_USE.get());

				item = item.copy();  // note: must make a copy as it is illegal to modify the stack in place
				// see IItemHandler documentation
				item.shrink(1);
				inputItems.setStackInSlot(i, item);

				genCounter++;  // to track how many ingots consumed
				isWorking = true;
				if (genCounter >= OreGeneratorConfig.INGOTS_PER_ORE.get()) {
					genCounter = 0;
					ItemStack remaining = ItemHandlerHelper.insertItem(outputItems,
							new ItemStack(generatingBlock.getBlock().asItem()),
							false);
					Tools.spawnInWorld(level, worldPosition, remaining);;  // no room in slot, chuck it onto ground
				}
			}
		}
		return isWorking;
	}

	// spawning item in world
	// superseded by method in Tools
//	private void spawnInWorld(ItemStack remaining) {
//		if (!remaining.isEmpty()) {
//			ItemEntity itemEntity = new ItemEntity(level,
//					worldPosition.getX(),
//					worldPosition.getY()+0.5,
//					worldPosition.getZ(), remaining);
//			itemEntity.setPickUpDelay(30);
//			itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0,1,0));
//			level.addFreshEntity(itemEntity);
//		}
//	}

	//region getters setters
	// called server side
	public boolean isGenerating() {
		return generating;
	}

	public void setGenerating(boolean generating) {
		this.generating = generating;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	public boolean isCollecting() {
		return collecting;
	}

	public void setCollecting(boolean collecting) {
		this.collecting = collecting;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	public void setGeneratingBlock(BlockState generatingBlock) {
		// only accept ores
		if (generatingBlock.is(Tags.Blocks.ORES)){
			this.generatingBlock = generatingBlock;
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
		}

	}

	//endregion

	// getting handlers
	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		// return capability based on side
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == null) {  // representing the entire block
  				return combinedHandler.cast(); // API rules
			} else if (side == Direction.DOWN) {
				return outputItemsHander.cast();
			} else {
				return inputItemsHandler.cast();
			}
		} else if (cap == CapabilityEnergy.ENERGY) {
			return energyHandler.cast();
		} else {
			return super.getCapability(cap, side);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		inputItemsHandler.invalidate();
		outputItemsHander.invalidate();
		combinedHandler.invalidate();
		energyHandler.invalidate();
	}

	//region saving and loading additional tags, with client tag handling

	// to load the correct model when the chunk is loaded
	// since the client doesnt automatically get data, and there are a few data that we do need

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		pTag.put("Inventory", inputItems.serializeNBT());
		pTag.put("Energy", energy.serializeNBT());
		saveClientData(pTag);  // need to put info tag  creation in saveclientdata
		// for functionality reasons
		CompoundTag extraInfo = pTag.getCompound("Info");
		extraInfo.putInt("Generating", genCounter);
	}

	// separate method for what we need to load for client
	// then we can do tag handling
	private void saveClientData(CompoundTag tag) {
		CompoundTag extraInfo = new CompoundTag();
		// split into separate method
		extraInfo.putBoolean("generating", generating);
		extraInfo.putBoolean("collecting", collecting);
		extraInfo.putBoolean("working", working);
		if (generatingBlock != null) {
			extraInfo.put("block", NbtUtils.writeBlockState(generatingBlock));
		}


		tag.put("Info", extraInfo);
	}

	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		if (pTag.contains("Inventory")) inputItems.deserializeNBT(pTag.getCompound("Inventory"));
		if (pTag.contains("Energy")) energy.deserializeNBT(pTag.get("Energy"));  // Energy is a Get and NOT a compound
		if (pTag.contains("Info")) {
			loadClientData(pTag);
			genCounter = pTag.getCompound("Info").getInt("Generating");
		}
	}

	private void loadClientData(CompoundTag tag) {
		CompoundTag info = tag.getCompound("Info");
		generating = info.getBoolean("generating");
		collecting = info.getBoolean("collecting");
		working = info.getBoolean("working");
		if (info.contains("block")) {
			generatingBlock = NbtUtils.readBlockState(info.getCompound("block"));
		}
	}

	//endregion

	//region handling tags for client side data which is crucial to operation

	// 4 very important methods

	// important 1 and 2: getUpdateTag()/handleUpdateTag() pair are called whenever client
	//  receives a new chunk it has not received before -- i.e. when first loaded.

	@Override
	public CompoundTag getUpdateTag() {
		// called server side
		CompoundTag tag = super.getUpdateTag();  // create a tag for all the data that may
		// be useful to pass to a client
		saveClientData(tag);  // and then we need to save stuff onto it
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		// called client side
		if (tag != null) {
			loadClientData(tag);
		}
	}

	// important 3 and 4: getUpdatePacket()/onDataPacket() are called when a block update happens
	// 	on the client (either a block state change, or a notification from server). Best to implement
	// 	around getUpdateTag() and handleUpdateTag();

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		// called server side
		return ClientboundBlockEntityDataPacket.create(this);
		// CBEDP.create is just a wrapper for `create(pBlockEntity, BlockEntity::getUpdateTag);`
		// i.e. autocalls getUpdateTag
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		// called client side
		// compare and update values
		boolean oldGenerating = generating;
		boolean oldCollecting = collecting;
		boolean oldWorking = working;
		BlockState oldGenBlock = generatingBlock;
		// handle tag
		CompoundTag tag = pkt.getTag();
		handleUpdateTag(tag);
		if (oldGenerating != generating || oldCollecting != collecting
				|| oldWorking != working || !Objects.equals(generatingBlock, oldGenBlock)) {
			// i.e. state changed on server, request model to update in acoordance
			ModelDataManager.requestModelDataRefresh(this);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
		}
		//29:17
	}

	//endregion

	@NotNull
	@Override
	public IModelData getModelData() {
		// used by baked model to get model data with four properties
		// since baked model may be created in a different thread, we shouldn't access the
		// block entity directly to avoid multithreading race conditions
		return new ModelDataMap.Builder()
				.withInitial(TARGET_BLOCK, generatingBlock)
				.withInitial(GENERATING, generating)
				.withInitial(WORKING, working)
				.withInitial(COLLECTING, collecting)
				.build();
	}


	//region helper methods to create handlers

	@Nonnull
	private ItemStackHandler createInputItemHandler() {
		return new ItemStackHandler(INPUT_SLOTS) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
			}

			@NotNull
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return ItemStack.EMPTY;
			}
		};
	}

	@Nonnull
	private ItemStackHandler createOutoutItemHander() {
		return new ItemStackHandler(OUTPUT_SLOTS) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
			}
		};
	}

	// combined handler doesn't need to be accessed, only there to be exposed
	// because API rules say there must be a whole block handler
	private IItemHandler createCombinedHandler() {
		return new CombinedInvWrapper(inputItems, outputItems) {
			// cannot extract or insert
			@NotNull
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return ItemStack.EMPTY;
			}

			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				return stack;
			}
		};
	}

	public CustomEnergyStorage createEnergy() {
		return new CustomEnergyStorage(OreGeneratorConfig.ENERGY_CAP.get(), OreGeneratorConfig.ENERGY_RECEIVE.get()) {
			@Override
			protected void onEnergyChanged() {
				setChanged();
			}
		};
	}

	//endregion
}
