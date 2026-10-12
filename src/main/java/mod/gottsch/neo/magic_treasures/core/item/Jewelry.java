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

import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.component.JewelryData;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterials;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.client.tooltip.TooltipMarkers;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import java.util.Optional;

/**
 * The base of jewelry items: lore and the tooltip, through the stack's {@link IJewelryHandler}. Since M9 the only
 * jewelry items are {@link GenericJewelry} (the 1.x items with built-in defaults are gone).
 * Created by Mark Gottschling on 5/29/2023
 */
public class Jewelry extends Item implements IJewelry{
    private String loreKey;

    /**
     *
     * @param properties
     */
    public Jewelry(Properties properties) {
        super(properties.stacksTo(1));
    }

    /**
     * whether the affixer (gem, spell scroll etc.) may be applied to this jewelry. not persisted.
     */
    public boolean acceptsAffixer(ItemStack affixer) {
        return true;
    }

    @Override
    public Component getName(ItemStack itemStack) {
        if (isNamed()) {
            return Component.translatable(this.getDescriptionId(itemStack)).withStyle(ChatFormatting.YELLOW);
        } else {
            return Component.translatable(this.getDescriptionId(itemStack));
        }
    }

    /**
     *
     */
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        Optional<IJewelryHandler> handler = JewelryHandler.get(stack);
        // "Ring · Ruby · Lvl 4", drawn under the name in the icon header
        handler.ifPresent(h -> tooltip.add(TooltipMarkers.subtitle(MagicTreasures.MOD_ID, h.getSubtitle())));
        tooltip.add(Component.literal(LangUtil.NEWLINE));

        if (hasLore(stack)) {
            appendLoreHoverText(stack, context, tooltip, flag);
        }

        // add handler tooltips
        handler.ifPresent(h -> h.appendHoverText(stack, context, tooltip, flag));
    }

    /** whether the tooltip shows lore; generic jewelry has it per stack (its preset's lore) */
    protected boolean hasLore(ItemStack stack) {
        return StringUtils.isNotBlank(getLoreKey());
    }

    @Override
    public void appendLoreHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

            // lore may be multiple lines, so separate on \n and add to tooltip
            Component lore = Component.translatable(LangUtil.tooltip(getLoreKey()));
            for (String s : lore.getString().split("~")) {
                tooltip.add(Component.translatable(s)
                        .withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.ITALIC));
            }
        tooltip.add(Component.literal(LangUtil.NEWLINE));
    }

    @Override
    public String getLoreKey() {
        return loreKey;
    }
    @Override
    public Item setLoreKey(String loreKey) {
        this.loreKey = loreKey;
        return this;
    }
}
