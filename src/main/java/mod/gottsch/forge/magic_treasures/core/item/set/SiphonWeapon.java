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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

/**
 * A set weapon. It carries no spell; its full-strength hits on a mob put mana back into the worn jewelry instead
 * (siphon, see {@code SiphonEventHandler}): a slow source of mana that isn't the anvil.
 */
public class SiphonWeapon extends SwordItem {
    private final int siphonMana;
    private final String loreKey;

    /** the reach a polearm adds in the main hand; a fixed id (1.20.1 modifiers are keyed by UUID; 1.21.1 uses
     * {@code magictreasures:set_weapon_reach}) */
    private static final UUID REACH_ID = UUID.fromString("5b0a3f8e-6c2d-4e1a-9b7f-3d8c1e2a4f60");

    private final double reach;
    /** the sword's modifiers plus reach, built on first use: ForgeMod's attribute isn't there while items register */
    private Multimap<Attribute, AttributeModifier> mainHandModifiers;

    /** {@code attackDamage} and {@code attackSpeed} as for a vanilla sword: added to the tier's bonus and the player's base */
    public SiphonWeapon(Tier tier, int attackDamage, float attackSpeed, int siphonMana, String loreKey, Properties properties) {
        this(tier, attackDamage, attackSpeed, 0.0, siphonMana, loreKey, properties);
    }

    /** As above, plus {@code reach} blocks of attack reach while held (a polearm) */
    public SiphonWeapon(Tier tier, int attackDamage, float attackSpeed, double reach, int siphonMana, String loreKey, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
        this.reach = reach;
        this.siphonMana = siphonMana;
        this.loreKey = loreKey;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND || reach <= 0) {
            return super.getDefaultAttributeModifiers(slot);
        }
        if (mainHandModifiers == null) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(super.getDefaultAttributeModifiers(slot));
            builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(REACH_ID, "Set weapon reach", reach,
                    AttributeModifier.Operation.ADDITION));
            mainHandModifiers = builder.build();
        }
        return mainHandModifiers;
    }

    /** Mana a full-strength hit puts back into the worn jewelry. */
    public int getSiphonMana() {
        return siphonMana;
    }

    @Override
    public Component getName(ItemStack stack) {
        return ((MutableComponent) super.getName(stack)).withStyle(ChatFormatting.YELLOW);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        SetPieceTooltips.appendLore(tooltip, loreKey);
        SetPieceTooltips.appendSiphon(tooltip, siphonMana);
    }
}
