/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2024 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.magic_treasures.core.event;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.item.Gemstone;
import mod.gottsch.forge.magic_treasures.core.tag.MagicTreasuresTags;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 
 * @author Mark Gottschling May 26, 2024
 *
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID)
public class ItemEventHandler {

	@SubscribeEvent
	public static void onItemInfo(ItemTooltipEvent event) {
		// attach magic things gemstones tooltips to vanilla and other mod items
		if (!(event.getItemStack().getItem() instanceof Gemstone) && event.getItemStack().is(MagicTreasuresTags.Items.STONES)) {
//			event.getToolTip().add(Component.translatable(LangUtil.tooltip("gemstone.usage")).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
//			event.getToolTip().add(Component.translatable(LangUtil.NEWLINE));
			Gemstone.addHoverText(event.getItemStack(), null, event.getToolTip(), TooltipFlag.Default.NORMAL);
		}

		// attach magic things recharge item tooltips to vanilla and other mod items
		if (event.getItemStack().is(MagicTreasuresTags.Items.RECHARGERS)) {
			event.getToolTip().add(Component.translatable(LangUtil.NEWLINE));
			event.getToolTip().add(Component.translatable(LangUtil.tooltip("gemstone.recharge.usage")).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
			event.getToolTip().add(Component.translatable(LangUtil.NEWLINE));
		}
	}
}
