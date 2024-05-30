
package mod.gottsch.forge.magic_things.core.loot.function;

import com.google.gson.*;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.magic_things.MagicThings;
import mod.gottsch.forge.magic_things.api.MagicThingsApi;
import mod.gottsch.forge.magic_things.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_things.core.capability.MagicThingsCapabilities;
import mod.gottsch.forge.magic_things.core.item.MagicThingsItems;
import mod.gottsch.forge.magic_things.core.loot.MagicThingsLootFunctions;
import mod.gottsch.forge.magic_things.core.rarity.MagicThingsRarity;
import mod.gottsch.forge.magic_things.core.spell.ISpell;
import mod.gottsch.forge.magic_things.core.spell.MagicThingsSpells;
import mod.gottsch.forge.magic_things.core.spell.SpellEntity;
import mod.gottsch.forge.magic_things.core.spell.SpellRegistry;
import mod.gottsch.forge.magic_things.core.util.ModUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 *
 * @author Mark Gottschling on May 23, 2024
 *
 */
public class ImbueRandomly extends LootItemConditionalFunction {
//	private static final ResourceLocation LOCATION = new ResourceLocation("gealdorcraft:random_spell");
	private static final String LEVELS = "levels";
	private static final String RARITY = "rarity";
	private static final String RARITIES = "rarities";

	private final NumberProvider levels;
	private final IRarity rarity;
	private final List<IRarity> rarities;

	/**
	 *
	 * @param conditions
	 * @param levels
	 * @param rarity
	 */
	public ImbueRandomly(LootItemCondition[] conditions, NumberProvider levels, IRarity rarity, List<IRarity> rarities) {
		super(conditions);
		this.levels = levels;
		this.rarity = rarity;
		this.rarities = rarities != null ? rarities : new ArrayList<>();
	}

	@Override
	public LootItemFunctionType getType() {
		return MagicThingsLootFunctions.IMBUE_RANDOMLY;
	}

	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		Random rand = new Random();
		MagicThings.LOGGER.debug("incoming stack -> {}", stack.getDisplayName());
		List<ISpell> spells = new ArrayList<>();
		ISpell spell;

		MagicThings.LOGGER.debug("rarity -> {}", rarity);

		int stackMaxLevel = stack.getCapability(MagicThingsCapabilities.JEWELRY_CAPABILITY).map(IJewelryHandler::getMaxLevel).orElse(0);

		spell = MagicThingsSpells.DEFAULT_HEALING;
		if (rarity != null && rarity != MagicThingsRarity.NONE) {
			// get all the spells by rarity
			spells.addAll(SpellRegistry.get(rarity));

		} else if (!rarities.isEmpty()) {
			for (IRarity rarity : rarities) {
				spells.addAll(SpellRegistry.get(rarity));
			}
		} else {
			// select random level
			int level = this.levels == null ? 1 : this.levels.getInt(context);
			MagicThings.LOGGER.debug("selected level -> {}", level);

			// select a spell by level
			spells.addAll(SpellRegistry.get(level));
		}

		// filter spells to those <= stackMaxLevel
		spells = spells.stream().filter(s -> s.getLevel() <= stackMaxLevel).toList();

		// default
		if (spells.isEmpty()) {
			spells = SpellRegistry.get(stackMaxLevel);
		}

		if (!spells.isEmpty()) {
			spell = spells.get(new Random().nextInt(spells.size()));
		}
		MagicThings.LOGGER.debug("selected spell -> {}", spell.getName());

		SpellEntity spellEntity = spell.entity();
		stack.getCapability(MagicThingsCapabilities.JEWELRY_CAPABILITY).ifPresent(handler -> {
			handler.getSpells().add(spellEntity);
		});

		return stack;
	}

	/*
	 *
	 */
	public static class Serializer extends LootItemConditionalFunction.Serializer<ImbueRandomly> {
		public void serialize(JsonObject json, ImbueRandomly function, JsonSerializationContext context) {
			super.serialize(json, function, context);
			json.add(LEVELS, context.serialize(function.levels));
			json.addProperty(RARITY, function.rarity.getName());
			if (!function.rarities.isEmpty()) {
				final JsonArray jsonArray = new JsonArray();
				function.rarities.forEach(r -> {
					jsonArray.add(new JsonPrimitive(r.getName().toString()));
				});
				json.add(RARITIES, jsonArray);
			}
		}

		public ImbueRandomly deserialize(JsonObject jsonObject, JsonDeserializationContext context, LootItemCondition[] conditions) {
			NumberProvider levels = null;
			if (jsonObject.has(LEVELS)) {
				levels = GsonHelper.getAsObject(jsonObject, LEVELS, context, NumberProvider.class);
			}
			String rarityStr = "";
			if(jsonObject.has(RARITY)) {
				rarityStr = GsonHelper.getAsString(jsonObject, RARITY);
            }
			IRarity rarity = MagicThingsApi.getRarity(rarityStr).orElse(MagicThingsRarity.NONE);

			List<IRarity> rarities = new ArrayList<>();
			if (jsonObject.has(RARITIES)) {
				GsonHelper.getAsJsonArray(jsonObject, RARITIES).forEach(element -> {
					String rarityName = GsonHelper.convertToString(element, RARITY);
					IRarity r = MagicThingsApi.getRarity(rarityName).orElse(MagicThingsRarity.NONE);
					if (r != MagicThingsRarity.NONE) {
						rarities.add(r);
					}
                });
            }
			return new ImbueRandomly(conditions, levels, rarity, rarities);
		}
	}
}