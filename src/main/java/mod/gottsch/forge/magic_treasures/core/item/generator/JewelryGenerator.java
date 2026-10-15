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
package mod.gottsch.forge.magic_treasures.core.item.generator;

import mod.gottsch.forge.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.forge.magic_treasures.core.component.StackData;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.forge.magic_treasures.core.item.SpellScroll;
import mod.gottsch.forge.magic_treasures.core.spell.ISpell;
import mod.gottsch.forge.magic_treasures.core.tag.MagicTreasuresTags;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

/**
 * The anvil's jewelry results. Every result is a copy of the input with its identity or state changed; since M9 there
 * are no per-combination items to look up by name (the 1.x {@code Namer} path went with them).
 *
 * @author Mark Gottschling May 6, 2024
 */
public class JewelryGenerator {

    /*
     * The result is a copy of the input, and the gem is set on (or cleared from) its identity.
     * The max values follow from the identity, so there is no item to look up by name. Adding is allowed when the
     * stone is an enabled gemstone, the rules allow it on this material (the material's accepts_gems and gems limit,
     * the gem's affixes_to) and no preset forbids gems. As in 1.x, the gem's extra mana and recharges are added to
     * the current values; on removal the current values are capped at the new max.
     */
    public ItemStack addStone(ItemStack jewelry, ItemStack stone) {
        IJewelryHandler source = JewelryHandler.get(jewelry).orElse(null);
        if (source == null || stone == null || stone.isEmpty() || source.hasStone() || !source.acceptsAffixer(stone)) {
            return ItemStack.EMPTY;
        }
        ItemStack result = jewelry.copy();
        IJewelryHandler dest = JewelryHandler.get(result).orElseThrow(IllegalStateException::new);
        dest.setStone(ModUtil.getName(stone.getItem()));
        if (!dest.hasStone()) {
            return ItemStack.EMPTY; // not a known gemstone
        }
        dest.setMana(source.getMana() + (dest.getMaxMana() - source.getMaxMana()));
        dest.setRecharges(source.getRecharges() + (dest.getMaxRecharges() - source.getMaxRecharges()));
        return result;
    }

    public Optional<ItemStack> removeStone(ItemStack jewelry) {
        if (JewelryHandler.get(jewelry).filter(IJewelryHandler::presetAcceptsGems).isEmpty()) {
            return Optional.empty();
        }
        ItemStack result = jewelry.copy();
        Optional<IJewelryHandler> dest = JewelryHandler.get(result);
        dest.ifPresent(handler -> {
            handler.setStone(null);
            // reads are capped at the new max; store the capped values
            handler.setMana(handler.getMana());
            handler.setRecharges(handler.getRecharges());
        });
        return dest.map(handler -> result);
    }

    public Optional<ItemStack> addSpells(ItemStack jewelry, ItemStack spellStack) {
        if (!spellStack.is(MagicTreasuresTags.Items.SPELL_SCROLLS)) return Optional.empty();

        ItemStack destStack = jewelry.copy();

        IJewelryHandler handler = JewelryHandler.get(destStack).orElseThrow(IllegalStateException::new);
        ISpell spell = ((SpellScroll)spellStack.getItem()).getSpell();
        if (handler.getMaxLevel() >= spell.getLevel()) {
            handler.getSpells().add(spell.entity());
            return Optional.of(destStack);
        }
        return Optional.empty();
    }

    public Optional<ItemStack> recharge(ItemStack jewelry) {
        ItemStack destStack = jewelry.copy();

        IJewelryHandler handler = JewelryHandler.get(destStack).orElseThrow(IllegalStateException::new);
        if (handler.getRecharges() > 0) {
            handler.setMana(handler.getMaxMana());
            handler.setRecharges(handler.getRecharges() - 1);
            return Optional.of(destStack);
        }
        return Optional.empty();
    }

    public Optional<ItemStack> repair(ItemStack jewelry) {
        ItemStack destStack = jewelry.copy();

        IJewelryHandler handler = JewelryHandler.get(destStack).orElseThrow(IllegalStateException::new);
        // Tempered (Sal'andaar's full set): this repair uses the stamp instead of a repair
        if (StackData.has(destStack, MagicTreasuresDataComponents.TEMPERED)) {
            handler.setUses(handler.getMaxUses());
            StackData.remove(destStack, MagicTreasuresDataComponents.TEMPERED);
            return Optional.of(destStack);
        }
        if (handler.getRepairs() > 0) {
            handler.setUses(handler.getMaxUses());
            handler.setRepairs(Math.max(0, handler.getRepairs() - 1));
            return Optional.of(destStack);
        }
        return Optional.empty();
    }
}
