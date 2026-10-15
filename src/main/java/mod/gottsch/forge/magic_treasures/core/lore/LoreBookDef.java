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
package mod.gottsch.forge.magic_treasures.core.lore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.DataCodecs;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A lore book ({@code magictreasures:lore_book}): one of the stories, found in chests. The entry holds only what the
 * game needs to place it; the words are lang keys named after the entry's id, so every book translates:
 * <pre>
 * lore_book.&lt;namespace&gt;.&lt;path&gt;.title
 * lore_book.&lt;namespace&gt;.&lt;path&gt;.author
 * lore_book.&lt;namespace&gt;.&lt;path&gt;.page.1 ... .page.&lt;pages&gt;
 * </pre>
 *
 * @param rarity    which chests can give it (exactly this rarity's)
 * @param pages     how many page keys it has
 * @param character whose story it is ({@code silbro}, {@code salandaar}, {@code maldritch}, {@code selene}), for
 *                  per-character advancements; empty for the rest
 * @param texture   its icon, an item texture ({@code magictreasures:item/lore_book/old_binder}); when empty, the
 *                  icon is {@code <namespace>:item/lore_book/<path>}, and the shared default if that doesn't exist
 * @param disabled  a pack can switch a built-in book off
 *
 * @author Mark Gottschling on 10/14/2026
 */
public record LoreBookDef(MagicTreasuresRarity rarity, int pages, Optional<String> character,
                          Optional<ResourceLocation> texture, boolean disabled) {

    public static final Codec<LoreBookDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DataCodecs.RARITY.fieldOf("rarity").forGetter(LoreBookDef::rarity),
            Codec.intRange(1, 50).fieldOf("pages").forGetter(LoreBookDef::pages),
            Codec.STRING.optionalFieldOf("character").forGetter(LoreBookDef::character),
            ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(LoreBookDef::texture),
            Codec.BOOL.optionalFieldOf("disabled", false).forGetter(LoreBookDef::disabled)
    ).apply(instance, LoreBookDef::new));

    public static String titleKey(ResourceLocation id) {
        return key(id, "title");
    }

    public static String authorKey(ResourceLocation id) {
        return key(id, "author");
    }

    /** the book's pages, as translatable text; {@code page} counts from 1, as the keys do */
    public List<Component> pageText(ResourceLocation id) {
        List<Component> text = new ArrayList<>(pages);
        for (int page = 1; page <= pages; page++) {
            text.add(Component.translatable(key(id, "page." + page)));
        }
        return text;
    }

    private static String key(ResourceLocation id, String part) {
        return "lore_book." + id.getNamespace() + "." + id.getPath() + "." + part;
    }
}
