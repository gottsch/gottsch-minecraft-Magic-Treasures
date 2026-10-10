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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A 1px line across the tooltip that fades out at both ends.
 */
@OnlyIn(Dist.CLIENT)
public class ClientDividerTooltip implements ClientTooltipComponent, FullWidthTooltip {
    /** space above and below the line */
    private static final int MARGIN = 3;
    /** a muted version of the vanilla tooltip border's purple */
    private static final int RGB = 0x5A3A8E;

    private int width;
    /** extra space above the line, to even out what sits above it */
    private int padTop;

    @Override
    public void setTooltipWidth(int width) {
        this.width = width;
    }

    public void setPadTop(int padTop) {
        this.padTop = padTop;
    }

    @Override
    public int getHeight() {
        return padTop + MARGIN * 2 + 1;
    }

    @Override
    public int getWidth(Font font) {
        return width;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        if (width <= 0) {
            return;
        }
        int lineY = y + padTop + MARGIN;
        int fade = Math.max(1, width / 6);
        // solid middle in one fill, then each faded end pixel by pixel
        guiGraphics.fill(x + fade, lineY, x + width - fade, lineY + 1, 0xFF000000 | RGB);
        for (int i = 0; i < fade && i < width - fade; i++) {
            int argb = ((255 * (i + 1) / (fade + 1)) << 24) | RGB;
            guiGraphics.fill(x + i, lineY, x + i + 1, lineY + 1, argb);
            guiGraphics.fill(x + width - 1 - i, lineY, x + width - i, lineY + 1, argb);
        }
    }
}
