/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * Magic Treasures is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Magic Treasures is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Magic Treasures.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neo.magic_treasures.core.item;

import com.google.common.collect.Lists;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.block.MagicTreasuresBlocks;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterial;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterials;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.neo.magic_treasures.core.setup.Registration;
import mod.gottsch.neo.magic_treasures.core.spell.MagicTreasuresSpells;
import mod.gottsch.neo.magic_treasures.core.spell.SpellRegistry;
import mod.gottsch.neo.magic_treasures.core.tag.MagicTreasuresTags;
import mod.gottsch.neo.magic_treasures.core.util.ModUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import mod.gottsch.neo.magic_treasures.core.item.JewelryPliers;


/**
 * Created by Mark Gottschling on 5/29/2023
 */
public class MagicTreasuresItems {

    public static final Supplier<Item.Properties> MAGIC_TREASURES_PROPS_SUPPLIER = () -> new Item.Properties();
    // tab items
    public static final DeferredItem<Item> MAGIC_TREASURES_TAB = Registration.ITEMS.register("magic_treasures_tab", () -> new Item(new Item.Properties()));

	// tools
	// 64 uses: each gem extraction costs 1 durability
	public static DeferredItem<Item> JEWELRY_PLIERS = Registration.ITEMS.register("jewelry_pliers", () -> new JewelryPliers(MAGIC_TREASURES_PROPS_SUPPLIER.get().durability(64)));

	// spell scrolls
	public static final List<DeferredItem<Item>> ALL_SPELL_SCROLLS = Lists.newArrayList();
	static {
		SpellRegistry.values().forEach(spell -> {
			DeferredItem<Item> scroll = Registration.ITEMS.register(spell.getName().getPath() + "_scroll",
					() -> new SpellScroll(MAGIC_TREASURES_PROPS_SUPPLIER.get(), spell));
			// add scroll registry item to a list
			ALL_SPELL_SCROLLS.add(scroll);
		});
	}

	// recipe scrolls
	public static DeferredItem<Item> RING_RECIPE = Registration.ITEMS.register("ring_recipe", () -> new JewelryRecipeScroll(MAGIC_TREASURES_PROPS_SUPPLIER.get()));
	public static DeferredItem<Item> NECKLACE_RECIPE = Registration.ITEMS.register("necklace_recipe", () -> new JewelryRecipeScroll(MAGIC_TREASURES_PROPS_SUPPLIER.get()));
	public static DeferredItem<Item> BRACELET_RECIPE = Registration.ITEMS.register("bracelet_recipe", () -> new JewelryRecipeScroll(MAGIC_TREASURES_PROPS_SUPPLIER.get()));
//	public static DeferredItem<Item> BELT_RECIPE = Registration.ITEMS.register("belt_recipe", () -> new JewelryRecipeScroll(magic_treasures_PROPS_SUPPLIER.get()));

