package com.bocbin.forgethingy.setup;

import com.bocbin.forgethingy.ForgeThingy;
import com.bocbin.forgethingy.entities.ThiefEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = ForgeThingy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

	public static final String TAB_NAME = "forgethingy";

	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
		@Override
		public @NotNull ItemStack makeIcon() {
			return new ItemStack(Reg.TEST_ORE_ITEM.get());
		}
	};

	public static void init(FMLCommonSetupEvent event) {
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(Reg.THIEF.get(), ThiefEntity.prepareAttributes().build());
	}
}
