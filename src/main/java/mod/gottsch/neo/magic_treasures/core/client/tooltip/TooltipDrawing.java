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

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Small drawing helpers shared by the tooltip components.
 */
@OnlyIn(Dist.CLIENT)
final class TooltipDrawing {
    /** full-bright, as vanilla draws tooltip text */
    static final int LIGHT = 0xF000F0;

    private TooltipDrawing() {}

    /** A 1px rectangle outline. */
    static void outline(GuiGraphics guiGraphics, int x, int y, int width, int height, int argb) {
        guiGraphics.fill(x, y, x + width, y + 1, argb);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, argb);
        guiGraphics.fill(x, y + 1, x + 1, y + height - 1, argb);
        guiGraphics.fill(x + width - 1, y + 1, x + width, y + height - 1, argb);
    }
}
