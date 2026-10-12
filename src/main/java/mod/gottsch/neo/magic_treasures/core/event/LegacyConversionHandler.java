package mod.gottsch.neo.magic_treasures.core.event;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.LegacyConversion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

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
 */
@EventBusSubscriber(modid = MagicTreasures.MOD_ID)
public final class LegacyConversionHandler {
    private static final int INTERVAL = 20;

    private LegacyConversionHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % INTERVAL != 0) {
            return;
        }
        int converted = convert(player.getInventory());
        converted += CuriosApi.getCuriosInventory(player).map(curios -> curios.getCurios().values().stream()
                .mapToInt(handler -> convert(handler.getStacks()) + convert(handler.getCosmeticStacks()))
                .sum()).orElse(0);
        if (converted > 0) {
            MagicTreasures.LOGGER.info("converted {} 1.x jewelry item(s) carried by {}", converted, player.getGameProfile().getName());
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

    private static int convert(IDynamicStackHandler stacks) {
        int converted = 0;
        for (int i = 0; i < stacks.getSlots(); i++) {
            Optional<ItemStack> stack = LegacyConversion.convert(stacks.getStackInSlot(i));
            if (stack.isPresent()) {
                stacks.setStackInSlot(i, stack.get());
                converted++;
            }
        }
        return converted;
    }
}
