package mod.gottsch.forge.magic_treasures.core.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Every stack in a player's Curios slots, cosmetic ones included. Curios is optional: this is the only class outside
 * {@link CuriosEquipmentSpellHandler} that references its API, so call it only when Curios is loaded, or loading it
 * fails with NoClassDefFoundError.
 */
final class CuriosSlots {
    private CuriosSlots() {}

    static void forEach(ServerPlayer player, Consumer<ItemStack> action) {
        CuriosApi.getCuriosInventory(player).ifPresent(curios -> curios.getCurios().values().forEach(handler -> {
            forEach(handler.getStacks(), action);
            forEach(handler.getCosmeticStacks(), action);
        }));
    }

    /** puts {@code replace}'s result in place of each stack it returns one for; how many were replaced */
    static int replaceEach(ServerPlayer player, Function<ItemStack, Optional<ItemStack>> replace) {
        return CuriosApi.getCuriosInventory(player).map(curios -> curios.getCurios().values().stream()
                .mapToInt(handler -> replaceEach(handler.getStacks(), replace) + replaceEach(handler.getCosmeticStacks(), replace))
                .sum()).orElse(0);
    }

    private static void forEach(IDynamicStackHandler stacks, Consumer<ItemStack> action) {
        for (int i = 0; i < stacks.getSlots(); i++) {
            action.accept(stacks.getStackInSlot(i));
        }
    }

    private static int replaceEach(IDynamicStackHandler stacks, Function<ItemStack, Optional<ItemStack>> replace) {
        int replaced = 0;
        for (int i = 0; i < stacks.getSlots(); i++) {
            Optional<ItemStack> stack = replace.apply(stacks.getStackInSlot(i));
            if (stack.isPresent()) {
                stacks.setStackInSlot(i, stack.get());
                replaced++;
            }
        }
        return replaced;
    }
}
