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
package mod.gottsch.forge.magic_treasures.core.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.List;

/**
 * Small boxed labels laid out left to right, wrapping onto a new row at the width cap.
 */
@OnlyIn(Dist.CLIENT)
public class ClientChipsTooltip implements ClientTooltipComponent {
    /** text inset from the border, left and right */
    private static final int PAD = 3;
    private static final int GAP = 3;
    private static final int CHIP_HEIGHT = 11;
    private static final int ROW_HEIGHT = CHIP_HEIGHT + 2;
    private static final int BORDER_ARGB = 0xFF4A4A4A;
    private static final int FILL_ARGB = 0x40000000;

    private final FormattedCharSequence[] texts;
    /** per chip: x, y, width */
    private final int[][] boxes;
    private final int width;
    private final int height;

    public ClientChipsTooltip(List<Component> chips, int maxWidth) {
        Font font = Minecraft.getInstance().font;
        int limit = maxWidth > 0 ? maxWidth : Integer.MAX_VALUE;
        texts = new FormattedCharSequence[chips.size()];
        boxes = new int[chips.size()][];
        int x = 0;
        int y = 0;
        int widest = 0;
        for (int i = 0; i < chips.size(); i++) {
            texts[i] = chips.get(i).getVisualOrderText();
            int chipWidth = font.width(texts[i]) + PAD * 2;
            if (x > 0 && x + chipWidth > limit) {
                x = 0;
                y += ROW_HEIGHT;
            }
            boxes[i] = new int[] {x, y, chipWidth};
            widest = Math.max(widest, x + chipWidth);
            x += chipWidth + GAP;
        }
        width = widest;
        height = chips.isEmpty() ? 0 : y + ROW_HEIGHT;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth(Font font) {
        return width;
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        for (int i = 0; i < texts.length; i++) {
            font.drawInBatch(texts[i], x + boxes[i][0] + PAD, y + boxes[i][1] + 2, -1, true, matrix, buffer,
                    Font.DisplayMode.NORMAL, 0, TooltipDrawing.LIGHT);
        }
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        for (int[] box : boxes) {
            int left = x + box[0];
            int top = y + box[1];
            guiGraphics.fill(left + 1, top + 1, left + box[2] - 1, top + CHIP_HEIGHT - 1, FILL_ARGB);
            TooltipDrawing.outline(guiGraphics, left, top, box[2], CHIP_HEIGHT, BORDER_ARGB);
        }
    }
}
