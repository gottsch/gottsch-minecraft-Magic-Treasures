package mod.gottsch.forge.magic_treasures.core.item;

import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import mod.gottsch.forge.magic_treasures.core.capability.JewelryCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * One of the 418 1.x jewelry item ids, kept registered so old stacks still load (and keep their 1.x model and name
 * in chests, storage mods and item frames). It does nothing: a player carrying it, or opening the container holding
 * it, converts it to 2.0 generic jewelry ({@link mod.gottsch.forge.magic_treasures.core.jewelry.data.LegacyConversion}).
 * <p>
 * Deliberately not a {@link Jewelry}, so no jewelry code (handlers, Curios, sets, the anvil) treats it as jewelry.
 */
public class LegacyJewelryItem extends Item {
    public LegacyJewelryItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    /** keeps the stack's 1.x jewelry state until it is converted (see JewelryCapability) */
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new JewelryCapability();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable(LangUtil.tooltip("legacy_jewelry")).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
