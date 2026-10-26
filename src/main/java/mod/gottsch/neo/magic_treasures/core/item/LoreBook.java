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
package mod.gottsch.neo.magic_treasures.core.item;

import mod.gottsch.neo.gottschbooks.api.BookItem;
import mod.gottsch.neo.gottschbooks.api.Books;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.neo.magic_treasures.core.lore.LoreBookDef;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Optional;

/**
 * One item for every lore book. Which story it is sits on the stack (the {@code gottschbooks:book} component: the
 * book's id, the same in MT's {@code lore_book} registry and in Gottsch Books' {@code book} registry). The text is
 * {@code assets/magictreasures/books/<id>/<locale>/text.md}, read in Gottsch Books' reader; the item's name and
 * author are lang keys named after the id (see {@link LoreBookDef}). Right-click opens the reader (see
 * {@link BookItem}).
 *
 * @author Mark Gottschling on 10/14/2026
 */
public class LoreBook extends BookItem {

    public LoreBook(Properties properties) {
        super(properties);
    }

    /** a book of {@code id} */
    public static ItemStack of(Item item, ResourceLocation id) {
        return Books.of(item, id);
    }

    public static Optional<ResourceLocation> getId(ItemStack stack) {
        return Books.getId(stack);
    }

    /** the book's entry, from the registries {@code holders} (the level's, or the creative tab's) */
    public static Optional<LoreBookDef> getDef(ItemStack stack, HolderLookup.Provider holders) {
        return getId(stack).flatMap(id -> holders.lookup(MagicTreasuresRegistries.LORE_BOOK)
                .flatMap(books -> books.get(ResourceKey.create(MagicTreasuresRegistries.LORE_BOOK, id))))
                .map(Holder::value);
    }

    /**
     * Whether book {@code id} is in play: it has a {@code gottschbooks:book} entry that isn't disabled. That entry is
     * the one switch for a book: it takes it out of loot and the creative tab, and out of the "read them all" count.
     */
    public static boolean enabled(ResourceLocation id, HolderLookup.Provider holders) {
        return Books.getDef(id, holders).filter(def -> !def.disabled()).isPresent();
    }

    @Override
    public Component getName(ItemStack stack) {
        return getId(stack).<Component>map(id -> Component.translatable(LoreBookDef.titleKey(id)))
                .orElseGet(() -> super.getName(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        getId(stack).ifPresent(id -> tooltip.add(Component.translatable(LangUtil.tooltip("lore_book.author"),
                Component.translatable(LoreBookDef.authorKey(id))).withStyle(ChatFormatting.GRAY)));
    }
}
