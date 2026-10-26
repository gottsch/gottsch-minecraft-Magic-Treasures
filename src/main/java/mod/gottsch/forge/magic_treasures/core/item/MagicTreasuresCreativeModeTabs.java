/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2024 Mark Gottschling (gottsch)
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

import mod.gottsch.forge.magic_treasures.core.lore.LoreBookDef;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.setup.Registration;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.LegacyConversion;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 
 * @author Mark Gottschling Jul 11, 2024
 *
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MagicTreasuresCreativeModeTabs {

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicTreasures.MOD_ID);
	
	public static final RegistryObject<CreativeModeTab> MOD_TAB = TABS.register("magic_treasures_tab",
			() -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.magictreasures"))
			.icon(MagicTreasuresItems.MAGIC_TREASURES_TAB.get()::getDefaultInstance)
			.displayItems((displayParams, output) -> {
				// add all items
				Registration.ITEMS.getEntries().forEach(item -> {
				// the generic jewelry items are listed per combination below, never bare
				// M9: the 418 1.x jewelry items stay registered (old stacks must load) but leave the tab; generic ones replace them
				if (!item.equals(MagicTreasuresItems.MAGIC_TREASURES_TAB) && !MagicTreasuresItems.GENERIC_JEWELRY.contains(item)
						&& !item.equals(MagicTreasuresItems.LORE_BOOK)
						&& !item.equals(MagicTreasuresItems.SPECTRAL_SWORD)
						&& !LegacyConversion.isLegacy(item.get())) {
						output.accept(item.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
					}
				});
				// 2.0 generic jewelry, from the registries the client has
				JewelryCreativeStacks.build(displayParams.holders()).forEach(entry -> output.accept(entry.stack(), entry.visibility()));
				// one lore book per story, from the registry the client has
				displayParams.holders().lookup(MagicTreasuresRegistries.LORE_BOOK).ifPresent(books -> books.listElements()
						.filter(book -> LoreBook.enabled(book.key().location(), displayParams.holders()))
						.sorted(java.util.Comparator.comparing((net.minecraft.core.Holder.Reference<LoreBookDef> book) -> book.value().rarity().getCode())
								.thenComparing(book -> book.key().location().toString()))
						.forEach(book -> output.accept(LoreBook.of(MagicTreasuresItems.LORE_BOOK.get(), book.key().location()),
								TabVisibility.PARENT_AND_SEARCH_TABS)));
			})
			.build()
			);

}
