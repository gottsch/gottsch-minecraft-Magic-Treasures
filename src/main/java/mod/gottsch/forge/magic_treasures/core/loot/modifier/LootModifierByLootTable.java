
package mod.gottsch.forge.magic_treasures.core.loot.modifier;

import com.google.gson.JsonObject;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.gottschcore.random.RandomHelper;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.forge.magic_treasures.core.config.Config;
import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.forge.magic_treasures.core.item.SpellScroll;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.forge.magic_treasures.core.registry.JewelryRegistry;
import mod.gottsch.forge.magic_treasures.core.registry.StoneRegistry;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Mark Gottschling May 15, 2024
 *
 */
public class LootModifierByLootTable extends LootModifier {

	// the number of items to add
	private final int count;
	private final IRarity rarity;
	private final double chance;
	private ResourceLocation lootTable;

	protected LootModifierByLootTable(LootItemCondition[] conditionsIn, int count, IRarity rarity, double chance, ResourceLocation lootTable) {
		super(conditionsIn);
		this.count = count;
		this.rarity = rarity;
		this.chance = chance;
		this.lootTable = lootTable;
	}

	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		MagicTreasures.LOGGER.debug("executing LootModifierByLootTable");

		// if chance was left blank or null, then set to 100% by default
		double localChance = chance == 0.0 ? 1.0 : chance;

		// determine if specific loot modifier is enabled
		boolean isEnabled = Optional.ofNullable(Config.enableLootModifiers.get(lootTable.toString().toLowerCase())).
				map(ForgeConfigSpec.ConfigValue::get).orElse(false);
		MagicTreasures.LOGGER.debug("isEnabled for {} -> {}", lootTable, isEnabled);

		if (Config.SERVER.loot.enableVanillaLootModifiers.get()
				&& isEnabled
				&& RandomHelper.checkProbability(context.getLevel().getRandom(), localChance * 100)) {

			// get the loot table
			LootTable table = context.getLevel().getServer().getLootTables().get(lootTable);

			List<ItemStack> tempLoot = table.getRandomItems(context);
			// grab the loot from the loot list
			for (int index = 0; index < Math.min(count, tempLoot.size()); index++) {
				ItemStack outputStack = tempLoot.get(index);
				generatedLoot.add(outputStack);
			}
		}
		return generatedLoot;
	}

	/*
	 * 
	 */
	public static class Serializer extends GlobalLootModifierSerializer<LootModifierByLootTable> {

		@Override
		public LootModifierByLootTable read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
			int count = GsonHelper.getAsInt(object, "count");
			String rarityStr = GsonHelper.getAsString(object, "rarity");
			IRarity rarity = MagicTreasuresApi.getRarity(rarityStr).orElse(MagicTreasuresRarity.COMMON);
			double chance = GsonHelper.getAsDouble(object, "chance");
			String lootTableStr = GsonHelper.getAsString(object, "lootTable");
			
			return new LootModifierByLootTable(conditions, count, rarity, chance,
					ModUtil.asLocation(lootTableStr));
		}

		@Override
		public JsonObject write(LootModifierByLootTable instance) {
			JsonObject json = makeConditions(instance.conditions);
			json.addProperty("count", Integer.valueOf(instance.count));
			json.addProperty("rarity", instance.rarity.getName());
			json.addProperty("chance", Double.valueOf(instance.chance));
			json.addProperty("lootTable", instance.lootTable.toString());
			return json;
		}

	}
}