	// recharge scroll
	public static DeferredItem<Item> RECHARGE_SCROLL = Registration.ITEMS.register("recharge_scroll", () -> new Item(MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	// metals / ingots / ores
	public static DeferredItem<Item> SILVER_INGOT = Registration.ITEMS.register("silver_ingot", () -> new Item(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		@Override
		public void appendHoverText(ItemStack itemStack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
			// TODO
		}
	});
	public static DeferredItem<Item> RAW_SILVER = Registration.ITEMS.register("raw_silver", () -> new Item(MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	// gemstones
	public static DeferredItem<Item> JADEITE = Registration.ITEMS.register("jadeite", () -> new Gemstone(MAGIC_TREASURES_PROPS_SUPPLIER.get()));
	public static DeferredItem<Item> TOPAZ = Registration.ITEMS.register("topaz", () -> new Gemstone(MAGIC_TREASURES_PROPS_SUPPLIER.get()));
    public static DeferredItem<Item> ONYX = Registration.ITEMS.register("onyx", () -> new Gemstone(MAGIC_TREASURES_PROPS_SUPPLIER.get()));
	public static DeferredItem<Item> RUBY = Registration.ITEMS.register("ruby", () -> new Gemstone(MAGIC_TREASURES_PROPS_SUPPLIER.get()));
    public static DeferredItem<Item> SAPPHIRE = Registration.ITEMS.register("sapphire", () -> new Gemstone(MAGIC_TREASURES_PROPS_SUPPLIER.get()));
    public static DeferredItem<Item> WHITE_PEARL = Registration.ITEMS.register("white_pearl", () -> new Gemstone(MAGIC_TREASURES_PROPS_SUPPLIER.get()));
    public static DeferredItem<Item> BLACK_PEARL = Registration.ITEMS.register("black_pearl", () -> new Gemstone(MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	// belts
	public static DeferredItem<Item> SKULL_BELT = Registration.ITEMS.register("skull_belt", () -> new ManaWell(MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	/*
     * a list of all mod generated items.
     */
    public static final List<DeferredItem<Item>> ALL_JEWELRY = Lists.newArrayList();
	public static final List<DeferredItem<Item>> STANDARD_JEWELRY = Lists.newArrayList();

	// custom jewelry

	// common/uncommon
	public static DeferredItem<Item> SILBROS_RING_OF_VITALITY = Registration.ITEMS.register("silbros_ring_of_vitality", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.RING, JewelryMaterials.WOOD)
					.withSize(JewelrySizeTier.REGULAR)
					.withStone(ModUtil.getName(Items.BEDROCK))
					.with($ -> {
						$.spells.add(MagicTreasuresSpells.DEFAULT_HEALING.entity());
						$.maxMana = 80;
						$.maxRecharges = 0;
						$.maxRepairs = 1;
						$.maxLevel = 2;
						$.acceptsAffixer = p -> {
							return false;
						};
					});
		}
	}.setLoreKey("jewelry.silbros_ring_of_vitality.lore"));

	public static DeferredItem<Item> STRONGMANS_BRACERS = Registration.ITEMS.register("strongmans_bracers", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
		return new JewelryHandler.Builder(JewelryType.BRACELET, JewelryMaterials.WOOD)
				.withSize(JewelrySizeTier.REGULAR)
				.withStone(ModUtil.getName(Items.BEDROCK))
				.with($ -> {
					$.spells.add(SpellRegistry.get(MagicTreasuresSpells.QUICK_STRENGTH).orElse(MagicTreasuresSpells.DEFAULT_HEALING).entity());
					$.maxMana = 50;
					$.maxUses = 100;
					$.maxRecharges = 1;
					$.maxLevel = 2;
					$.acceptsAffixer = p -> {
						return false;
					};
				});
		}
	});

	// common
	public static DeferredItem<Item> PEASANTS_FORTUNE = Registration.ITEMS.register("peasants_fortune", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.RING, JewelryMaterials.IRON)
					.withSize(JewelrySizeTier.GREAT)
					.withStone(ModUtil.getName(Items.BEDROCK))
					.with($ -> {
						$.maxUses = 125;
						$.maxMana = 250;
						$.maxRecharges = 1;
						$.maxLevel = 4;
					});
		}
	});

