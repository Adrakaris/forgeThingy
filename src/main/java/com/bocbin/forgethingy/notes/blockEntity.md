# Creating a Block Entity

Creating a block entity is not fucking trivial, apparently. 

This will attempt to document the process for creating the power generator block entity. 

## Contents

1. [Capabilities](#Capabilities)
2. [Server and Client Side](#Server and Client Side)
3. [Block](#Block)
4. [BlockEntity](#BlockEntity)
5. [Container](#Container)
6. [Screen](#Screen)
7. [Data Generation](#Data Generation)

## Capabilities

For cross compatibility, forge has a framework called "capabilities", which lets mods cross-interact 
with each other, whether that be for item management, fluid management, or energy storage. 

These are like `IItemHandler` for inventories or `IEnergyStorage` for RF (*note: a custom energy storage*
*class is used to add more capability*). Other private fields are for things you don't need to be 
cross-compatible.

## Server and Client Side

Block entites (with GUIs) are made up of four main components: the block, the blockEntity, the
container, and the screen

Of these, the block provides the basis, the blockEntity does all the blockEntity logic,
the container is the **server-side** GUI processor, and the screen is the **client-side**
GUI that is rendered. The last of these should only appear client-side. 

## Block
### Main Parts

A tileentity block must implement `EntityBlock` as well as extend `Block`. It is registered
as usual. 

The constructor takes no parameters. Block properties can be defined here.

To add block states (to change textures when a machine is running versus when it is off)
it must override `Block.createBlockStateDefinition`, and also one should override 
`Block.getStateForPlacement` to set a known block state upon placement

Must override `EntityBlock.newBlockEntity` to return a new BlockEntity class. The
BlockEntity only takes the `BlockPos` and `BlockState` fields, since its super constructor
should internally pass its registration object.

If the block entity needs to be ticked server side (such as for furnace work), then
we should override `EntityBlock.getTicker`, which calls a `tick` (or other named function)
on the block entity class. This is done through a `BlockEntityTicker` functional interface,
and takes the form of a lambda:
```
(level, pos, state, blockEntity) -> if (blockEntity instanceof {blockentity} tile) { tile.tick() }
```

If the block entity has a right click interaction (such as a GUI), then `BlockBehaviour.use`
can be overridden. **Note:** `use` is called on both server and client side, and so we need
to check the side with `Level.isClientSide`. In the case of the generator, the use
interaction opens the GUI (on the server side, which will communicate over to client side
itself). We need to get the block-entity at the current block position, and then can create
a `MenuProvider`, a class that can return a new container. This is also communicated
to the client through the `NetworkHooks.openGui` function. A success message is returned
outside the if statement. This is where we create the title which is displayed on top
of the GUI

### Other Details

* `TranslatableComponent`s are used for names and tooltips and stuff.
* `Block.appendHoverText` can be overridden to add display text to the block, when hovered
    in an inventory.
* The "occlusion shape" (`getOcclusionShape`) should be slightly smaller than a full cube
    if the block is a model which is not a full block all the way round, as that can
    cause some render issues.
* Prefer to use vanilla predefined block states over custom ones. 


## BlockEntity

The block entity class contains the actual processing of the block entity. It extends `BlockEntity`.
It should be registed as a `BlockEntityType<>` in the registry, and the supplier is of the type
`BlockEntityType.Builder` since it needs a block entity and its parent block. 

Whenever anything is changed in a block entity, the method `setChanged()` must be called
whenever some variable changes that we want to save, else Minecraft will miss it. 

### Fields

Depending on the block entity's use, it may store constants and internal variables. These are
only server-side, but can be retrieved client-side with data slots, which will be covered later.

If a block entity is to handle items, fluid, energy, etc, they need to be created as Forge
capabilities, such as `IItemStackHandler` and `IEnergyStorage`. `IItemStackHandler` already
has an implementation, `ItemStackHandler`, whilst we have made a custom energy storage
handler for added functionality. 

These must be all then stored in `LazyOptional`s, and these are the ones which we interact
with in code. 

### Methods

The constructor needs only to take `BlockPos` and `BlockState`, as we get the `BlockEntityType`
from our registry, where we have registered the block entity class.

We must also override `getRemoved`, to remove the handlers when the block entity is
broken -- calling `super.setRemoved()` as well is good practice.

In `TestPowerGeneratorBE` the server tick method is called `tickServer`, though this
does not override anything and thus has no rules on naming. It is called on every tick. 
Forge energy is a push system, not a pull system. Thus, a generator block must
push energy to adjacent blocks, rather than adjacent blocks actively receiving
energy. 

To save and load the contents of the block, we must override the `load(CompoundTag)` and
`saveAdditional(CompoundTag)`, to save and load our NBT data. Importantly, all tags must be
upper case, ~~though I suspect this is because of the loot table in datagen~~. We use the
info tag in saving more data, because the loot table generation is generic and works off
info.

Getters (and setters!) are needed/wanted for other fields. 


## Container

A container is the inventory of the block entity. This is a **server-side** class, as the
container opens on both server and client side. The screen is the client-side actual
drawing class.

It is registered as a `MenuType`, which is a nested supplier: 
```
() -> IForgeMenuType.create((windowId, inv, data) -> new Container(windowId, data.readBlockPos(), inv, inv.player))
```

Note that the container not only manages the block's inventory, it also manages the
player's inventory too.

### Fields

The container should store a block entity variable, a player variable, and an item handler
variable for the inventory. This represents the inventory, the player interacting, and the
block being interacted with.
The block entity type can be a generic BlockEntity, but
I guess if we want special fields we need it to be of the correct type. 

### Methods

The constructor takes a containerID, a `BlockPos`, a vanilla `Inventory`, and a `Player`.
From the `BlockPos` we can get the tileEntity, which we should store. **Never use the vanilla**
**Inventory class directly, always use the forge IItemHandler capability** -- forge helpfully
provides `new Invwrapper(Inventory)` to do this. 

We then have to lay out the inventory slots, in the correct position according to the texture.
*Note:* each inventory slot is 16px square, with a 2px gap, and the correct top right corner
is the grey part inside the slot box. 

We have to use **data slots** to transfer data between client and server. Data slots send 
*16 bit shorts*, so we need two data slots for a 32 bit integer, plus some maths. See
`trackPower` for how it interacts with capabilities, and `trackIntField` for a generic
implementation with getters and setters.

We need to override `AbstractContainerMenu.stillValid` to make sure that the inventory
disappears when the block is broken.

We also need to override `quickMoveStack`, as this is the method to handle shift-clicks.
This unfortunately needs to be done manually.


## Screen

The screen is the **client-side** class that interacts with the container. 

It is not registered in the normal registry, rather it is registered in the `ClientSetup`,
with an enqueued command. The actual container class can be accessed via the internal
variable `menu` (this.menu)

### Fields

The screen is the class that stores the GUI texture, with a resource location. 

### Methods

The constructor takes a 'menu' (the container), an inventory, and a title (which is the
title to be displayed in the inventory).  

`render` is just... there. 

`renderBg` renders the background (blitting to posestack), which you tend to want to put
all your graphics on, since the UI background gets called first. Here you can call your
getters from the container and it will work if you set up the dataslots correctly.

`renderLabels` is for rendering foreground content, i.e. text, which is done after
most other things are on.


## Data Generation

Add the block to the correct block (and perhaps item) tags, such as `needs_iron_pickaxe`. 

Add all the localisation to the language provider. 

Since it's not a full block, just *casually manually make a block model in `BlockStates`*
*because clearly it's the best way*, I'm sure BlockBench or something has something for that?

Add a recipe in recipes. Use forge tags if possible. 

The block entity uses the default loot table, which is a loot table that tries to save the
`Items`, `Energy`, and `Info` tags on break. 

Run data.

(It's actually not much, but the block model creation was such a pisstake)
