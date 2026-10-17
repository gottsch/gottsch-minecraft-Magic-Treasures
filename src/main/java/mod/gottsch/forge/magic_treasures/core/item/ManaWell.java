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
package mod.gottsch.forge.magic_treasures.core.item;

import mod.gottsch.forge.magic_treasures.core.capability.IManaWellHandler;
import mod.gottsch.forge.magic_treasures.core.capability.ManaWellHandler;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * A mana reservoir. While worn (any Curios slot) or held, spells take the part of their cost that their
 * jewelry can't pay out of it. Recharged at an anvil, like jewelry; it has no spells of its own.
 *
 * @author Mark Gottschling on 10/9/2026
 */
public class ManaWell extends Item {
    private final double maxMana;
    private final int maxRecharges;

    public ManaWell(Properties properties, double maxMana, int maxRecharges) {
        super(properties.stacksTo(1));
        this.maxMana = maxMana;
        this.maxRecharges = maxRecharges;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public int getMaxRecharges() {
        return maxRecharges;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        // same layout as jewelry: blank line, description (hidden while [shift] is held), blank line, stats
        tooltip.add(Component.translatable(LangUtil.NEWLINE));
        LangUtil.appendHideableHoverText(tooltip, tt -> {
            tooltip.add(Component.translatable(LangUtil.tooltip("mana_well.usage")).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
            tooltip.add(Component.translatable(LangUtil.NEWLINE));
        });
        ManaWellHandler.get(stack).ifPresent(handler -> {
            tooltip.add(Component.translatable(LangUtil.INDENT2)
                    .append(Component.translatable(LangUtil.tooltip("jewelry.mana"),
                            ChatFormatting.BLUE + String.valueOf(Math.round(handler.getMana())),
                            ChatFormatting.BLUE + String.valueOf(Math.round(handler.getMaxMana())))));
            tooltip.add(Component.translatable(LangUtil.INDENT2)
                    .append(Component.translatable(LangUtil.tooltip("jewelry.mana.recharges"), ChatFormatting.BLUE + String.valueOf(handler.getRecharges()))));
        });
    }

}
