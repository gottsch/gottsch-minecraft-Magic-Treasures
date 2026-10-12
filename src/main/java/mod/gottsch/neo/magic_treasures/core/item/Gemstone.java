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

import mod.gottsch.neo.magic_treasures.core.jewelry.data.GemstoneDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.StoneTierDef;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Gottschling on 7/4/2023
 */
public class Gemstone extends Item {

    public Gemstone(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return ((MutableComponent)super.getName(stack)).withStyle(getNameColor());
    }

    public ChatFormatting getNameColor() {
        return ChatFormatting.WHITE;
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltip, flag);
        addHoverText(itemStack, level, tooltip, flag);
    }

    /**
     * The gem part of a tooltip, from the gem's {@code gemstone} registry entry: usage, and with shift its rarity,
     * its stone tier's mana, recharges and factors, and the materials it is limited to. Shown for any item that is an
     * enabled gemstone, MT's or not (ItemEventHandler adds it to other mods' items); nothing for a disabled gem.
     */
    public static void addHoverText(ItemStack itemStack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        Optional<GemstoneDef> gem = JewelryDataIndex.gemstone(itemStack.getItem()).map(Holder::value);
        if (gem.isEmpty()) {
            return;
        }
        tooltip.add(Component.translatable(LangUtil.tooltip("gemstone.usage")).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.translatable(LangUtil.NEWLINE));

        LangUtil.appendAdvancedHoverText(tooltip, tt -> {
            StoneTierDef tier = gem.get().tier().value();
            gem.get().rarity().ifPresent(r -> line(tt, "gemstone.rarity", ChatFormatting.GOLD + WordUtils.capitalizeFully(r.getValue())));
            line(tt, "gemstone.mana", ChatFormatting.BLUE + String.valueOf(tier.mana()));
            line(tt, "gemstone.recharges", ChatFormatting.GREEN + String.valueOf(tier.recharges()));
            factor(tt, "gemstone.cost_factor", tier.factors().cost());
            factor(tt, "gemstone.cooldown_factor", tier.factors().cooldown());
            factor(tt, "gemstone.effect_amount_factor", tier.factors().effect());
            factor(tt, "gemstone.duration_factor", tier.factors().duration());
            factor(tt, "gemstone.range_factor", tier.factors().range());
            gem.get().affixesTo().ifPresent(materials -> {
                MutableComponent names = Component.empty();
                materials.stream().forEach(material -> {
                    ResourceLocation id = material.unwrapKey().map(ResourceKey::location).orElse(null);
                    if (id != null) {
                        if (!names.getSiblings().isEmpty()) {
                            names.append(", ");
                        }
                        names.append(Component.translatableWithFallback("material." + id.getNamespace() + "." + id.getPath(),
                                WordUtils.capitalizeFully(id.getPath().replace('_', ' '))));
                    }
                });
                tt.add(Component.translatable(LangUtil.INDENT2).append(Component.translatable(LangUtil.tooltip("gemstone.affixes_to"),
                        names.withStyle(ChatFormatting.YELLOW))));
            });
            tt.add(Component.translatable(LangUtil.NEWLINE));
        });
    }

    private static void line(List<Component> tooltip, String key, String value) {
        tooltip.add(Component.translatable(LangUtil.INDENT2).append(Component.translatable(LangUtil.tooltip(key), value)));
    }

    /** a factor's line, only when it changes something */
    private static void factor(List<Component> tooltip, String key, double factor) {
        if (factor < 1.0) {
            line(tooltip, key, ChatFormatting.AQUA + LangUtil.negativePercent(factor));
        } else if (factor > 1.0) {
            line(tooltip, key, ChatFormatting.AQUA + LangUtil.positivePercent(factor));
        }
    }
}
