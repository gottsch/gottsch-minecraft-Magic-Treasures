package mod.gottsch.neo.magic_treasures.core.client.model;

import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;

/**
 * Tints the fallback layers of the composed jewelry model ({@link JewelryModel#TINT_MATERIAL},
 * {@link JewelryModel#TINT_GEM}); real art is untinted.
 * <p>
 * M4 spike: the colors are a fixed map here. M1 replaces it with the {@code color} of the jewelry_material and
 * gemstone registry entries.
 */
public class JewelryColors implements ItemColor {
    private static final int WHITE = 0xFFFFFFFF;

    // TODO M1: read from the jewelry_material / gemstone registries
    private static final Map<ResourceLocation, Integer> SPIKE_COLORS = Map.of(
            ResourceLocation.fromNamespaceAndPath("mypack", "titanium"), 0xB8C2CC,
            ResourceLocation.fromNamespaceAndPath("mymod", "amber"), 0xE8A33A,
            ResourceLocation.fromNamespaceAndPath("magictreasures", "ruby"), 0xD0203A);

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        JewelryIdentity identity = stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
        if (identity == null) {
            return WHITE;
        }
        return switch (tintIndex) {
            case JewelryModel.TINT_MATERIAL -> color(Optional.of(identity.material()));
            case JewelryModel.TINT_GEM -> color(identity.gem());
            default -> WHITE;
        };
    }

    private static int color(Optional<ResourceLocation> id) {
        // 1.21 item tints are ARGB: an alpha of 0 would make the layer invisible
        return id.map(SPIKE_COLORS::get).map(rgb -> 0xFF000000 | rgb).orElse(WHITE);
    }
}
