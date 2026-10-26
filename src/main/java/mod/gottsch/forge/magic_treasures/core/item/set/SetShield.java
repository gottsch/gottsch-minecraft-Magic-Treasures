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

import mod.gottsch.forge.magic_treasures.core.set.SetEquipment;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.function.Supplier;

/**
 * A set shield. Like set armor it carries no spell; held in either hand it resonates with its set's jewelry
 * ({@link SetEquipment#isResonant} counts shields). Blocking works as for the vanilla shield.
 * <p>
 * Its model needs the {@code blocking} item property, which vanilla registers for its own shield only; the client
 * setup registers it for every set shield.
 */
public class SetShield extends ShieldItem {
    private final Supplier<Ingredient> repair;
    private final String loreKey;

    /** {@code durability} as for the vanilla shield (336) */
    public SetShield(int durability, Supplier<Ingredient> repair, String loreKey, Properties properties) {
        super(properties.durability(durability));
        this.repair = repair;
        this.loreKey = loreKey;
    }

    /** the set's own material, not the vanilla shield's planks */
    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repair.get().test(repairCandidate);
    }

    /** no banner colours: ShieldItem would add one to the name */
    @Override
    public String getDescriptionId(ItemStack stack) {
        return getDescriptionId();
    }

    @Override
    public Component getName(ItemStack stack) {
        return ((MutableComponent) super.getName(stack)).withStyle(ChatFormatting.YELLOW);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        SetPieceTooltips.appendLore(tooltip, loreKey);
        SetPieceTooltips.appendResonance(tooltip, SetEquipment.RESONANCE_PER_PIECE);
    }
}
