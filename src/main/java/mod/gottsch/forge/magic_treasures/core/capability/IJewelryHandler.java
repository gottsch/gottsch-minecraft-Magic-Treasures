/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.magic_treasures.core.capability;

import mod.gottsch.forge.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryStats;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.forge.magic_treasures.core.spell.SpellEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * A write-through view of a jewelry stack. What the piece <em>is</em> (its {@link JewelryIdentity}) and its
 * computed stats are read-only here; play state (uses, mana, repairs, recharges, spells) and the gem can be changed,
 * and every change is written straight back to the stack. Get one with {@code JewelryHandler.get(stack)}.
 *
 * Created by Mark Gottschling on 6/1/2023
 */
public interface IJewelryHandler {
    /** the jewelry stack this handler views */
    ItemStack getStack();

    /*
     * what the piece is
     */
    JewelryIdentity getIdentity();

    /** the jewelry type: {@code "ring"}, {@code "necklace"}, {@code "bracelet"} */
    String getType();

    /** the {@code magictreasures:jewelry_material} id */
    ResourceLocation getMaterial();

    /** the {@code magictreasures:jewelry_style} id */
    ResourceLocation getStyle();

    /** the {@code magictreasures:gemstone} id, if the piece has a gem (see {@link #getStone()} for its item) */
    Optional<ResourceLocation> getGem();

    /** the {@code magictreasures:jewelry_preset} id, for named pieces */
    Optional<ResourceLocation> getPreset();

    /** max values, level, factors and rarity, computed from the identity and the datapack registries */
    JewelryStats getStats();

    MagicTreasuresRarity getRarity();

    /*
     * computed values
     */
    int getMaxUses();

    double getMaxMana();

    int getMaxRepairs();

    int getMaxRecharges();

    int getMaxLevel();

    double getSpellCostFactor();

    double getSpellEffectAmountFactor();

    double getSpellDurationFactor();

    double getSpellCooldownFactor();

    double getSpellRangeFactor();

    double modifySpellCost(double cost);

    double modifyEffectAmount(double amount);

    int modifyDuration(int duration);

    long modifyCooldown(long cooldown);

    double modifyRange(double range);

    /*
     * play state
     */
    int getUses();

    void setUses(int uses);

    /** infinite pieces never lose uses (a preset's {@code "infinite": true}) */
    boolean isInfinite();

    double getMana();

    void setMana(double mana);

    int getRepairs();

    void setRepairs(int repairs);

    int getRecharges();

    void setRecharges(int recharges);

    /**
     * @return an unmodifiable list of spell entities bound to this stack. Use addSpell/setSpells to change it.
     */
    List<SpellEntity> getSpells();

    void setSpells(List<SpellEntity> spells);

    void addSpell(SpellEntity spell);

    /*
     * the gem
     */

    /** the gem's <em>item</em> id, or null without a gem (a pack's gemstone id can differ from its item's) */
    ResourceLocation getStone();

    /** sets the gem by its item id; null or {@code minecraft:air} removes it; an item that isn't a gemstone is ignored */
    void setStone(ResourceLocation item);

    boolean hasStone();

    /** whether the anvil may set this item as the gem (anything that isn't a gemstone is accepted) */
    boolean acceptsAffixer(ItemStack stack);

    /** false when a preset says {@code "accepts_gems": false}: no gem may be set or removed with the anvil */
    boolean presetAcceptsGems();

    /** false when a preset says {@code "extractable": false}: the pliers can't take its gem out */
    boolean gemExtractable();

    /*
     * tooltip
     */
    void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flag);

    /** The tooltip header's second line: type, gem and max spell level. */
    Component getSubtitle();
}
