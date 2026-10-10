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

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * A set weapon. It carries no spell; its full-strength hits on a mob put mana back into the worn jewelry instead
 * (siphon, see {@code SiphonEventHandler}): a slow source of mana that isn't the anvil.
 */
public class SiphonWeapon extends SwordItem {
    private final int siphonMana;
    private final String loreKey;

    public SiphonWeapon(Tier tier, int attackDamage, float attackSpeed, int siphonMana, String loreKey, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
        this.siphonMana = siphonMana;
        this.loreKey = loreKey;
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
