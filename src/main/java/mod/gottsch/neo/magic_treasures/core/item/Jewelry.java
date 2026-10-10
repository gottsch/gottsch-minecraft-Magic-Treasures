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
 * Created by Mark Gottschling on 5/29/2023
 */
public class Jewelry extends Item implements IJewelry{
    private String loreKey;

    // cached result of jewelryDefaults(). the builder holds no tag-dependent data until build() is called.
    private JewelryHandler.Builder defaults;

    /**
     *
     * @param properties
     */
    public Jewelry(Properties properties) {
        super(properties.stacksTo(1));
    }

    /**
     * The builder for this item's default JewelryData. Items override this to set their material,
     * size, stone, spells etc. JewelryHandler.get(stack) builds the default data from it the first time
     * a stack is accessed (replaces the 1.20.1 initCapabilities override).
     */
    public JewelryHandler.Builder jewelryDefaults() {
        return new JewelryHandler.Builder(JewelryType.UNKNOWN, JewelryMaterials.NONE, JewelrySizeTier.UNKNOWN);
    }

    private JewelryHandler.Builder getDefaults() {
        if (defaults == null) {
            defaults = jewelryDefaults();
        }
        return defaults;
    }

    /**
     * whether the affixer (gem, spell scroll etc.) may be applied to this jewelry. not persisted.
     */
    public boolean acceptsAffixer(ItemStack affixer) {
        return getDefaults().acceptsAffixer.test(affixer);
    }

    /**
     * Applies Curse of Vanishing to jewelry whose defaults ask for it. In 1.20.1 this was done in
     * initCapabilities; in 1.21 enchantments are registry data, so it needs a level. Curios calls
     * inventoryTick for curio slots too.
     */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide() && getDefaults().vanishingCurse) {
            Holder<Enchantment> curse = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.VANISHING_CURSE);
            if (stack.getEnchantments().getLevel(curse) == 0) {
                stack.enchant(curse, 1);
            }
        }
    }

    /**
     * NOTE called every frame while rendered, so this reads the component directly rather than through
     * a handler (and doesn't create it).
     */
    @Override
    public boolean isFoil(ItemStack stack) {
        JewelryData data = stack.get(MagicTreasuresDataComponents.JEWELRY);
        return data != null ? !data.spells().isEmpty() : !getDefaults().spells.isEmpty();
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

        if (StringUtils.isNotBlank(getLoreKey())) {
            appendLoreHoverText(stack, context, tooltip, flag);
        }

        // add handler tooltips
        handler.ifPresent(h -> h.appendHoverText(stack, context, tooltip, flag));
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
