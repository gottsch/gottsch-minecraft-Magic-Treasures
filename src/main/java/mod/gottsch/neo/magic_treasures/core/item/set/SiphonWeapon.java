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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * A set weapon. It carries no spell; its full-strength hits on a mob put mana back into the worn jewelry instead
 * (siphon, see {@code SiphonEventHandler}): a slow source of mana that isn't the anvil.
 */
public class SiphonWeapon extends SwordItem {
    private final int siphonMana;
    private final String loreKey;

    /** the reach a polearm adds in the main hand */
    private static final ResourceLocation REACH_ID = ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "set_weapon_reach");

    /** {@code attackDamage} and {@code attackSpeed} as for a vanilla sword: added to the tier's bonus and the player's base */
    public SiphonWeapon(Tier tier, int attackDamage, float attackSpeed, int siphonMana, String loreKey, Properties properties) {
        this(tier, attackDamage, attackSpeed, 0.0, siphonMana, loreKey, properties);
    }

    /** As above, plus {@code reach} blocks of attack reach while held (a polearm) */
    public SiphonWeapon(Tier tier, int attackDamage, float attackSpeed, double reach, int siphonMana, String loreKey, Properties properties) {
        super(tier, properties.attributes(withReach(SwordItem.createAttributes(tier, attackDamage, attackSpeed), reach)));
        this.siphonMana = siphonMana;
        this.loreKey = loreKey;
    }

    private static ItemAttributeModifiers withReach(ItemAttributeModifiers attributes, double reach) {
        if (reach <= 0) {
            return attributes;
        }
        return attributes.withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE,
                new AttributeModifier(REACH_ID, reach, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        SetPieceTooltips.appendLore(tooltip, loreKey);
        SetPieceTooltips.appendSiphon(tooltip, siphonMana);
    }
}
