package com.bocbin.forgethingy.setup;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.blocks.*;
import com.bocbin.forgethingy.entities.ThiefEntity;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.bocbin.forgethingy.ForgeThingy.MODID;

public class Reg {

	// define "deferred registries"
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
	private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
	private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	public static void init() {

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		// objects must be registerd in a registery before they can exist
		// happens before common setup

		BLOCKS.register(bus);
		ITEMS.register(bus);
		BLOCK_ENTITIES.register(bus);
		CONTAINERS.register(bus);
		ENTITIES.register(bus);
	}

	// defining common properties for blocks and items
	public static final Item.Properties ITEM_PROPS = new Item.Properties().tab(ModSetup.ITEM_GROUP);
	// strength refers to pickaxe level
	public static final BlockBehaviour.Properties ORE_PROPS = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();
	public static final BlockBehaviour.Properties METAL_PROPS = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).strength(2f).requiresCorrectToolForDrops().sound(SoundType.METAL);

	// defining blocks and items
	// only one Item/Block class exists, actual instances in the world are like ItemStack or BlockState,
	// which reference from the main block.

	//region blocks and blockitems
	public static final RegistryObject<Block> TEST_ORE = BLOCKS.register("test_ore", () -> new Block(ORE_PROPS));
	public static final RegistryObject<Item> TEST_ORE_ITEM = fromBlock(TEST_ORE);
	public static final RegistryObject<Block> TEST_ORE_DEEPSLATE = BLOCKS.register("test_ore_deepslate", () -> new Block(ORE_PROPS));
	public static final RegistryObject<Item> TEST_ORE_DEEPSLATE_ITEM = fromBlock(TEST_ORE_DEEPSLATE);
	public static final RegistryObject<Block> TEST_ORE_NETHER = BLOCKS.register("test_ore_nether", () -> new Block(ORE_PROPS));
	public static final RegistryObject<Item> TEST_ORE_NETHER_ITEM = fromBlock(TEST_ORE_NETHER);
	public static final RegistryObject<Block> TEST_ORE_END = BLOCKS.register("test_ore_end", () -> new Block(ORE_PROPS));
	public static final RegistryObject<Item> TEST_ORE_END_ITEM = fromBlock(TEST_ORE_END);
	public static final RegistryObject<Block> TEST_INGOT_BLOCK = BLOCKS.register("test_ingot_block", () -> new Block(METAL_PROPS));
	public static final RegistryObject<Item> TEST_INGOT_BLOCK_ITEM = fromBlock(TEST_INGOT_BLOCK);

	public static final RegistryObject<Block> TEST_POWERGENERATOR = BLOCKS.register("test_power_generator", TestPowerGenerator::new);
	public static final RegistryObject<Item> TEST_POWERGENERATOR_ITEM = fromBlock(TEST_POWERGENERATOR);
	public static final RegistryObject<BlockEntityType<TestPowerGeneratorBE>> TEST_POWERGENERATOR_BE = BLOCK_ENTITIES.register(
			"test_power_generator",
			() -> BlockEntityType.Builder.of(TestPowerGeneratorBE::new, TEST_POWERGENERATOR.get()).build(null)
	);
	public static final RegistryObject<MenuType<TestPowerGeneratorContainer>> TEST_POWERGENERATOR_CONTAINER = CONTAINERS.register(
			"test_power_generator",
			// data is a packet that comes from server
			() -> IForgeMenuType.create((windowId, inv, data) -> new TestPowerGeneratorContainer(windowId, data.readBlockPos(), inv, inv.player))
	);

	// new "generator" block. It does not have a GUI, so we don't need a container class
	public static final RegistryObject<Block> ORE_GENERATOR = BLOCKS.register("ore_generator", OreGenerator::new);
	public static final RegistryObject<Item> ORE_GENERATOR_ITEM = fromBlock(ORE_GENERATOR);
	public static final RegistryObject<BlockEntityType<OreGeneratorBE>> ORE_GENERATOR_BE = BLOCK_ENTITIES.register(
			"ore_generator",
			() -> BlockEntityType.Builder.of(OreGeneratorBE::new, ORE_GENERATOR.get()).build(null)
	);
	//endregion

	//region items
	public static final RegistryObject<Item> RAW_TEST_ORE = ITEMS.register("raw_test_ore", () -> new Item(ITEM_PROPS));
	public static final RegistryObject<Item> TEST_INGOT = ITEMS.register("test_ingot", () -> new Item(ITEM_PROPS));
	//endregion

	//region custom item tags
	public static final TagKey<Block> TAG_TEST_ORE = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(MODID, "test_ore"));
	public static final TagKey<Item> TAG_TEST_ORE_ITEM = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MODID, "test_ore"));
	//endregion

	//region entities
	public static final RegistryObject<EntityType<ThiefEntity>> THIEF = ENTITIES.register(
			"thief", () -> EntityType.Builder.of(ThiefEntity::new, MobCategory.CREATURE)
					.sized(0.6f, 1.95f)
					.clientTrackingRange(8)
					.setShouldReceiveVelocityUpdates(false)  // 不知其用
					.build("thief"));
	public static final RegistryObject<Item> THIEF_SPAWN_EGG = ITEMS.register(
			"thief", () -> new ForgeSpawnEggItem(THIEF, 0xff0000, 0x00ff00, ITEM_PROPS)
	);
	//endregion

	// to get a BlockItem from a block (i.e. register an item for a block)
	// aiye java generics
	public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
		return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPS));
	}
}