	// uncommon
	public static DeferredItem<Item> AMULET_OF_DEFENCE = Registration.ITEMS.register("amulet_of_defence", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.NECKLACE, JewelryMaterials.COPPER)
					.withSize(JewelrySizeTier.REGULAR)
					.withStone(MagicTreasuresItems.TOPAZ.getId())
					.with($ -> {
						$.spells.add(MagicTreasuresSpells.MAGIC_RESISTANCE_SPELL.entity());
						$.maxMana = 100;
					})
					.setInfinite();
		}
	});

	public static DeferredItem<Item> MALDRITCHS_FIRST_AMULET = Registration.ITEMS.register("maldritchs_first_amulet", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.NECKLACE, JewelryMaterials.BONE)
					.withSize(JewelrySizeTier.REGULAR)
					.withStone(MagicTreasuresItems.ONYX.getId())
					.with($ -> {
						$.spells.add(SpellRegistry.get(MagicTreasuresSpells.HARM).orElse(MagicTreasuresSpells.DEFAULT_HEALING).entity());
						$.maxUses = 300;
						$.maxMana = 150;
						$.maxRepairs = 1;
						$.spellCostFactor = .95; // 0.1 points below regular bone
					});
		}
	}.setLoreKey("jewelry.maldritchs_first_amulet.lore"));

	public static DeferredItem<Item> AQUA_RING = Registration.ITEMS.register("aqua_ring", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.RING, JewelryMaterials.SILVER)
					.withSize(JewelrySizeTier.REGULAR)
					.withStone(MagicTreasuresItems.TOPAZ.getId())
					.with($ -> {
						$.spells.add(MagicTreasuresSpells.WATER_BREATHING_SPELL.entity());
						$.maxMana = 150;
					})
					.setInfinite();
		}
	});

	// scarce
	public static DeferredItem<Item> JOURNEYMANS_BANDS = Registration.ITEMS.register("journeyman_bands", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.BRACELET, JewelryMaterials.GOLD)
					.withSize(JewelrySizeTier.GREAT)
					.withStone(MagicTreasuresItems.JADEITE.getId())
					.with($ -> {
						$.spells.add(MagicTreasuresSpells.SPEED_SPELL.entity());
						$.spells.add(MagicTreasuresSpells.NIGHT_VISION_SPELL.entity());
						$.maxMana = 100;
						$.maxRecharges = 0;
					})
					.setInfinite();
		}
	});

	public static DeferredItem<Item> MEDICS_TOKEN = Registration.ITEMS.register("medics_token", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.NECKLACE, JewelryMaterials.GOLD)
					.withSize(JewelrySizeTier.GREAT)
					.withStone(JADEITE.getId())
					.with($ -> {
						$.spells.add(SpellRegistry.get(MagicTreasuresSpells.GREATER_HEALING).orElse(MagicTreasuresSpells.DEFAULT_HEALING).entity());
						$.maxMana = 300;
						$.maxRecharges = 1;
					})
					.setInfinite();
		}
	});

	// scarce
	public static DeferredItem<Item> ADEPHAGIAS_BOUNTY = Registration.ITEMS.register("adephagias_bounty", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.BRACELET, JewelryMaterials.GOLD)
					.withSize(JewelrySizeTier.GREAT)
					.withStone(JADEITE.getId())
					.with($ -> {
						$.spells.add(SpellRegistry.get(MagicTreasuresSpells.SATIETY).orElse(MagicTreasuresSpells.DEFAULT_HEALING).entity());
						$.maxMana = 300;
						$.maxRecharges = 1;
						$.maxLevel = 6;
					})
					.setInfinite();
		}
	});

	// rare
	public static DeferredItem<Item> SALANDAARS_WARD = Registration.ITEMS.register("salandaars_ward", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		@Override
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.NECKLACE, JewelryMaterials.GOLD)
					.withSize(JewelrySizeTier.GREAT)
					.withStone(MagicTreasuresItems.RUBY.getId())
					.with($ -> {
						$.spells.add(SpellRegistry.get(MagicTreasuresSpells.MANA_TOWER_SHIELD).orElse(MagicTreasuresSpells.DEFAULT_HEALING).entity());
						$.maxMana = 350;
						$.maxRecharges = 3;
						$.maxLevel = 7;
					});
		}
	});

	// epic
	public static DeferredItem<Item> ANGELS_RING = Registration.ITEMS.register("angels_ring", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.RING, JewelryMaterials.GOLD)
					.withSize(JewelrySizeTier.GREAT)
					.withStone(MagicTreasuresItems.WHITE_PEARL.getId())
					.with($ -> {
						$.spells.add(SpellRegistry.get(MagicTreasuresSpells.REGENERATION).orElse(MagicTreasuresSpells.DEFAULT_HEALING).entity());
						$.maxMana = 400;
						$.maxRecharges = 3;
						$.maxLevel = 8;
					});
		}
	});

	// rare / epic
	public static DeferredItem<Item> RING_OF_FORTITUDE = Registration.ITEMS.register("ring_of_fortitude", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.RING, JewelryMaterials.GOLD)
					.withSize(JewelrySizeTier.GREAT)
					.withStone(SAPPHIRE.getId())
					.with($ -> {
						$.vanishingCurse = true;
						$.baseName = "castle_ring";
						$.spells.add(SpellRegistry.get(MagicTreasuresSpells.SHADOW_ARMOR).get().entity());
						$.maxMana = 350;
						$.maxRecharges = 3;
						$.maxLevel = 7;
						$.acceptsAffixer = castleRingAffixer;
					})
					.setInfinite();
		}
	});
	//	.setLoreKey("jewelry.castle_ring.lore"));

	// epic / legendary
	public static DeferredItem<Item> RING_LIFE_DEATH = Registration.ITEMS.register("ring_of_life_death", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {
			return new JewelryHandler.Builder(JewelryType.RING, JewelryMaterials.BLOOD)
					.withSize(JewelrySizeTier.LORDS)
					.withStone(RUBY.getId())
					.with($ -> {
						$.vanishingCurse = true;
						$.baseName = "twisted_ring";
						$.spells.add(SpellRegistry.get(MagicTreasuresSpells.CHEAT_DEATH).get().entity());
						$.spells.add(SpellRegistry.get(MagicTreasuresSpells.GREATER_DRAIN).get().entity());
						$.maxMana = 1000;
						$.maxRecharges = 0;
						$.maxRepairs = 0;
						$.maxLevel = 9;
						$.acceptsAffixer = p -> {
							return false;
						};
					})
					.setInfinite();
		}
	});

	// rare / epic
	public static DeferredItem<Item> EYE_OF_THE_PHOENIX = Registration.ITEMS.register("eye_of_the_phoenix", () -> new NamedJewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
		public JewelryHandler.Builder jewelryDefaults() {

			return new JewelryHandler.Builder(JewelryType.NECKLACE, JewelryMaterials.GOLD)
					.withSize(JewelrySizeTier.GREAT)
					.withStone(BLACK_PEARL.getId())
					.with($ -> {
						$.baseName = "amulet";
						$.spells.add(SpellRegistry.get(MagicTreasuresSpells.BLESSING_OF_THE_PHOENIX).get().entity());
						$.maxMana = 300;
						$.maxRecharges = 3;
						$.maxRepairs = 3;
						$.maxLevel = 7;
					})
					.setInfinite();
		}
	});

	// TEST
