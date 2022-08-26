package com.bocbin.forgethingy.setup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

public class ModSetup {

	public static final String TAB_NAME = "forgethingy";

	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
		@Override
		public @NotNull ItemStack makeIcon() {
			return new ItemStack(Registration.TEST_ORE_ITEM.get());
		}
	};

	public static void init(FMLCommonSetupEvent event) {
	}
}
