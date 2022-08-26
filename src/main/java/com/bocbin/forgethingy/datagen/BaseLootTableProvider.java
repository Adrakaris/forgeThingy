package com.bocbin.forgethingy.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseLootTableProvider extends LootTableProvider {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	protected final Map<Block, LootTable.Builder> lootTables = new HashMap<>();
	private final DataGenerator generator;

	// auto generated constructor: p_... is the generator
	public BaseLootTableProvider(DataGenerator p_124437_) {
		super(p_124437_);
		this.generator = p_124437_;
	}

	protected abstract void addTables();

	/** Creates a standard loot table for a block entity to keep its internal contents */
	protected LootTable.Builder createStandardTable(String name, Block block, BlockEntityType<?> type) {
		// ??
		LootPool.Builder builder = LootPool.lootPool()
				.name(name)
				.setRolls(ConstantValue.exactly(1))
				// right this is a preserve inventory thing
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
								.copy("Info", "BlockEntityTag.Info", CopyNbtFunction.MergeStrategy.REPLACE)
								.copy("Inventory", "BlockEntityTag.Inventory", CopyNbtFunction.MergeStrategy.REPLACE)
								.copy("Energy", "BlockEntityTag.Energy", CopyNbtFunction.MergeStrategy.REPLACE))
						.apply(SetContainerContents.setContents(type)
								.withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))))
				);
		return LootTable.lootTable().withPool(builder);
	}

	/** Lets a block drop itself */
	protected LootTable.Builder createSimpleTable(String name, Block block) {
		LootPool.Builder builder = LootPool.lootPool()
				.name(name)
				.setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block));
		return LootTable.lootTable().withPool(builder);
	}

	/**
	 * Supports both fortune and silk touch, for things like ores
	 *
	 * NOTE: even if you set min,max to one, the fortune roll seems to still be applied
	 *
	 * @param name maintain as name of mined block
	 * @param block the base block to drop on silk touch
	 * @param lootItem the item to drop
	 * @param min base min drops
	 * @param max base max drops
	 * @param fortuneProb probability per roll of a level of fortune giving an extra drop - 1 is indentical to minecraft
	 *                    behaviour for, say, redstone
	 * @param extraRolls Extra rolls on top of the fortune level
	 * @return loot table builder
	 */
	protected LootTable.Builder createSilkTouchTable(String name, Block block, Item lootItem, float min, float max, float fortuneProb, int extraRolls) {
		LootPool.Builder builder = LootPool.lootPool()
				.name(name)
				.setRolls(ConstantValue.exactly(1))  // roll table once
				.add(AlternativesEntry.alternatives(
						LootItem.lootTableItem(block)
								.when(MatchTool.toolMatches(ItemPredicate.Builder.item()
										.hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))),

						LootItem.lootTableItem(lootItem)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
//								.apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
								.apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, fortuneProb, extraRolls))  // probability, rounds to roll
								.apply(ApplyExplosionDecay.explosionDecay())  // ?? ask mcJty
				));

		return LootTable.lootTable().withPool(builder);
	}

	// custom table for ore drops
	protected LootTable.Builder createOreDropTable(String name, Block block, Item lootItem) {
		LootPool.Builder builder = LootPool.lootPool()
				.name(name)
				.setRolls(ConstantValue.exactly(1))  // roll table once
				.add(AlternativesEntry.alternatives(
						LootItem.lootTableItem(block)
								.when(MatchTool.toolMatches(ItemPredicate.Builder.item()
										.hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))),

						LootItem.lootTableItem(lootItem)
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
								.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
								// default ore bonus count - randomly 1x to 4x (1+3 for 3 fortune levels) of the setCounted value
								// thus for normal behaviour the setCount can be left out and it'll default to 1.
								.apply(ApplyExplosionDecay.explosionDecay())
				));

		return LootTable.lootTable().withPool(builder);
	}

	@Override
	public void run(HashCache cache) {
		addTables();

		Map<ResourceLocation, LootTable> tables = new HashMap<>();
		for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
			tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK).build());
		}

		writeTables(cache, tables);
	}

	private void writeTables(HashCache cache, Map<ResourceLocation, LootTable> tables) {
		Path outputFolder = this.generator.getOutputFolder();
		tables.forEach((key, lootTable) -> {
			Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
			try {
				DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
			} catch (IOException e) {
				LOGGER.error("Couldn't write loot table {}", path, e);
			}
		});
	}

	@Override
	public String getName() {
		return "Forge Thingy LootTables";
	}
}
