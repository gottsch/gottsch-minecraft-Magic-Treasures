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

import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * The tooltip lines set weapons and armor share with jewelry: the lore (dark aqua, italic, "~" breaks lines), then the
 * piece's own mechanic. The set block comes from the client tooltip event, as for jewelry.
 */
public final class SetPieceTooltips {

    private SetPieceTooltips() {}

    public static void appendLore(List<Component> tooltip, String loreKey) {
        tooltip.add(Component.literal(LangUtil.NEWLINE));
        Component lore = Component.translatable(LangUtil.tooltip(loreKey));
        for (String line : lore.getString().split("~")) {
            tooltip.add(Component.literal(line).withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC));
        }
        tooltip.add(Component.literal(LangUtil.NEWLINE));
    }

    /** "Siphon: full-strength hits restore 2 mana to your worn jewelry." */
    public static void appendSiphon(List<Component> tooltip, int mana) {
        tooltip.add(Component.translatable(LangUtil.tooltip("set_piece.siphon"), mana)
                .withStyle(ChatFormatting.GOLD));
    }

    /** "Resonance: +10% effect to the spells on this set's jewelry." */
    public static void appendResonance(List<Component> tooltip, double perPiece) {
        tooltip.add(Component.translatable(LangUtil.tooltip("set_piece.resonance"), Math.round(perPiece * 100))
                .withStyle(ChatFormatting.GOLD));
    }
}