//	public static DeferredItem<Item> SCARAB = Registration.ITEMS.register("scarab", () -> new NamedJewelry(magic_treasures_PROPS_SUPPLIER.get()) {
//		public JewelryHandler.Builder jewelryDefaults() {
//			return new JewelryHandler.Builder(JewelryType.CHARM, JewelryMaterials.GOLD)
//					.withSize(JewelrySizeTier.REGULAR)
//					.withStone(Items.AIR.getRegistryName())
//					.with($ -> $.spells.add(SpellRegistry.get(ModUtil.asLocation("lesser_healing")).orElse(MagicTreasuresSpells.DEFAULT_HEALING).entity()))
//					.build();
//
//			return new JewelryCapability(handler);
//		}
//	});

	// TODO move to MagicTreasuresAffixes or Jewelry class
    static Predicate<ItemStack> castleRingAffixer = p -> {
		return p.is(MagicTreasuresTags.Items.CASTLE_RING_RUBY)
				| p.is(MagicTreasuresTags.Items.CASTLE_RING_SAPPHIRE);
	};

    static {

		// SCARAB will be a mana well
//		STANDARD_JEWELRY.add(SCARAB);

		// TODO this is a special jewelry
//		ALL_JEWELRY.add(SCARAB);

		List<Pair<String, Supplier<Jewelry>>> jewelry = new ArrayList<>();
		JewelryBuilder builder = new JewelryBuilder(MagicTreasures.MOD_ID);
		jewelry = builder.useBaseDefaults().deferredBuild();
		jewelry.addAll(builder.useStoneDefaults().deferredBuild());

		// castle rings
		JewelryBuilder builder2 = new JewelryBuilder(MagicTreasures.MOD_ID);
		jewelry.addAll(builder2
				.types(JewelryType.RING)
				.materials(JewelryMaterials.SILVER)
				.sizes(JewelrySizeTier.REGULAR)
				.with($ -> {
					$.baseName = "castle_ring";
					$.loreKey = "jewelry.castle_ring.lore";
					$.maxUses = 500;
					$.maxMana = 200;
					$.maxRepairs = 0;
					$.acceptsAffixer = p -> {
						return p.is(MagicTreasuresTags.Items.CASTLE_RING_RUBY)
								| p.is(MagicTreasuresTags.Items.CASTLE_RING_SAPPHIRE);
					};
					// TODO all castle rings should be infinite or very high durability
				})
				.deferredBuild());
		jewelry.addAll(builder2
				.stones(RUBY.getId(), SAPPHIRE.getId())
				.deferredBuild());

		// wood jewelry (only regular size)
		JewelryBuilder woodBuilder = new JewelryBuilder(MagicTreasures.MOD_ID);
		jewelry.addAll(woodBuilder
				.materials(JewelryMaterials.WOOD)
				.sizes(JewelrySizeTier.REGULAR)
				.types(JewelryType.RING, JewelryType.BRACELET, JewelryType.NECKLACE)
				.deferredBuild()
		);
		jewelry.addAll(woodBuilder.useStoneDefaults().deferredBuild());

		// bone jewelry (only regular for now)
//		JewelryBuilder boneBuilder = new JewelryBuilder(MagicTreasures.MOD_ID);
//		jewelry.addAll(boneBuilder
//				.materials(JewelryMaterials.BONE)
//				.sizes(JewelrySizeTier.REGULAR, JewelrySizeTier.GREAT)
//				.types(JewelryType.RING, JewelryType.BRACELET, JewelryType.NECKLACE)
//				.deferredBuild()
//		);
//		jewelry.addAll(boneBuilder.useStoneDefaults().deferredBuild());

		// POC Lord's Necklaces
		JewelryBuilder lordsNecklacesBuilder = new JewelryBuilder(MagicTreasures.MOD_ID);
		jewelry.addAll(lordsNecklacesBuilder
				.materials(
						JewelryMaterials.IRON,
						JewelryMaterials.COPPER,
						JewelryMaterials.SILVER,
						JewelryMaterials.GOLD,
						JewelryMaterials.BONE)
				.sizes(JewelrySizeTier.LORDS)
				.types(JewelryType.RING, JewelryType.NECKLACE)
				.deferredBuild()
		);
		jewelry.addAll(lordsNecklacesBuilder.useStoneDefaults().deferredBuild());

		// Hawk Ring POC
		JewelryBuilder hawkRingBuilder = new HawkJewelryBuilder(MagicTreasures.MOD_ID);
		jewelry.addAll(hawkRingBuilder
				.materials(
						JewelryMaterials.WOOD,
						JewelryMaterials.IRON,
						JewelryMaterials.COPPER,
						JewelryMaterials.SILVER,
						JewelryMaterials.GOLD
				)
				.sizes(JewelrySizeTier.REGULAR)
				.types(JewelryType.RING)
				.with($ -> {
					$.baseName = "hawk_ring";
					$.acceptsAffixer = p -> {return false;};
				})
				.deferredBuild()
		);

		JewelryBuilder hawkRingBuilder2 = new HawkJewelryBuilder(MagicTreasures.MOD_ID);
		jewelry.addAll(hawkRingBuilder2
						.materials(
								JewelryMaterials.IRON,
								JewelryMaterials.COPPER,
								JewelryMaterials.SILVER,
								JewelryMaterials.GOLD
						)
						.sizes(JewelrySizeTier.GREAT, JewelrySizeTier.LORDS)
						.types(JewelryType.RING)
						.with($ -> {
							$.baseName = "hawk_ring";
							$.acceptsAffixer = p -> {return false;};
						})
						.deferredBuild()
		);

		jewelry.forEach(pair -> {
			DeferredItem<Item> item = Registration.ITEMS.register(pair.getKey(), pair.getValue());
			STANDARD_JEWELRY.add(item);
			MagicTreasures.LOGGER.debug("adding item -> {} to standard registry", item.getId());
			ALL_JEWELRY.add(item);
		});

    }

	public static final DeferredItem<Item> TOPAZ_ORE = fromBlock(MagicTreasuresBlocks.TOPAZ_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);
	public static final DeferredItem<Item> DEEPSLATE_TOPAZ_ORE = fromBlock(MagicTreasuresBlocks.DEEPSLATE_TOPAZ_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);

	public static final DeferredItem<Item> ONYX_ORE = fromBlock(MagicTreasuresBlocks.ONYX_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);
	public static final DeferredItem<Item> DEEPSLATE_ONYX_ORE = fromBlock(MagicTreasuresBlocks.DEEPSLATE_ONYX_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);

	public static final DeferredItem<Item> JADEITE_ORE = fromBlock(MagicTreasuresBlocks.JADEITE_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);
	public static final DeferredItem<Item> DEEPSLATE_JADEITE_ORE = fromBlock(MagicTreasuresBlocks.DEEPSLATE_JADEITE_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);


	public static final DeferredItem<Item> RUBY_ORE = fromBlock(MagicTreasuresBlocks.RUBY_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);
	public static final DeferredItem<Item> DEEPSLATE_RUBY_ORE = fromBlock(MagicTreasuresBlocks.DEEPSLATE_RUBY_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);
	public static final DeferredItem<Item> SAPPHIRE_ORE = fromBlock(MagicTreasuresBlocks.SAPPHIRE_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);
	public static final DeferredItem<Item> DEEPSLATE_SAPPHIRE_ORE = fromBlock(MagicTreasuresBlocks.DEEPSLATE_SAPPHIRE_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);

	public static final DeferredItem<Item> SILVER_ORE_BLOCKITEM = fromBlock(MagicTreasuresBlocks.SILVER_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);
	public static final DeferredItem<Item> DEEPSLATE_SILVER_ORE_BLOCKITEM = fromBlock(MagicTreasuresBlocks.DEEPSLATE_SILVER_ORE, MAGIC_TREASURES_PROPS_SUPPLIER);

	// TODO create/register all jewelry

	/** force-loads this class so its holders are created before the register is attached to the bus */
	public static void init() {
	}

	// convenience method: take a DeferredBlock<Block> and make a corresponding DeferredItem<Item> from it
	public static <B extends Block> DeferredItem<Item> fromBlock(DeferredBlock<B> block, Supplier<Item.Properties> itemProperties) {
		return Registration.ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), itemProperties.get()));
	}

	/**
	 * 
	 * @author Mark Gottschling Jul 11, 2023
	 *
	 */
	public static class JewelryBuilder {
		public List<JewelryType> types = new ArrayList<>();
		public List<JewelrySizeTier> sizes = new ArrayList<>();
		public List<JewelryMaterial> materials = new ArrayList<>();
		public List<ResourceLocation> stones = new ArrayList<>();
		public String baseName;
		public String loreKey;
		public int maxUses = -1;
		public int maxMana = -1;
		public int maxRepairs = -1;
		public int maxLevel = 0;
		public Predicate<ItemStack> acceptsAffixer = p -> true;
		
		protected String modid;
		
		public JewelryBuilder(String modid) {
			this.modid = modid;
		}
		
		public JewelryBuilder clear() {
			types.clear();
			sizes.clear();
			materials.clear();
			stones.clear();
			return this;
		}
		
		/*
		 * convenience setup
		 */
		public JewelryBuilder useBaseDefaults() {
			types(JewelryType.BRACELET, JewelryType.NECKLACE, JewelryType.RING); // ...
			sizes(JewelrySizeTier.REGULAR, JewelrySizeTier.GREAT);
			materials(
//					JewelryMaterials.WOOD,
					JewelryMaterials.IRON,
					JewelryMaterials.COPPER,
					JewelryMaterials.SILVER,
					JewelryMaterials.GOLD,
					JewelryMaterials.BONE);
			return this;
		}
		
		public JewelryBuilder useStoneDefaults() {
			stones(
					ModUtil.getName(Items.DIAMOND),
					JADEITE.getId(),
					TOPAZ.getId(),
					ONYX.getId(),
					RUBY.getId(),
					SAPPHIRE.getId(),
					WHITE_PEARL.getId(),
					BLACK_PEARL.getId()
					);
			return this;
		}

		public JewelryBuilder with(Consumer<JewelryBuilder> builder) {
			builder.accept(this);
			return this;
		}

		public JewelryBuilder types(JewelryType... types) {
			getTypes().addAll(Arrays.asList(types));
			return this;
		}
		
		public JewelryBuilder sizes(JewelrySizeTier... sizes) {
			getSizes().addAll(Arrays.asList(sizes));
			return this;
		}
		
		public JewelryBuilder materials(JewelryMaterial... materials) {
			getMaterials().addAll(Arrays.asList(materials));
			return this;
		}
		
		public JewelryBuilder stones(ResourceLocation... sources) {
			getStones().addAll(Arrays.asList(sources));
			return this;
		}

		/**
		 * Deferred build.
		 * This method returns a list of String/Supplier Pairs.
		 * This method can be used in a static call of a class to construct RegistryObjects
		 * The String in the Pair is used as the name (without the namespace)
		 * NOTE adornments are deferred and thus are not registered in
		 * TreasureAdornmentRegistry yet. That would have to happen in a post init like event.
		 * @return
		 */
		public List<Pair<String, Supplier<Jewelry>>> deferredBuild() {
			List<Pair<String, Supplier<Jewelry>>> jewelry = new ArrayList<>();

			if (types.isEmpty()) return jewelry;
			if (sizes.isEmpty()) return jewelry;

			List<ResourceLocation> tempStones = new ArrayList<>();
			if (stones.isEmpty()) {
				tempStones.add(ModUtil.getName(Items.AIR));
			}
			else {
				tempStones.addAll(stones);
			}

			// create the jewelry
			types.forEach(type -> {
				sizes.forEach(size -> {
					materials.forEach(material -> {
						tempStones.forEach(stone -> {
							// build the name
							String name = (size == JewelrySizeTier.REGULAR ? "" : size.getValue() + "_")
									+ material.getName() + "_"
									+ (stone.equals(ModUtil.getName(Items.AIR)) ? "" :  stone.getPath() + "_")
									+ (StringUtils.isNotBlank(baseName) ? baseName : type.toString());

							// build the jewelry supplier
							Supplier<Jewelry> a = deferredCreateJewelry(type, material, size, stone);
							MagicTreasures.LOGGER.debug("adding deferred jewelry item -> {}", name);

							// build a pair
							Pair<String, Supplier<Jewelry>> pair = Pair.of(name.toLowerCase(), a);
							jewelry.add(pair);
						});
					});
				});
			});
			return jewelry;
		}

		public Supplier<Jewelry> deferredCreateJewelry(JewelryType type, JewelryMaterial material, JewelrySizeTier size, ResourceLocation stone) {
			return () -> {
				Jewelry j = new Jewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
					public JewelryHandler.Builder jewelryDefaults() {
						return new JewelryHandler.Builder(type, material, stone, size)
								.with($ -> {
									$.baseName = JewelryBuilder.this.getBaseName();
									$.maxLevel = JewelryBuilder.this.maxLevel;
									$.maxUses = JewelryBuilder.this.maxUses;
									$.maxMana = JewelryBuilder.this.maxMana;
									$.maxRepairs = JewelryBuilder.this.maxRepairs;
									$.acceptsAffixer = JewelryBuilder.this.acceptsAffixer;
								});
					}
				};
				return (Jewelry) j.setLoreKey(JewelryBuilder.this.loreKey);
			};
		}

		public List<JewelryType> getTypes() {
			return types;
		}

		public List<JewelrySizeTier> getSizes() {
			return sizes;
		}

		public List<JewelryMaterial> getMaterials() {
			return materials;
		}

		public List<ResourceLocation> getStones() {
			return stones;
		}

		public String getBaseName() {
			return this.baseName;
		}

		public String getModid() {
			return modid;
		}
	}

	public static class HawkJewelryBuilder extends JewelryBuilder {

		public HawkJewelryBuilder(String modid) {
			super(modid);
		}

		@Override
		public Supplier<Jewelry> deferredCreateJewelry(JewelryType type, JewelryMaterial material, JewelrySizeTier size, ResourceLocation stone) {
			return () -> {
				Jewelry j = new Jewelry(MAGIC_TREASURES_PROPS_SUPPLIER.get()) {
					public JewelryHandler.Builder jewelryDefaults() {
						return new JewelryHandler.Builder(type, material, stone, size)
								.with($ -> {
									$.baseName = HawkJewelryBuilder.this.getBaseName();
									$.maxLevel = material.getMaxLevel() + 1;
									$.maxUses = HawkJewelryBuilder.this.maxUses;
									$.maxMana = material.getUses() * Math.max(1, material.getRepairs() + size.getRepairs()) * Math.max(1, size.getUsesMultiplier());
									$.maxRepairs = HawkJewelryBuilder.this.maxRepairs;
									$.acceptsAffixer = HawkJewelryBuilder.this.acceptsAffixer;

								});
					}
				};
				return (Jewelry) j.setLoreKey("jewelry.hawk_ring.lore");
			};
		}
	}
}
