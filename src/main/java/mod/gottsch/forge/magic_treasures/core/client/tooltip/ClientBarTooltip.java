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
 * One line: a label, a short bar filled to a fraction, and a value on the right. Spans the tooltip; consecutive
 * bars share label and value columns ({@link #align}) so their bars line up.
 */
@OnlyIn(Dist.CLIENT)
public class ClientBarTooltip implements ClientTooltipComponent, FullWidthTooltip {
    /** space between the bar and the text on either side */
    private static final int GAP = 6;
    private static final int MIN_BAR_WIDTH = 24;
    private static final int BAR_HEIGHT = 3;
    private static final int LINE_HEIGHT = 11;
    private static final int TRACK_ARGB = 0xFF2B2B2B;

    private final FormattedCharSequence label;
    private final FormattedCharSequence value;
    private final double fraction;
    private final int rgb;
    private int tooltipWidth;
    private int labelColumn;
    private int valueColumn;

    public ClientBarTooltip(Component label, Component value, double fraction, int rgb) {
        this.label = label.getVisualOrderText();
        this.value = value.getVisualOrderText();
        this.fraction = fraction;
        this.rgb = rgb;
    }

    /** Give every bar the widest label and value, so the bars start and end at the same x. */
    public static void align(List<ClientBarTooltip> bars, Font font) {
        int labelColumn = 0;
        int valueColumn = 0;
        for (ClientBarTooltip bar : bars) {
            labelColumn = Math.max(labelColumn, font.width(bar.label));
            valueColumn = Math.max(valueColumn, font.width(bar.value));
        }
        for (ClientBarTooltip bar : bars) {
            bar.labelColumn = labelColumn;
            bar.valueColumn = valueColumn;
        }
    }

    @Override
    public void setTooltipWidth(int width) {
        this.tooltipWidth = width;
    }

    @Override
    public int getHeight() {
        return LINE_HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        int columns = Math.max(labelColumn, font.width(label)) + Math.max(valueColumn, font.width(value));
        return Math.max(tooltipWidth, columns + GAP * 2 + MIN_BAR_WIDTH);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        font.drawInBatch(label, x, y, -1, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, TooltipDrawing.LIGHT);
        int valueX = x + getWidth(font) - font.width(value);
        font.drawInBatch(value, valueX, y, -1, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, TooltipDrawing.LIGHT);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int left = x + Math.max(labelColumn, font.width(label)) + GAP;
        int right = x + getWidth(font) - Math.max(valueColumn, font.width(value)) - GAP;
        // centred on the text's x-height
        int top = y + 3;
        guiGraphics.fill(left, top, right, top + BAR_HEIGHT, TRACK_ARGB);
        int filled = (int) Math.round((right - left) * fraction);
        if (filled > 0) {
            guiGraphics.fill(left, top, left + filled, top + BAR_HEIGHT, 0xFF000000 | rgb);
        }
    }
}
