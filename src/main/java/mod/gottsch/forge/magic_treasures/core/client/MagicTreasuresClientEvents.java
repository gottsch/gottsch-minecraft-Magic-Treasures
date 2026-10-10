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
package mod.gottsch.forge.magic_treasures.core.client;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.client.tooltip.IconTitleTooltips;
import mod.gottsch.forge.magic_treasures.core.client.tooltip.RichTooltips;
import mod.gottsch.forge.magic_treasures.core.config.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-only game-bus events.
 *
 * @author Mark Gottschling on 10/9/2026
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MagicTreasuresClientEvents {
	/** icon scale for the tooltip header: 2 = 32px */
	private static final float TOOLTIP_ICON_SCALE = 2.0F;
	/** text wraps here, and chips start a new row */
	private static final int TOOLTIP_MAX_WIDTH = 200;

	/**
	 * Show the item's icon beside its name in every Magic Treasures tooltip, then swap the tooltip's marker lines
	 * (dividers, bars, chips, grids) for drawn components.
	 * <p>
	 * The marker swap runs for every tooltip, not only Magic Treasures items: some screens (Curios slots) draw an
	 * item's tooltip lines without passing the stack, so the event's stack is empty. Only the icon needs the stack.
	 */
	@SubscribeEvent
	public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
		if (Config.CLIENT.showTooltipIcon.get()) {
			IconTitleTooltips.apply(event, MagicTreasuresClientEvents::isMagicTreasuresItem, TOOLTIP_ICON_SCALE);
		}
		RichTooltips.apply(event, MagicTreasures.MOD_ID, TOOLTIP_MAX_WIDTH);
	}

	/** Widen dividers and bars to the finished tooltip. */
	@SubscribeEvent
	public static void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
		RichTooltips.stretch(event);
	}

	private static boolean isMagicTreasuresItem(ItemStack stack) {
		return MagicTreasures.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
	}
}
