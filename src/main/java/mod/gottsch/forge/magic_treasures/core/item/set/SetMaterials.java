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
package mod.gottsch.forge.magic_treasures.core.item.set;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.forge.magic_treasures.core.tag.MagicTreasuresTags;
import net.minecraftforge.common.Tags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Tool tiers and armor materials of the set weapons and armor.
 */
public final class SetMaterials {

    private SetMaterials() {}

    /** Silbro's grown wood: between stone and iron. A SwordItem adds this bonus, its own damage and the player's 1. */
    public static final Tier LIVING_WOOD = new SetTier(350, 4.0F, 1.0F, 1, 15, () -> Ingredient.of(ItemTags.LOGS));

    /** Silbro's bark: chainmail's protection, iron's durability. */
    public static final ArmorMaterial BARKSKIN = new SetArmorMaterial("barkskin", 15,
            Map.of(ArmorItem.Type.HELMET, 2, ArmorItem.Type.CHESTPLATE, 5, ArmorItem.Type.LEGGINGS, 4, ArmorItem.Type.BOOTS, 1),
            15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(ItemTags.LOGS));

    /*
     * #12: the other three sets. Numbers are for scale; Mark tunes them in game (plan D9). On 1.21.1 armor durability
     * is per item; here it is the material's multiplier, with the same values.
     */

    /** Sal'andaar's smith's steel (Stonemark Hammer): a little better than iron. */
    public static final Tier SMITHS_STEEL = new SetTier(450, 6.5F, 2.0F, 2, 14, () -> Ingredient.of(Tags.Items.INGOTS_IRON));

    /** Sal'andaar's smith's steel (Smith's Helm): diamond's protection, less toughness, enchants poorly. */
    public static final ArmorMaterial SMITHS_STEEL_ARMOR = new SetArmorMaterial("smiths_steel", 25, defense(3, 8, 6, 3),
            9, SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F, () -> Ingredient.of(Tags.Items.INGOTS_IRON));

    /** Anvil Shield durability: the vanilla shield's 336, a bit more for smith's steel. */
    public static final int SMITHS_STEEL_SHIELD_DURABILITY = 450;
    public static final Supplier<Ingredient> SMITHS_STEEL_REPAIR = () -> Ingredient.of(Tags.Items.INGOTS_IRON);

    /** Maldritch's bone (Bone Scythe): stone-level, but takes enchantments well. */
    public static final Tier BONE = new SetTier(300, 5.0F, 2.0F, 1, 18, () -> Ingredient.of(Tags.Items.BONES));

    /** Maldritch's bone (Lich's Mask): chainmail's protection; his set leans on its weapon and death bonus. */
    public static final ArmorMaterial BONE_ARMOR = new SetArmorMaterial("bone", 15, defense(2, 5, 4, 1),
            18, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.of(Tags.Items.BONES));

    /** Selene's moon silver (Moonglaive): iron-level, enchants best of the three. */
    public static final Tier MOON_SILVER = new SetTier(400, 6.0F, 2.0F, 2, 20,
            () -> Ingredient.of(MagicTreasuresTags.Items.INGOTS_SILVER));

    /** Selene's pearls (Pearl Circlet): diamond's protection and toughness, and enchants best of any set material. */
    public static final ArmorMaterial PEARL_ARMOR = new SetArmorMaterial("pearl", 25, defense(3, 8, 6, 3),
            25, SoundEvents.ARMOR_EQUIP_GOLD, 2.0F, 0.0F,
            () -> Ingredient.of(MagicTreasuresItems.WHITE_PEARL.get(), MagicTreasuresItems.BLACK_PEARL.get()));

    private static Map<ArmorItem.Type, Integer> defense(int helmet, int chest, int legs, int boots) {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.HELMET, helmet);
        map.put(ArmorItem.Type.CHESTPLATE, chest);
        map.put(ArmorItem.Type.LEGGINGS, legs);
        map.put(ArmorItem.Type.BOOTS, boots);
        return map;
    }

    private record SetTier(int uses, float speed, float attackDamageBonus, int level, int enchantmentValue,
                           Supplier<Ingredient> repair) implements Tier {
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
        public int getLevel() {
            return level;
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

    private static final class SetArmorMaterial implements ArmorMaterial {
        /** vanilla's per-slot durability base, multiplied by the material's multiplier */
        private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = new EnumMap<>(Map.of(
                ArmorItem.Type.HELMET, 11, ArmorItem.Type.CHESTPLATE, 16, ArmorItem.Type.LEGGINGS, 15, ArmorItem.Type.BOOTS, 13));

        private final String name;
        private final int durabilityMultiplier;
        private final Map<ArmorItem.Type, Integer> defense;
        private final int enchantmentValue;
        private final SoundEvent sound;
        private final float toughness;
        private final float knockbackResistance;
        private final Supplier<Ingredient> repair;

        SetArmorMaterial(String name, int durabilityMultiplier, Map<ArmorItem.Type, Integer> defense, int enchantmentValue,
                         SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repair) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.defense = defense;
            this.enchantmentValue = enchantmentValue;
            this.sound = sound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.repair = repair;
        }

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return BASE_DURABILITY.get(type) * durabilityMultiplier;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return defense.get(type);
        }

        @Override
        public int getEnchantmentValue() {
            return enchantmentValue;
        }

        @Override
        public SoundEvent getEquipSound() {
            return sound;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repair.get();
        }

        /** textures/models/armor/{name}_layer_1.png in this mod's namespace */
        @Override
        public String getName() {
            return MagicTreasures.MOD_ID + ":" + name;
        }

        @Override
        public float getToughness() {
            return toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return knockbackResistance;
        }
    }
}
