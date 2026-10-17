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

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * Draws an {@link IconTitleTooltip}: the item's icon at the given scale, with the title vertically
 * centered beside it.
 *
 * @author Mark Gottschling on 10/9/2026
 */
@OnlyIn(Dist.CLIENT)
public class ClientIconTitleTooltip implements ClientTooltipComponent {
    private static final int ICON_PX = 16;
    private static final int GAP = 4;
    /** full-bright, as vanilla draws tooltip text */
    private static final int LIGHT = 0xF000F0;

    private final IconTitleTooltip tooltip;
    private final FormattedCharSequence title;
    private final int iconSize;

    public ClientIconTitleTooltip(IconTitleTooltip tooltip) {
        this.tooltip = tooltip;
        this.title = Language.getInstance().getVisualOrder(tooltip.title());
        this.iconSize = Math.round(ICON_PX * tooltip.scale());
    }

    @Override
    public int getHeight() {
        // +2 matches the spacing vanilla leaves under a text line
        return iconSize + 2;
    }

    @Override
    public int getWidth(Font font) {
        return iconSize + GAP + font.width(title);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        int textY = y + (iconSize - font.lineHeight) / 2 + 1;
        font.drawInBatch(title, x + iconSize + GAP, textY, -1, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, LIGHT);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(tooltip.scale(), tooltip.scale(), 1.0F);
        guiGraphics.renderItem(tooltip.stack(), 0, 0);
        guiGraphics.pose().popPose();
    }
}
