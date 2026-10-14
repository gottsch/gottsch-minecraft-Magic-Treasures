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
import mod.gottsch.neo.magic_treasures.core.item.set.SetArmor;
import mod.gottsch.neo.magic_treasures.core.item.set.SetShield;
import mod.gottsch.neo.magic_treasures.core.item.set.SetMaterials;
import mod.gottsch.neo.magic_treasures.core.item.set.SiphonWeapon;
import mod.gottsch.neo.magic_treasures.core.setup.Registration;
import mod.gottsch.neo.magic_treasures.core.spell.MagicTreasuresSpells;
import mod.gottsch.neo.magic_treasures.core.spell.SpellRegistry;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.LegacyConversion;
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
	public static DeferredItem<Item> RING_RECIPE = Registration.ITEMS.register("ring_recipe", () -> new JewelryRecipeScroll("ring", MAGIC_TREASURES_PROPS_SUPPLIER.get()));
	public static DeferredItem<Item> NECKLACE_RECIPE = Registration.ITEMS.register("necklace_recipe", () -> new JewelryRecipeScroll("necklace", MAGIC_TREASURES_PROPS_SUPPLIER.get()));
	public static DeferredItem<Item> BRACELET_RECIPE = Registration.ITEMS.register("bracelet_recipe", () -> new JewelryRecipeScroll("bracelet", MAGIC_TREASURES_PROPS_SUPPLIER.get()));
//	public static DeferredItem<Item> BELT_RECIPE = Registration.ITEMS.register("belt_recipe", () -> new JewelryRecipeScroll(magic_treasures_PROPS_SUPPLIER.get()));

	// 2.0 generic jewelry: one item per type; what the piece is lives in the jewelry_identity component
	public static DeferredItem<Item> RING = Registration.ITEMS.register("ring", () -> new GenericJewelry("ring", MAGIC_TREASURES_PROPS_SUPPLIER.get()));
	public static DeferredItem<Item> NECKLACE = Registration.ITEMS.register("necklace", () -> new GenericJewelry("necklace", MAGIC_TREASURES_PROPS_SUPPLIER.get()));
	public static DeferredItem<Item> BRACELET = Registration.ITEMS.register("bracelet", () -> new GenericJewelry("bracelet", MAGIC_TREASURES_PROPS_SUPPLIER.get()));
	public static final List<DeferredItem<Item>> GENERIC_JEWELRY = List.of(RING, NECKLACE, BRACELET);

	// recharge scroll
	public static DeferredItem<Item> RECHARGE_SCROLL =Registration.ITEMS.register("recharge_scroll", () -> new Item(MAGIC_TREASURES_PROPS_SUPPLIER.get()));

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
	// a mana well: 250 mana, 2 recharges
	// one item for every lore book; the story is a data component (see LoreBook)
	public static DeferredItem<Item> LORE_BOOK = Registration.ITEMS.register("lore_book", () -> new LoreBook(MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	public static DeferredItem<Item> SKULL_BELT = Registration.ITEMS.register("skull_belt", () -> new ManaWell(MAGIC_TREASURES_PROPS_SUPPLIER.get(), 250, 2));


	// set weapons and armor: no spells; siphon (weapons) and resonance (armor)
	// Silbro's Grove: a staff, quick (1 + 1 tier + 5 = 7 damage, a diamond sword's, +0.5 reach), siphon 2
	public static DeferredItem<Item> ROOTSTAFF = Registration.ITEMS.register("rootstaff", () ->
			new SiphonWeapon(SetMaterials.LIVING_WOOD, 5, -2.4F, 0.5, 2, "set_piece.rootstaff.lore", MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	public static DeferredItem<Item> BARKSKIN_VEST = Registration.ITEMS.register("barkskin_vest", () ->
			new SetArmor(SetMaterials.BARKSKIN, ArmorItem.Type.CHESTPLATE, 15, "set_piece.barkskin_vest.lore", MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	// Sal'andaar's Anvilwork: heavy and slow (1 + 2 tier + 8 = 11 damage, out-hits a netherite axe), siphon 2
	public static DeferredItem<Item> STONEMARK_HAMMER = Registration.ITEMS.register("stonemark_hammer", () ->
			new SiphonWeapon(SetMaterials.SMITHS_STEEL, 8, -3.1F, 2, "set_piece.stonemark_hammer.lore", MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	public static DeferredItem<Item> SMITHS_HELM = Registration.ITEMS.register("smiths_helm", () ->
			new SetArmor(SetMaterials.SMITHS_STEEL_ARMOR, ArmorItem.Type.HELMET, 25, "set_piece.smiths_helm.lore", MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	public static DeferredItem<Item> ANVIL_SHIELD = Registration.ITEMS.register("anvil_shield", () ->
			new SetShield(SetMaterials.SMITHS_STEEL_SHIELD_DURABILITY, SetMaterials.SMITHS_STEEL_REPAIR, "set_piece.anvil_shield.lore",
					MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	// Maldritch's Remains: long-handled (1 + 2 tier + 6 = 9 damage, a trident's, +0.5 reach), siphon 3, the strongest
	public static DeferredItem<Item> BONE_SCYTHE = Registration.ITEMS.register("bone_scythe", () ->
			new SiphonWeapon(SetMaterials.BONE, 6, -2.8F, 0.5, 3, "set_piece.bone_scythe.lore", MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	public static DeferredItem<Item> LICHS_MASK = Registration.ITEMS.register("lichs_mask", () ->
			new SetArmor(SetMaterials.BONE_ARMOR, ArmorItem.Type.HELMET, 15, "set_piece.lichs_mask.lore", MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	// Selene's Tides: a polearm (1 + 2 tier + 5 = 8 damage, +0.5 reach), siphon 2
	public static DeferredItem<Item> MOONGLAIVE = Registration.ITEMS.register("moonglaive", () ->
			new SiphonWeapon(SetMaterials.MOON_SILVER, 5, -2.6F, 0.5, 2, "set_piece.moonglaive.lore", MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	public static DeferredItem<Item> PEARL_CIRCLET = Registration.ITEMS.register("pearl_circlet", () ->
			new SetArmor(SetMaterials.PEARL_ARMOR, ArmorItem.Type.HELMET, 25, "set_piece.pearl_circlet.lore", MAGIC_TREASURES_PROPS_SUPPLIER.get()));

	/*
	 * M9: the 418 1.x jewelry item ids (the M0 table), kept registered so old stacks load; each converts to generic
	 * jewelry when carried. The 1.x builders, named pieces and combination items they replaced are gone.
	 */
	public static final Map<ResourceLocation, DeferredItem<Item>> LEGACY_JEWELRY = new LinkedHashMap<>();
	static {
		LegacyConversion.ids().forEach(id -> LEGACY_JEWELRY.put(id,
				Registration.ITEMS.register(id.getPath(), () -> new LegacyJewelryItem(MAGIC_TREASURES_PROPS_SUPPLIER.get()))));
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
}
