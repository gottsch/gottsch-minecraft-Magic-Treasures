
package mod.gottsch.forge.magic_treasures.core.registry;

import com.google.common.collect.Maps;
import mod.gottsch.forge.gottschcore.enums.IRarity;
import mod.gottsch.forge.magic_treasures.core.item.IJewelryType;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryStoneTier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Map;

/**
 * 
 * @author Mark Gottschling Jun 8, 2023
 *
 */
public class TagRegistry {
	private static final Map<IRarity, TagKey<Item>> STONE_RARITY_TAG_KEY_REGISTRY = Maps.newHashMap();
	private static final Map<IRarity, TagKey<Item>> JEWELRY_RARITY_TAG_KEY_REGISTRY = Maps.newHashMap();
	private static final Map<IJewelryType, TagKey<Item>> JEWELRY_TYPE_TAGS_REGISTRY = Maps.newHashMap();
	private static final Map<JewelryStoneTier, TagKey<Item>> STONE_TIER_TAGS_REGISTRY = Maps.newHashMap();
	
	private TagRegistry() { }

	public static void registerStoneRarity(IRarity rarity, TagKey<Item> tagKey) {
		STONE_RARITY_TAG_KEY_REGISTRY.put(rarity, tagKey);
	}

	public static void registerJewelryRarity(IRarity rarity, TagKey<Item> tagKey) {
		JEWELRY_RARITY_TAG_KEY_REGISTRY.put(rarity, tagKey);
	}

	public static void registerJewelryType(IJewelryType type, TagKey<Item> tagKey) {
		JEWELRY_TYPE_TAGS_REGISTRY.put(type, tagKey);
	}
	
	/**
	 * register a stoneTier tag to a stoneTier
	 * @param tier
	 * @param tierTag
	 */
//	@Deprecated
//	public static void registerJewelryStoneTier(IJewelryStoneTier tier, TagKey<Item> tierTag) {
////		STONE_TIER_TAGS_REGISTRY.put(tier, tierTag);
//	}

	public static void registerJewelryStoneTier(JewelryStoneTier tier, TagKey<Item> tierTag) {
		STONE_TIER_TAGS_REGISTRY.put(tier, tierTag);
	}

	public static TagKey<Item> getStoneRarityTag(IRarity rarity) {
		if (STONE_RARITY_TAG_KEY_REGISTRY.containsKey(rarity)) {
			return STONE_RARITY_TAG_KEY_REGISTRY.get(rarity);
		}
		return null;
	}

	public static TagKey<Item> getJewelryRarityTag(IRarity rarity) {
		if (JEWELRY_RARITY_TAG_KEY_REGISTRY.containsKey(rarity)) {
			return JEWELRY_RARITY_TAG_KEY_REGISTRY.get(rarity);
		}
		return null;
	}

	// TODO these need to return Optional<>
	public static TagKey<Item> getJewelryTypeTag(IJewelryType type) {
		if (JEWELRY_TYPE_TAGS_REGISTRY.containsKey(type)) {
			return JEWELRY_TYPE_TAGS_REGISTRY.get(type);
		}
		return null;
	}

	public static TagKey<Item> getJewelryStoneTierTag(JewelryStoneTier tier) {
		if (STONE_TIER_TAGS_REGISTRY.containsKey(tier)) {
			return STONE_TIER_TAGS_REGISTRY.get(tier);
		}
		return null;
	}
}
