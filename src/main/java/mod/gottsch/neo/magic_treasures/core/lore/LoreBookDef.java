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
package mod.gottsch.neo.magic_treasures.core.lore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.DataCodecs;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * A lore book ({@code magictreasures:lore_book}): one of the stories, found in chests. The entry holds only what loot
 * and the icon need. The rest lives with Gottsch Books under the same id:
 * <ul>
 *     <li>the text: {@code assets/<namespace>/books/<path>/<locale>/text.md};</li>
 *     <li>the {@code gottschbooks:book} entry ({@code data/<namespace>/gottschbooks/book/<path>.json}), which every
 *     book needs, and whose {@code "disabled": true} switches the book off;</li>
 *     <li>the character's tag ({@code #magictreasures:books/selene}), for the per-character advancements.</li>
 * </ul>
 * The item's name and the tooltip's author are lang keys, so they translate:
 * <pre>
 * lore_book.&lt;namespace&gt;.&lt;path&gt;.title
 * lore_book.&lt;namespace&gt;.&lt;path&gt;.author
 * </pre>
 *
 * @param rarity  which chests can give it (exactly this rarity's)
 * @param texture its icon, an item texture ({@code magictreasures:item/lore_book/old_binder}); when empty, the
 *                icon is {@code <namespace>:item/lore_book/<path>}, and the shared default if that doesn't exist
 *
 * @author Mark Gottschling on 10/14/2026
 */
public record LoreBookDef(MagicTreasuresRarity rarity, Optional<ResourceLocation> texture) {

    public static final Codec<LoreBookDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DataCodecs.RARITY.fieldOf("rarity").forGetter(LoreBookDef::rarity),
            ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(LoreBookDef::texture)
    ).apply(instance, LoreBookDef::new));

    public static String titleKey(ResourceLocation id) {
        return key(id, "title");
    }

    public static String authorKey(ResourceLocation id) {
        return key(id, "author");
    }

    private static String key(ResourceLocation id, String part) {
        return "lore_book." + id.getNamespace() + "." + id.getPath() + "." + part;
    }
}
