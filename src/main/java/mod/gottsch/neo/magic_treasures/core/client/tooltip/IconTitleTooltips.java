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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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

    /**
     * Gives the icon header back its plain title (and the subtitle as the next line) when the header would make the
     * tooltip taller than the screen. Vanilla moves a tall tooltip up to {@code screen height - height - 3} with no
     * floor, so one taller than the screen starts above the top and loses its title; the plain header is shorter.
     * Call last in {@code GatherComponents}, after markers became components ({@link RichTooltips#apply}).
     * <p>
     * The height is added up as vanilla will (text lines wrapped at the event's max width, 10px each; components by
     * their own height). Vanilla may wrap narrower near a screen edge, which only adds lines, so near an edge this can
     * still miss by a line or two.
     */
    public static void fitToScreen(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        if (elements.isEmpty() || !(elements.get(0).right().orElse(null) instanceof IconTitleTooltip header)) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        int wrap = event.getMaxWidth() > 0 ? event.getMaxWidth() : Integer.MAX_VALUE;
        int height = elements.size() == 1 ? -2 : 0;
        for (Either<FormattedText, TooltipComponent> element : elements) {
            height += element.map(
                    text -> Math.max(1, font.split(text, wrap).size()) * font.lineHeight,
                    component -> heightOf(component, font));
        }
        // the background reaches 3px past the contents on both sides
        if (height + 6 <= event.getScreenHeight()) {
            return;
        }
        elements.set(0, Either.left(header.title()));
        if (header.subtitle() != null) {
            elements.add(1, Either.left(header.subtitle()));
        }
    }

    private static int heightOf(TooltipComponent component, Font font) {
        if (component instanceof IconTitleTooltip header) {
            return new ClientIconTitleTooltip(header).getHeight();
        }
        if (component instanceof RenderedTooltip rendered) {
            return rendered.component().getHeight();
        }
        return font.lineHeight; // another mod's component: assume one line
    }
}
