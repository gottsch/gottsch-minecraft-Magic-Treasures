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
package mod.gottsch.neo.magic_treasures.core.client.hud;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.capability.ManaWellHandler;
import mod.gottsch.neo.magic_treasures.core.config.Config;
import mod.gottsch.neo.magic_treasures.core.set.JewelrySet;
import mod.gottsch.neo.magic_treasures.core.set.JewelrySets;
import mod.gottsch.neo.magic_treasures.core.set.SetEquipment;
import mod.gottsch.neo.magic_treasures.core.set.SpellStat;
import mod.gottsch.neo.magic_treasures.core.spell.CooldownSpellEntity;
import mod.gottsch.neo.magic_treasures.core.spell.Spell;
import mod.gottsch.neo.magic_treasures.core.spell.SpellEntity;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The jewelry HUD: one icon per worn piece that casts or pays for spells, with a mana bar under it and vanilla's
 * cooldown sweep over it, and a line for each set with a piece worn. A piece with several spells has its icon split
 * into one column per spell, in spell order, each with its own sweep (Ring of Life and Death: Cheat Death on the left,
 * Greater Drain on the right).
 * <p>
 * "Worn" is {@link SetEquipment#getWornItems}, the same rule spells and sets use. The HUD only shows while at least
 * one worn piece has a spell. Mana wells show beside the jewelry they pay for.
 */
public final class JewelryHud implements LayeredDraw.Layer {
	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "jewelry_hud");

	private static final int ICON = 16;
	private static final int SLOT = 18;
	private static final int BAR_GAP = 1;
	private static final int BAR_HEIGHT = 2;
	private static final int LINE_GAP = 3;

	private static final int BAR_BACK = 0xFF000000;
	private static final int JEWELRY_MANA = 0xFF5AA0FF;
	private static final int WELL_MANA = 0xFFE08A3A;
	/** vanilla's cooldown overlay: translucent white */
	private static final int COOLDOWN = 0x7FFFFFFF;

	/** {@code cooldowns}: per spell, in spell order, how much of its cooldown is left (0 to 1); empty for a mana well */
	private record Piece(ItemStack stack, float mana, int manaColor, float[] cooldowns) {}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		if (player == null || minecraft.options.hideGui || minecraft.getDebugOverlay().showDebugScreen()
				|| !Config.CLIENT.showJewelryHud.get()) {
			return;
		}

		float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
		int screenWidth = graphics.guiWidth();
		int screenHeight = graphics.guiHeight();
		List<ItemStack> worn = SetEquipment.getWornItems(player);
		List<Piece> pieces = gatherPieces(player, worn, partialTick);
		if (pieces.isEmpty()) {
			return;
		}
		List<Component> setLines = gatherSetLines(worn);

		Font font = minecraft.font;
		Config.HudCorner corner = Config.CLIENT.jewelryHudCorner.get();
		int offsetX = Config.CLIENT.jewelryHudOffsetX.get();
		int offsetY = Config.CLIENT.jewelryHudOffsetY.get();

		int iconsWidth = pieces.size() * SLOT - (SLOT - ICON);
		int iconsHeight = ICON + BAR_GAP + BAR_HEIGHT;
		int textHeight = setLines.isEmpty() ? 0 : LINE_GAP + setLines.size() * font.lineHeight;
		int top = corner.isBottom() ? screenHeight - offsetY - iconsHeight - textHeight : offsetY;
		int iconsLeft = corner.isRight() ? screenWidth - offsetX - iconsWidth : offsetX;

		for (int i = 0; i < pieces.size(); i++) {
			renderPiece(graphics, pieces.get(i), iconsLeft + i * SLOT, top);
		}

		int y = top + iconsHeight + LINE_GAP;
		for (Component line : setLines) {
			int x = corner.isRight() ? screenWidth - offsetX - font.width(line) : offsetX;
			graphics.drawString(font, line, x, y, 0xFFFFFF, true);
			y += font.lineHeight;
		}
	}

	private static void renderPiece(GuiGraphics graphics, Piece piece, int x, int y) {
		graphics.renderItem(piece.stack(), x, y);
		float[] cooldowns = piece.cooldowns();
		for (int i = 0; i < cooldowns.length; i++) {
			if (cooldowns[i] <= 0) {
				continue;
			}
			// one column per spell; columns after the first leave a 1px gap so side-by-side sweeps read apart
			int left = x + i * ICON / cooldowns.length + (i > 0 ? 1 : 0);
			int right = x + (i + 1) * ICON / cooldowns.length;
			// same overlay vanilla draws over an item on cooldown (an ender pearl after a throw)
			int top = y + Mth.floor(ICON * (1.0F - cooldowns[i]));
			int bottom = top + Mth.ceil(ICON * cooldowns[i]);
			graphics.fill(RenderType.guiOverlay(), left, top, right, bottom, COOLDOWN);
		}
		int barY = y + ICON + BAR_GAP;
		graphics.fill(x, barY, x + ICON, barY + BAR_HEIGHT, BAR_BACK);
		int filled = Math.round(ICON * piece.mana());
		if (filled > 0) {
			graphics.fill(x, barY, x + filled, barY + BAR_HEIGHT, piece.manaColor());
		}
	}

	/** The worn jewelry with spells, then the worn mana wells; empty when nothing worn has a spell. */
	private static List<Piece> gatherPieces(Player player, List<ItemStack> worn, float partialTick) {
		List<Piece> jewelry = new ArrayList<>();
		List<Piece> wells = new ArrayList<>();
		double now = player.level().getGameTime() + partialTick;
		for (ItemStack stack : worn) {
			Optional<IJewelryHandler> handler = JewelryHandler.get(stack);
			if (handler.isPresent()) {
				if (!handler.get().getSpells().isEmpty()) {
					jewelry.add(new Piece(stack, fraction(handler.get().getMana(), handler.get().getMaxMana()), JEWELRY_MANA,
							cooldowns(player, stack, handler.get(), now)));
				}
				continue;
			}
			ManaWellHandler.get(stack).ifPresent(well ->
					wells.add(new Piece(stack, fraction(well.getMana(), well.getMaxMana()), WELL_MANA, new float[0])));
		}
		if (jewelry.isEmpty()) {
			return List.of();
		}
		jewelry.addAll(wells);
		return jewelry;
	}

	/** For each spell on the jewelry, in order, how much of its cooldown is left, 0 to 1 (0 for a spell without one). */
	private static float[] cooldowns(Player player, ItemStack stack, IJewelryHandler handler, double now) {
		List<SpellEntity> spells = handler.getSpells();
		float[] result = new float[spells.size()];
		for (int i = 0; i < spells.size(); i++) {
			SpellEntity entity = spells.get(i);
			if (!(entity instanceof CooldownSpellEntity cooldownEntity) || !(entity.getSpell() instanceof Spell spell)) {
				continue;
			}
			double remaining = cooldownEntity.getCooldownExpireTime() - now;
			if (remaining <= 0) {
				continue;
			}
			// the full length this cast was given, set bonuses included, as the cast worked it out
			double total = SetEquipment.modify(player, stack, spell, SpellStat.COOLDOWN, spell.modifyCooldown(stack));
			if (total > 0) {
				result[i] = (float) Math.min(1.0, remaining / total);
			}
		}
		return result;
	}

	/** "Silbro's Grove (2/3)" for each set with a piece worn; green once a bonus tier is on. */
	private static List<Component> gatherSetLines(List<ItemStack> worn) {
		List<Component> lines = new ArrayList<>();
		for (JewelrySet set : JewelrySets.getAll()) {
			int count = set.countWorn(worn);
			if (count == 0) {
				continue;
			}
			boolean active = !set.getTiers().isEmpty() && count >= set.getTiers().firstKey();
			lines.add(Component.translatable(LangUtil.tooltip("set.header"), set.getName(), count, set.size())
					.withStyle(active ? ChatFormatting.GREEN : ChatFormatting.GRAY));
		}
		return lines;
	}

	private static float fraction(double value, double max) {
		return max <= 0 ? 0 : (float) Mth.clamp(value / max, 0, 1);
	}
}
