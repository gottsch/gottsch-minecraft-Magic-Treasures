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
package mod.gottsch.forge.magic_treasures.core.item;

import mod.gottsch.forge.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.forge.magic_treasures.core.client.LoreBookClient;
import mod.gottsch.forge.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.forge.gottschcore.nbt.StackData;
import mod.gottsch.forge.magic_treasures.core.lore.LoreBookDef;
import mod.gottsch.forge.magic_treasures.core.lore.LoreBookReading;
import net.minecraft.server.level.ServerPlayer;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * One item for every lore book. Which story it is sits on the stack ({@code magictreasures:lore_book}, the book's id
 * in the {@code lore_book} registry); the title, author and pages are lang keys named after that id (see
 * {@link LoreBookDef}). Right-click opens the vanilla book screen, read-only: the text can't be edited or copied.
 *
 * @author Mark Gottschling on 10/14/2026
 */
public class LoreBook extends Item {

    public LoreBook(Properties properties) {
        super(properties.stacksTo(1));
    }

    /** a book of {@code id} */
    public static ItemStack of(Item item, ResourceLocation id) {
        ItemStack stack = new ItemStack(item);
        StackData.set(stack, MagicTreasuresDataComponents.LORE_BOOK, id);
        return stack;
    }

    public static Optional<ResourceLocation> getId(ItemStack stack) {
        return Optional.ofNullable(StackData.get(stack, MagicTreasuresDataComponents.LORE_BOOK));
    }

    /** the book's entry, from the registries {@code holders} (the level's, or the creative tab's) */
    public static Optional<LoreBookDef> getDef(ItemStack stack, HolderLookup.Provider holders) {
        return getId(stack).flatMap(id -> holders.lookup(MagicTreasuresRegistries.LORE_BOOK)
                .flatMap(books -> books.get(ResourceKey.create(MagicTreasuresRegistries.LORE_BOOK, id))))
                .map(Holder::value);
    }

    @Override
    public Component getName(ItemStack stack) {
        return getId(stack).<Component>map(id -> Component.translatable(LoreBookDef.titleKey(id)))
                .orElseGet(() -> super.getName(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        getId(stack).ifPresent(id -> tooltip.add(Component.translatable(LangUtil.tooltip("lore_book.author"),
                Component.translatable(LoreBookDef.authorKey(id))).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getId(stack).isEmpty()) {
            return InteractionResultHolder.pass(stack);
        }
        if (level.isClientSide()) {
            // only the client has a screen; this branch never runs on a dedicated server, so the class never loads there
            ResourceLocation id = getId(stack).get();
            getDef(stack, level.registryAccess()).ifPresent(def -> LoreBookClient.open(id, stack, def));
        } else if (player instanceof ServerPlayer serverPlayer && getDef(stack, level.registryAccess()).isPresent()) {
            LoreBookReading.markRead(serverPlayer, getId(stack).get());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
