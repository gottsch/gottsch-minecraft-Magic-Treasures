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

import mod.gottsch.neo.magic_treasures.core.set.SetEquipment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Set armor. It carries no spell; while worn it resonates with its set's jewelry, adding to their spells' effect
 * ({@link SetEquipment#RESONANCE_PER_PIECE} per piece).
 */
public class SetArmor extends ArmorItem {
    private final String loreKey;

    /** {@code durabilityMultiplier} as for a vanilla armor material (iron is 15) */
    public SetArmor(Holder<ArmorMaterial> material, Type type, int durabilityMultiplier, String loreKey, Properties properties) {
        super(material, type, properties.durability(type.getDurability(durabilityMultiplier)));
        this.loreKey = loreKey;
    }

    @Override
    public Component getName(ItemStack stack) {
        return ((MutableComponent) super.getName(stack)).withStyle(ChatFormatting.YELLOW);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        SetPieceTooltips.appendLore(tooltip, loreKey);
        SetPieceTooltips.appendResonance(tooltip, SetEquipment.RESONANCE_PER_PIECE);
    }
}
