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
package mod.gottsch.neo.magic_treasures.core.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.List;

/**
 * Text cells in equal-width columns, filled left to right.
 */
@OnlyIn(Dist.CLIENT)
public class ClientGridTooltip implements ClientTooltipComponent {
    private static final int COLUMN_GAP = 10;
    private static final int ROW_HEIGHT = 10;

    private final FormattedCharSequence[] cells;
    private final int columns;
    private final int columnWidth;

    public ClientGridTooltip(int columns, List<Component> cells) {
        Font font = Minecraft.getInstance().font;
        this.columns = Math.max(1, columns);
        this.cells = new FormattedCharSequence[cells.size()];
        int widest = 0;
        for (int i = 0; i < cells.size(); i++) {
            this.cells[i] = cells.get(i).getVisualOrderText();
            widest = Math.max(widest, font.width(this.cells[i]));
        }
        this.columnWidth = widest;
    }

    @Override
    public int getHeight() {
        int rows = (cells.length + columns - 1) / columns;
        return rows * ROW_HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        int used = Math.min(columns, cells.length);
        return used == 0 ? 0 : used * columnWidth + (used - 1) * COLUMN_GAP;
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        for (int i = 0; i < cells.length; i++) {
            int cellX = x + (i % columns) * (columnWidth + COLUMN_GAP);
            int cellY = y + (i / columns) * ROW_HEIGHT;
            font.drawInBatch(cells[i], cellX, cellY, -1, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, TooltipDrawing.LIGHT);
        }
    }
}
