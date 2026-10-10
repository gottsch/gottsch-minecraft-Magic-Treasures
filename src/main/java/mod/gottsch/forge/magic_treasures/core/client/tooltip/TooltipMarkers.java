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

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder tooltip lines for things text can't draw (divider lines, bars, chips, grids, a header subtitle).
 * {@code appendHoverText} adds them like any other line, so it stays common code and other mods' lines are untouched;
 * on the client, {@link RichTooltips#apply} swaps each one for a drawn component. Safe to call on either side.
 * <p>
 * A marker is a translatable component whose key is {@code <namespace>.tooltip_marker.<type>}, carrying its data as
 * the translation args. The keys have no translations: a marker that is never swapped shows as its key.
 */
public final class TooltipMarkers {
    public static final String DIVIDER = "divider";
    public static final String SUBTITLE = "subtitle";
    public static final String BAR = "bar";
    public static final String CHIPS = "chips";
    public static final String GRID = "grid";
    public static final String SPACE = "space";

    private static final String KEY_INFIX = ".tooltip_marker.";

    private TooltipMarkers() {}

    /** Empty vertical space, smaller than a blank line. */
    public static Component space(String namespace, int pixels) {
        return marker(namespace, SPACE, pixels);
    }

    /** A drawn 1px line across the tooltip. */
    public static Component divider(String namespace) {
        return marker(namespace, DIVIDER);
    }

    /** A second line under the header's title; a plain text line when there is no icon header. */
    public static Component subtitle(String namespace, Component text) {
        return marker(namespace, SUBTITLE, text);
    }

    /**
     * A labelled bar across the tooltip: label on the left, value on the right, and a bar under them filled to
     * {@code fraction} (clamped to 0..1) in {@code rgb}.
     */
    public static Component bar(String namespace, Component label, Component value, double fraction, int rgb) {
        return marker(namespace, BAR, label, value, Math.max(0.0, Math.min(1.0, fraction)), rgb);
    }

    /** A row of small boxed labels, wrapping onto more rows at the tooltip's width cap. */
    public static Component chips(String namespace, List<? extends Component> chips) {
        return marker(namespace, CHIPS, chips.toArray());
    }

    /** Cells laid out left to right in {@code columns} equal-width columns. */
    public static Component grid(String namespace, int columns, List<? extends Component> cells) {
        List<Object> args = new ArrayList<>(cells.size() + 1);
        args.add(columns);
        args.addAll(cells);
        return marker(namespace, GRID, args.toArray());
    }

    /** The marker type of {@code text}, or null if it isn't one of {@code namespace}'s markers. */
    public static String typeOf(FormattedText text, String namespace) {
        if (text instanceof Component component && component.getContents() instanceof TranslatableContents contents) {
            String prefix = namespace + KEY_INFIX;
            if (contents.getKey().startsWith(prefix)) {
                return contents.getKey().substring(prefix.length());
            }
        }
        return null;
    }

    /** The data a marker carries; only call it on text {@link #typeOf} recognised. */
    public static Object[] argsOf(FormattedText text) {
        return ((TranslatableContents) ((Component) text).getContents()).getArgs();
    }

    private static Component marker(String namespace, String type, Object... args) {
        return Component.translatable(namespace + KEY_INFIX + type, args);
    }
}
