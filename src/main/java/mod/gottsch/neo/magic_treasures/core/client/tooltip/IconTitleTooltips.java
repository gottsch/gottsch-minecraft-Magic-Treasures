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

import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;
import java.util.function.Predicate;

/**
 * Entry points for the icon title tooltip. A mod registers {@code IconTitleTooltip -> ClientIconTitleTooltip}
 * in {@code RegisterClientTooltipComponentFactoriesEvent} and calls {@link #apply} from a client
 * {@code RenderTooltipEvent.GatherComponents} handler.
 *
 * @author Mark Gottschling on 10/9/2026
 */
@OnlyIn(Dist.CLIENT)
public final class IconTitleTooltips {

    private IconTitleTooltips() {}

    /**
     * Replaces the tooltip's title line with an icon header when {@code filter} accepts the stack. Leaves the
     * tooltip alone if another mod has already replaced the title with something that isn't text.
     */
    public static void apply(RenderTooltipEvent.GatherComponents event, Predicate<ItemStack> filter, float scale) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !filter.test(stack)) {
            return;
        }
        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        if (elements.isEmpty()) {
            return;
        }
        elements.get(0).left().ifPresent(title ->
                elements.set(0, Either.right(new IconTitleTooltip(stack, title, scale))));
    }
}
