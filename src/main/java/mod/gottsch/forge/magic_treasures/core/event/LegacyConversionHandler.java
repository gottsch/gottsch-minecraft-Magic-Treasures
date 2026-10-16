package mod.gottsch.forge.magic_treasures.core.event;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.advancement.JewelryTrigger;
import mod.gottsch.forge.magic_treasures.core.advancement.MagicTreasuresCriteria;
import mod.gottsch.forge.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.forge.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.forge.gottschcore.nbt.StackData;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryStats;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.LegacyConversion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.TickEvent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * M9: converts 1.x jewelry to 2.0 generic jewelry ({@link LegacyConversion}) wherever a player has it:
 * <ul>
 *     <li>the player's inventory (main, armor, offhand) and every Curios slot, cosmetic ones included, once a second.
 *     A picked-up piece converts here too: the pickup event may not replace the item entity's stack;</li>
 *     <li>a container (chest, ender chest, shulker box…) when the player opens it.</li>
 * </ul>
 * Chests nobody opens keep their 1.x stacks: inert {@code LegacyJewelryItem}s that look as before until touched.
 * <p>
 * {@code Item#inventoryTick} isn't used: its slot number restarts at 0 in each inventory section, so it can't say
 * where to put the new stack.
 * <p>
 * The same scan reports each carried 2.0 piece to the {@code carry} advancement action (Lord's Ransom, Treasure
 * Hunter).
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID)
public final class LegacyConversionHandler {
    private static final int INTERVAL = 20;
    /** Curios is optional; its slots are only read through {@link CuriosSlots} when it's loaded */
    private static final boolean CURIOS = ModList.get().isLoaded("curios");

    private LegacyConversionHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)
                || player.tickCount % INTERVAL != 0) {
            return;
        }
        int converted = convert(player.getInventory());
        if (CURIOS) {
            converted += CuriosSlots.replaceEach(player, LegacyConversion::convert);
        }
        reportCarried(player);
        if (converted > 0) {
            MagicTreasures.LOGGER.info("converted {} 1.x jewelry item(s) carried by {}", converted, player.getGameProfile().getName());
        }
    }

    /** Reports every carried 2.0 piece, once per style and rarity, to the {@code carry} advancement action. */
    private static void reportCarried(ServerPlayer player) {
        Set<JewelryTrigger.Details> reported = new HashSet<>();
        Container inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            reportCarried(player, inventory.getItem(i), reported);
        }
        if (CURIOS) {
            CuriosSlots.forEach(player, stack -> reportCarried(player, stack, reported));
        }
    }

    private static void reportCarried(ServerPlayer player, ItemStack stack, Set<JewelryTrigger.Details> reported) {
        JewelryIdentity identity = StackData.get(stack, MagicTreasuresDataComponents.JEWELRY_IDENTITY);
        if (identity == null) {
            return;
        }
        JewelryTrigger.Details details = JewelryTrigger.Details.piece(identity.style(), JewelryStats.of(identity).rarity());
        if (reported.add(details)) {
            MagicTreasuresCriteria.JEWELRY.trigger(player, MagicTreasuresCriteria.CARRY, details);
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        int converted = 0;
        for (Slot slot : event.getContainer().slots) {
            if (slot.container == player.getInventory()) {
                continue; // the tick handles the player's own inventory
            }
            Optional<ItemStack> stack = LegacyConversion.convert(slot.getItem());
            if (stack.isPresent()) {
                slot.set(stack.get());
                converted++;
            }
        }
        if (converted > 0) {
            MagicTreasures.LOGGER.info("converted {} 1.x jewelry item(s) in a container opened by {}", converted,
                    player.getGameProfile().getName());
            event.getContainer().broadcastChanges();
        }
    }

    private static int convert(Container container) {
        int converted = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            Optional<ItemStack> stack = LegacyConversion.convert(container.getItem(i));
            if (stack.isPresent()) {
                container.setItem(i, stack.get());
                converted++;
            }
        }
        return converted;
    }
}
