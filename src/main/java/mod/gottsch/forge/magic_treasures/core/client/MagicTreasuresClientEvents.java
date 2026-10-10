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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import mod.gottsch.forge.magic_treasures.core.client.tooltip.TooltipMarkers;
import mod.gottsch.forge.magic_treasures.core.set.JewelrySets;
import mod.gottsch.forge.magic_treasures.core.set.SetEquipment;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import java.util.ArrayList;
import java.util.List;

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
	/** room left under a tall tooltip, so its end doesn't look cut off */
	private static final int TOOLTIP_BOTTOM_MARGIN = 16;

	/**
	 * Obscure Tooltips draws its own icon, name and rarity header from the tooltip's first text line. With our icon
	 * header in place of the name, it takes the next line (Curios' "Slot: Ring") as the title and both headers show.
	 */
	private static final boolean HAS_OWN_ICON_HEADER = ModList.get().isLoaded("obscure_tooltips");

	/**
	 * Show the item's icon beside its name in every Magic Treasures tooltip, then swap the tooltip's marker lines
	 * (dividers, bars, chips, grids) for drawn components.
	 * <p>
	 * The marker swap runs for every tooltip, not only Magic Treasures items: some screens (Curios slots) draw an
	 * item's tooltip lines without passing the stack, so the event's stack is empty. Only the icon needs the stack.
	 */
	@SubscribeEvent
	public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
		if (Config.CLIENT.showTooltipIcon.get() && !HAS_OWN_ICON_HEADER) {
			IconTitleTooltips.apply(event, MagicTreasuresClientEvents::isMagicTreasuresItem, TOOLTIP_ICON_SCALE);
		}
		RichTooltips.apply(event, MagicTreasures.MOD_ID, TOOLTIP_MAX_WIDTH);
	}

	/** Widen dividers and bars to the finished tooltip, and keep it off the bottom of the screen. */
	@SubscribeEvent
	public static void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
		RichTooltips.stretch(event);
		RichTooltips.lift(event, TOOLTIP_BOTTOM_MARGIN);
	}

	/**
	 * Adds the set block to the tooltip of any set piece: the set's name with how many pieces are worn, each piece
	 * (worn ones lit), and each bonus tier (active ones lit). Goes above "Hold [SHIFT] to expand" when that's shown.
	 */
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		JewelrySets.get(event.getItemStack().getItem()).ifPresent(set -> {
			Player player = event.getEntity();
			List<ItemStack> worn = player == null ? List.of() : SetEquipment.getWornItems(player);
			int count = set.countWorn(worn);

			List<Component> lines = new ArrayList<>();
			lines.add(TooltipMarkers.divider(MagicTreasures.MOD_ID));
			lines.add(Component.translatable(LangUtil.tooltip("set.header"), set.getName(), count, set.size())
					.withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
			for (Item piece : set.getPieces()) {
				boolean isWorn = worn.stream().anyMatch(stack -> stack.is(piece));
				lines.add(Component.literal(LangUtil.INDENT2).append(piece.getDescription())
						.withStyle(isWorn ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
			}
			for (int pieces : set.getTiers().keySet()) {
				lines.add(Component.translatable(LangUtil.tooltip("set.bonus"), pieces, set.getBonusDescription(pieces))
						.withStyle(count >= pieces ? ChatFormatting.GREEN : ChatFormatting.GRAY));
			}

			List<Component> tooltip = event.getToolTip();
			int at = tooltip.size();
			for (int i = 0; i < tooltip.size(); i++) {
				if (tooltip.get(i).getContents() instanceof TranslatableContents contents
						&& contents.getKey().equals(LangUtil.tooltip("hold_shift"))) {
					// keep the blank line above "Hold [SHIFT]" between it and the set block
					at = i > 0 ? i - 1 : i;
					break;
				}
			}
			tooltip.addAll(at, lines);
		});
	}

	private static boolean isMagicTreasuresItem(ItemStack stack) {
		return MagicTreasures.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
	}
}
