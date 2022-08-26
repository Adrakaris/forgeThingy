package com.bocbin.forgethingy.setup;

import com.bocbin.forgethingy.ForgeThingy;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {

	// define "deferred registries"
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ForgeThingy.MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ForgeThingy.MODID);

	public static void init() {

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		// objects must be registerd in a registery before they can exist
		// happens before common setup

		BLOCKS.register(bus);
		ITEMS.register(bus);
	}

	// defining common properties for blocks and items
	public static final Item.Properties ITEM_PROPS = new Item.Properties().tab(ModSetup.ITEM_GROUP);
	// strength refers to pickaxe level
	public static final BlockBehaviour.Properties ORE_PROPS = BlockBehaviour.Properties.of(Material.STONE).strength(2f);

	// defining blocks and items
	// only one Item/Block class exists, actual instances in the world are like ItemStack or BlockState,
	// which reference from the main block.

	public static final RegistryObject<Block> TEST_ORE = BLOCKS.register("test_ore", () -> new Block(ORE_PROPS));
	public static final RegistryObject<Item> TEST_ORE_ITEM = fromBlock(TEST_ORE);
	public static final RegistryObject<Block> TEST_ORE_DEEPSLATE = BLOCKS.register("test_ore_deepslate", () -> new Block(ORE_PROPS));
	public static final RegistryObject<Item> TEST_ORE_DEEPSLATE_ITEM = fromBlock(TEST_ORE_DEEPSLATE);
	public static final RegistryObject<Block> TEST_ORE_NETHER = BLOCKS.register("test_ore_nether", () -> new Block(ORE_PROPS));
	public static final RegistryObject<Item> TEST_ORE_NETHER_ITEM = fromBlock(TEST_ORE_NETHER);
	public static final RegistryObject<Block> TEST_ORE_END = BLOCKS.register("test_ore_end", () -> new Block(ORE_PROPS));
	public static final RegistryObject<Item> TEST_ORE_END_ITEM = fromBlock(TEST_ORE_END);

	// to get a BlockItem from a block (i.e. register an item for a block)
	// aiye java generics
	public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
		return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPS));
	}
}
