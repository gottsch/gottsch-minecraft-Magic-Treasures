package mod.gottsch.forge.magic_treasures.core.client.model;

import mod.gottsch.forge.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.forge.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.forge.gottschcore.nbt.StackData;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryDataIndex;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Tints the fallback layers of the composed jewelry model ({@link JewelryModel#TINT_MATERIAL},
 * {@link JewelryModel#TINT_GEM}) with the {@code color} of the jewelry_material and gemstone registry entries; real
 * art is untinted.
 */
public class JewelryColors implements ItemColor {
    private static final int WHITE = 0xFFFFFFFF;

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        JewelryIdentity identity = StackData.get(stack, MagicTreasuresDataComponents.JEWELRY_IDENTITY);
        if (identity == null) {
            return WHITE;
        }
        return switch (tintIndex) {
            case JewelryModel.TINT_MATERIAL -> argb(JewelryDataIndex.material(identity.material())
                    .map(material -> material.value().color()));
            case JewelryModel.TINT_GEM -> argb(identity.gem().flatMap(JewelryDataIndex::gemstone)
                    .map(gem -> gem.value().color()));
            default -> WHITE;
        };
    }

    /** Unknown or disabled ids stay untinted. */
    private static int argb(Optional<Integer> rgb) {
        // 1.21 item tints are ARGB: an alpha of 0 would make the layer invisible
        return rgb.map(color -> 0xFF000000 | color).orElse(WHITE);
    }
}
