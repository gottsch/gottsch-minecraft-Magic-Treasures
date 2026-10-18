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
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Client side of {@link TooltipMarkers}: swaps marker lines for drawn components, and stretches the full-width ones
 * to the tooltip's width just before it is drawn.
 */
@OnlyIn(Dist.CLIENT)
public final class RichTooltips {
    private RichTooltips() {}

    /** Call from {@code RegisterClientTooltipComponentFactoriesEvent}. */
    public static void registerFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(RenderedTooltip.class, RenderedTooltip::component);
    }

    /**
     * Replaces {@code namespace}'s markers with drawn components. If there are any, text lines also wrap at
     * {@code maxWidth} (0 for no cap), and chips wrap there too. Run it after {@link IconTitleTooltips#apply}, so a
     * subtitle can join the icon header.
     */
    public static void apply(RenderTooltipEvent.GatherComponents event, String namespace, int maxWidth) {
        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        boolean found = false;
        for (int i = 0; i < elements.size(); i++) {
            Optional<FormattedText> text = elements.get(i).left();
            String type = text.map(t -> TooltipMarkers.typeOf(t, namespace)).orElse(null);
            if (type == null) {
                continue;
            }
            found = true;
            Object[] args = TooltipMarkers.argsOf(text.get());
            if (TooltipMarkers.SUBTITLE.equals(type)) {
                Component subtitle = (Component) args[0];
                if (elements.get(0).right().orElse(null) instanceof IconTitleTooltip header) {
                    elements.set(0, Either.right(header.withSubtitle(subtitle)));
                    elements.remove(i--);
                } else {
                    elements.set(i, Either.left(subtitle));
                }
                continue;
            }
            ClientTooltipComponent component = create(type, args, maxWidth);
            if (component == null) {
                elements.remove(i--);
            } else {
                elements.set(i, Either.right(new RenderedTooltip(component)));
            }
        }
        if (found && maxWidth > 0) {
            event.setMaxWidth(maxWidth);
        }
    }

    /**
     * Like {@link #apply}, but a tooltip taller than the screen wraps wider, {@code step} pixels at a time, until it
     * fits or reaches the room beside the mouse. A wider tooltip is shorter, so it keeps its top on screen.
     */
    public static void applyFitting(RenderTooltipEvent.GatherComponents event, String namespace, int maxWidth, int step) {
        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        List<Either<FormattedText, TooltipComponent>> gathered = new ArrayList<>(elements);
        apply(event, namespace, maxWidth);
        if (event.getMaxWidth() != maxWidth) {
            return; // no markers: not one of ours
        }
        int room = roomBesideMouse(event.getScreenWidth());
        int width = maxWidth;
        while (width < room && !IconTitleTooltips.fitsScreen(event)) {
            width = Math.min(width + step, room);
            // chips wrap when they are made, so make everything again at the new width
            elements.clear();
            elements.addAll(gathered);
            apply(event, namespace, width);
        }
    }

    /**
     * The widest a tooltip's text can be where the mouse is now. A tooltip too wide for either side of the mouse
     * wraps to the room on one side, chosen as Forge does ({@code ForgeHooksClient.gatherTooltipComponents}).
     */
    private static int roomBesideMouse(int screenWidth) {
        Minecraft minecraft = Minecraft.getInstance();
        int mouseX = (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth()
                / minecraft.getWindow().getScreenWidth());
        return mouseX > screenWidth / 2 ? mouseX - 12 - 8 : screenWidth - 16 - mouseX;
    }

    /** Call from {@code RenderTooltipEvent.Pre}: widens every {@link FullWidthTooltip} to the widest other component. */
    public static void stretch(RenderTooltipEvent.Pre event) {
        int width = 0;
        boolean any = false;
        for (ClientTooltipComponent component : event.getComponents()) {
            if (component instanceof FullWidthTooltip) {
                any = true;
            } else {
                width = Math.max(width, component.getWidth(event.getFont()));
            }
        }
        if (!any) {
            return;
        }
        List<ClientBarTooltip> bars = new ArrayList<>();
        for (ClientTooltipComponent component : event.getComponents()) {
            if (component instanceof ClientBarTooltip bar) {
                bars.add(bar);
            }
        }
        ClientBarTooltip.align(bars, event.getFont());
        // the aligned bars may now need more room than the rest of the tooltip
        for (ClientBarTooltip bar : bars) {
            width = Math.max(width, bar.getWidth(event.getFont()));
        }
        for (ClientTooltipComponent component : event.getComponents()) {
            if (component instanceof FullWidthTooltip fullWidth) {
                fullWidth.setTooltipWidth(width);
            }
        }
        // a text line is 1px shorter than a bar row, so a divider under text gets that pixel back: every divider
        // then has the same gap above it
        List<ClientTooltipComponent> components = event.getComponents();
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof ClientDividerTooltip divider) {
                divider.setPadTop(i > 0 && components.get(i - 1) instanceof ClientTextTooltip ? 1 : 0);
            }
        }
    }

    /**
     * Call from {@code RenderTooltipEvent.Pre}: a rich tooltip that would rest on the bottom of the screen is lifted
     * so {@code margin} pixels show under it, which reads as "nothing cut off". Vanilla clamps it to 3.
     */
    public static void lift(RenderTooltipEvent.Pre event, int margin) {
        if (event.getComponents().stream().noneMatch(component -> component instanceof FullWidthTooltip)) {
            return;
        }
        // the height vanilla gives the tooltip's contents
        int height = event.getComponents().size() == 1 ? -2 : 0;
        for (ClientTooltipComponent component : event.getComponents()) {
            height += component.getHeight();
        }
        // vanilla draws the tooltip at mouse y - 12, then pulls it up to end 3 pixels above the bottom
        int lowest = event.getScreenHeight() - height - 3 - margin + 12;
        if (event.getY() > lowest) {
            event.setY(Math.max(lowest, 4 + 12));
        }
    }

    /**
     * Call from {@code RenderTooltipEvent.Pre}: a rich tooltip that just fits right of the mouse is moved left so its
     * border shows too. Vanilla keeps it there while {@code x + width <= screen width}, but the border reaches
     * {@code border} pixels further, past the edge.
     */
    public static void keepBorderOnScreen(RenderTooltipEvent.Pre event, int border) {
        int width = 0;
        boolean ours = false;
        for (ClientTooltipComponent component : event.getComponents()) {
            ours |= component instanceof FullWidthTooltip;
            width = Math.max(width, component.getWidth(event.getFont()));
        }
        if (!ours) {
            return;
        }
        // vanilla draws the tooltip at mouse x + 12, and flips it left of the mouse only if it overflows
        int right = event.getX() + 12 + width;
        if (right <= event.getScreenWidth() && right + border > event.getScreenWidth()) {
            event.setX(event.getScreenWidth() - border - width - 12);
        }
    }

    private static ClientTooltipComponent create(String type, Object[] args, int maxWidth) {
        return switch (type) {
            case TooltipMarkers.DIVIDER -> new ClientDividerTooltip();
            case TooltipMarkers.SPACE -> new ClientSpaceTooltip(((Number) args[0]).intValue());
            case TooltipMarkers.BAR -> new ClientBarTooltip((Component) args[0], (Component) args[1],
                    ((Number) args[2]).doubleValue(), ((Number) args[3]).intValue());
            case TooltipMarkers.CHIPS -> new ClientChipsTooltip(components(args, 0), maxWidth);
            case TooltipMarkers.GRID -> new ClientGridTooltip(((Number) args[0]).intValue(), components(args, 1));
            default -> null;
        };
    }

    private static List<Component> components(Object[] args, int from) {
        List<Component> list = new ArrayList<>(args.length - from);
        for (int i = from; i < args.length; i++) {
            list.add((Component) args[i]);
        }
        return list;
    }
}
