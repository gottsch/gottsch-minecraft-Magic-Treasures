/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.magic_treasures.core.item.set;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.tag.MagicTreasuresTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Tool tiers and armor materials of the set weapons and armor. Armor materials are a registry in 1.21.
 */
public final class SetMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, MagicTreasures.MOD_ID);

    private SetMaterials() {}

    /** Silbro's grown wood: between stone and iron. A sword adds this bonus, its own damage and the player's 1. */
    public static final Tier LIVING_WOOD = new SetTier(350, 4.0F, 1.0F, BlockTags.INCORRECT_FOR_STONE_TOOL, 15,
            () -> Ingredient.of(ItemTags.LOGS));

    /** Silbro's bark: chainmail's protection. Durability is per item (iron's multiplier, 15). */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BARKSKIN = ARMOR_MATERIALS.register("barkskin", () ->
            new ArmorMaterial(defense(2, 5, 4, 1), 15, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(ItemTags.LOGS),
                    // textures/models/armor/barkskin_layer_1.png
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "barkskin"))),
                    0.0F, 0.0F));

    /*
     * #12: the other three sets. Numbers are for scale; Mark tunes them in game (plan D9).
     */

    /** Sal'andaar's smith's steel (Stonemark Hammer): a little better than iron. */
    public static final Tier SMITHS_STEEL = new SetTier(450, 6.5F, 2.0F, BlockTags.INCORRECT_FOR_IRON_TOOL, 14,
            () -> Ingredient.of(Tags.Items.INGOTS_IRON));

    /** Sal'andaar's smith's steel (Smith's Helm): diamond's protection, less toughness, enchants poorly. */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SMITHS_STEEL_ARMOR = armor("smiths_steel",
            defense(3, 8, 6, 3), 1.0F, 9, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Tags.Items.INGOTS_IRON));

    /** Anvil Shield durability: the vanilla shield's 336, a bit more for smith's steel. */
    public static final int SMITHS_STEEL_SHIELD_DURABILITY = 450;
    public static final Supplier<Ingredient> SMITHS_STEEL_REPAIR = () -> Ingredient.of(Tags.Items.INGOTS_IRON);

    /** Maldritch's bone (Bone Scythe): stone-level, but takes enchantments well. */
    public static final Tier BONE = new SetTier(300, 5.0F, 2.0F, BlockTags.INCORRECT_FOR_STONE_TOOL, 18,
            () -> Ingredient.of(Tags.Items.BONES));

    /** Maldritch's bone (Lich's Cowl): chainmail's protection; his set leans on its weapon and death bonus. */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BONE_ARMOR = armor("bone",
            defense(2, 5, 4, 1), 0.0F, 18, SoundEvents.ARMOR_EQUIP_CHAIN, () -> Ingredient.of(Tags.Items.BONES));

    /** Selene's moon silver (Moonglaive): iron-level, enchants best of the three. */
    public static final Tier MOON_SILVER = new SetTier(400, 6.0F, 2.0F, BlockTags.INCORRECT_FOR_IRON_TOOL, 20,
            () -> Ingredient.of(MagicTreasuresTags.Items.INGOTS_SILVER));

    /** Selene's pearls (Pearl Circlet): diamond's protection and toughness, and enchants best of any set material. */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PEARL_ARMOR = armor("pearl",
            defense(3, 8, 6, 3), 2.0F, 25, SoundEvents.ARMOR_EQUIP_GOLD,
            () -> Ingredient.of(MagicTreasuresItems.WHITE_PEARL.get(), MagicTreasuresItems.BLACK_PEARL.get()));

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }

    /** an armor material whose worn texture is {@code textures/models/armor/<name>_layer_1.png} */
    private static DeferredHolder<ArmorMaterial, ArmorMaterial> armor(String name, Map<ArmorItem.Type, Integer> defense,
            float toughness, int enchantmentValue, Holder<SoundEvent> sound, Supplier<Ingredient> repair) {
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(defense, enchantmentValue, sound, repair,
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, name))),
                toughness, 0.0F));
    }

    private static Map<ArmorItem.Type, Integer> defense(int helmet, int chest, int legs, int boots) {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.HELMET, helmet);
        map.put(ArmorItem.Type.CHESTPLATE, chest);
        map.put(ArmorItem.Type.LEGGINGS, legs);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.BODY, chest);
        return map;
    }

    private record SetTier(int uses, float speed, float attackDamageBonus, TagKey<Block> incorrectBlocks,
                           int enchantmentValue, Supplier<Ingredient> repair) implements Tier {
        @Override
        public int getUses() {
            return uses;
        }

        @Override
        public float getSpeed() {
            return speed;
        }

        @Override
        public float getAttackDamageBonus() {
            return attackDamageBonus;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return incorrectBlocks;
        }

        @Override
        public int getEnchantmentValue() {
            return enchantmentValue;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repair.get();
        }
    }
}
