package com.bocbin.forgethingy.worldgen.ores;

import com.bocbin.forgethingy.setup.Reg;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class TestOreGen {

	public static final int OVERWORLD_VEINSIZE = 5;
	public static final int OVERWORLD_AMOUNT = 3;
	public static final int DEEPSLATE_VEINSIZE = 6;
	public static final int DEEPSLATE_AMOUNT = 3;
	public static final int NETHER_VEINSIZE = 5;
	public static final int NETHER_AMOUNT = 3;
	public static final int END_VEINSIZE = 10;
	public static final int END_AMOUNT = 4;

	public static final RuleTest IN_ENDSTONE = new TagMatchTest(Tags.Blocks.END_STONES);

	public static Holder<PlacedFeature> OVERWORLD_GEN;
	public static Holder<PlacedFeature> DEEPSLATE_GEN;
	public static Holder<PlacedFeature> NETHER_GEN;
	public static Holder<PlacedFeature> END_GEN;

	public static void registerConfiguredFeatures() {
		// worldgen (oregen) is a feature
		// when a chunk is generated, things happen with shaping, carvers, features (like ores), structures (like villages)
		// and so we need to register our features for oregen
		// luckily ore is a preexisting feature

		// create a new ore configuration, so we can juse the preexisting ORE feature
		OreConfiguration overworldConfig = new OreConfiguration(
				OreFeatures.STONE_ORE_REPLACEABLES,  // ruletest -- where the ore will generate
				Reg.TEST_ORE.get().defaultBlockState(),  // block
				OVERWORLD_VEINSIZE  // size
				);
		OVERWORLD_GEN = registerPlacedFeature(
				"test_ore",
				new ConfiguredFeature<>(Feature.ORE, overworldConfig),  // placement modifiers have types - can only use each type once probably
				CountPlacement.of(OVERWORLD_AMOUNT),  // number of tries
				InSquarePlacement.spread(),  // spread generation around
				BiomeFilter.biome(),  // need - checks if biome supports feature
				HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(40))  // where to generate
		);

		OreConfiguration deepslateConfig = new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Reg.TEST_ORE_DEEPSLATE.get().defaultBlockState(), DEEPSLATE_VEINSIZE);
		DEEPSLATE_GEN = registerPlacedFeature(
				"test_ore_deepslate",
				new ConfiguredFeature<>(Feature.ORE, deepslateConfig),
				CountPlacement.of(DEEPSLATE_AMOUNT),
				InSquarePlacement.spread(),
				BiomeFilter.biome(),
				HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.aboveBottom(64))
		);

		OreConfiguration netherConfig = new OreConfiguration(OreFeatures.NETHER_ORE_REPLACEABLES, Reg.TEST_ORE_NETHER.get().defaultBlockState(), NETHER_VEINSIZE);
		NETHER_GEN = registerPlacedFeature("test_ore_nether",
				new ConfiguredFeature<>(Feature.ORE, netherConfig),
				CountPlacement.of(NETHER_AMOUNT),
				InSquarePlacement.spread(),
				BiomeFilter.biome(),
				HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(98)));

		OreConfiguration endConfig = new OreConfiguration(IN_ENDSTONE, Reg.TEST_ORE_END.get().defaultBlockState(), END_VEINSIZE);
		END_GEN = registerPlacedFeature("test_ore_end",
				new ConfiguredFeature<>(Feature.ORE, endConfig),
				CountPlacement.of(END_AMOUNT),
				InSquarePlacement.spread(),
				BiomeFilter.biome(),
				HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(100)));
	}

	private static <C extends FeatureConfiguration, F extends Feature<C>> Holder<PlacedFeature> registerPlacedFeature(
			String regName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers
	) {
		// forge registry entites have to be registered in a deferred registry - specific to forge
		// but configured features are not forge registry, they are vanilla feature bus
		// luckily there is (somehow) a placement utils which does this for us
		return PlacementUtils.register(regName, Holder.direct(feature), placementModifiers);
	}

	// important: need this event listener so that they can be generated
	public static void onBiomeLoadingEvent(BiomeLoadingEvent event) {
		if (event.getCategory() == Biome.BiomeCategory.NETHER) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NETHER_GEN);
		} else if (event.getCategory() == Biome.BiomeCategory.THEEND) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, END_GEN);
		} else {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OVERWORLD_GEN);
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_GEN);
		}
	}

}
