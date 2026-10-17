package mod.gottsch.forge.magic_treasures.core.jewelry;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * this is like a capability handler but doesn't have an associated capability
 * because it is a single instance and not attached to any entity instance.
 */
@Deprecated // can use custom tiers to accomplish the same thing without muddy waters with a new class and mappings
public class JewelryStoneHandler {
    private Predicate<ItemStack> canAffix = p -> true;

    public JewelryStoneHandler() {}

    public boolean canAffix(ItemStack itemStack) {
        return canAffix.test(itemStack);
    }

    public JewelryStoneHandler setCanAffix(Predicate<ItemStack> p) {
        this.canAffix = p;
        return this;

    }
}
