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

import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Tooltip data for a header line that shows the item's icon beside its name. Drawn by
 * {@link ClientIconTitleTooltip}; added by {@link IconTitleTooltips#apply}.
 * <p>
 * Part of a small, mod-independent tooltip toolkit (nothing here imports Magic Treasures), meant to move
 * to GottschCore once the design settles.
 *
 * @param stack the item to draw
 * @param title the tooltip's original title line (keeps its rarity color and formatting)
 * @param subtitle a second line under the title, or null (see {@link TooltipMarkers#subtitle})
 * @param scale icon scale: 1 = 16px, 2 = 32px
 *
 * @author Mark Gottschling on 10/9/2026
 */
public record IconTitleTooltip(ItemStack stack, FormattedText title, FormattedText subtitle, float scale) implements TooltipComponent {

    public IconTitleTooltip(ItemStack stack, FormattedText title, float scale) {
        this(stack, title, null, scale);
    }

    public IconTitleTooltip withSubtitle(FormattedText subtitle) {
        return new IconTitleTooltip(stack, title, subtitle, scale);
    }
}
