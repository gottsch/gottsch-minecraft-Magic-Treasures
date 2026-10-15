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
package mod.gottsch.forge.magic_treasures.core.event;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.set.BoundPossessionsBonus;
import mod.gottsch.forge.magic_treasures.core.set.JewelrySet;
import mod.gottsch.forge.magic_treasures.core.set.JewelrySets;
import mod.gottsch.forge.magic_treasures.core.set.SetEquipment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Bound possessions (Maldritch's Remains, full set): the set's worn pieces stay with a player who dies wearing it.
 * <p>
 * The pieces in the vanilla inventory (hands, armor, hotbar jewelry) are taken out on death, before anything drops,
 * and given back on respawn. They're kept in the dead player's persistent data, so a logout on the death screen
 * doesn't lose them. Pieces in Curios slots stay there through {@link CuriosBoundPossessions}. Grave mods only see the
 * drops, so they keep working for everything else.
 *
 * @author Mark Gottschling on 10/13/2026
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BoundPossessionsHandler {
    /** on a player who died with the bonus on: the pieces taken out of the inventory, each with its slot */
    public static final String KEY = MagicTreasures.MOD_ID + ":bound_possessions";

    /** lowest, and skipped if cancelled: only a death that really happens takes the pieces */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
                || !SetEquipment.isActive(player, BoundPossessionsBonus.class)) {
            return;
        }
        JewelrySet set = JewelrySets.MALDRITCHS_REMAINS;
        Inventory inventory = player.getInventory();
        ListTag bound = new ListTag();
        for (ItemStack stack : SetEquipment.getWornItems(player)) {
            if (!set.contains(stack)) {
                continue;
            }
            // Curios stacks aren't in the inventory, so they're never found here
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                if (inventory.getItem(slot) == stack) {
                    CompoundTag entry = new CompoundTag();
                    entry.putInt("slot", slot);
                    entry.put("stack", stack.save(new CompoundTag()));
                    bound.add(entry);
                    inventory.setItem(slot, ItemStack.EMPTY);
                    break;
                }
            }
        }
        // written even when empty: Curios reads it to keep the set's pieces in its slots
        player.getPersistentData().put(KEY, bound);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        CompoundTag data = event.getOriginal().getPersistentData();
        if (!event.isWasDeath() || !data.contains(KEY, Tag.TAG_LIST)) {
            return;
        }
        ListTag bound = data.getList(KEY, Tag.TAG_COMPOUND);
        data.remove(KEY);
        Player player = event.getEntity();
        Inventory inventory = player.getInventory();
        for (int i = 0; i < bound.size(); i++) {
            CompoundTag entry = bound.getCompound(i);
            ItemStack stack = ItemStack.of(entry.getCompound("stack"));
            if (stack.isEmpty()) {
                continue;
            }
            // back in its own slot (armor goes back on); if something took the slot, wherever it fits
            int slot = entry.getInt("slot");
            if (slot < inventory.getContainerSize() && inventory.getItem(slot).isEmpty()) {
                inventory.setItem(slot, stack);
            } else if (!inventory.add(stack)) {
                player.drop(stack, false);
            }
        }
    }
}
