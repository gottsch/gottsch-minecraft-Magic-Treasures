package mod.gottsch.neo.magic_treasures.core.item;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * M4 spike: test stacks of the generic ring, one per sprite lookup case of the composed jewelry model.
 * Shown at the end of the creative tab, after the legacy items they should match. Removed in M3.
 */
public class JewelryModelSpike {

    private static ResourceLocation mt(String path) {
        return ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, path);
    }

    public static List<ItemStack> stacks() {
        return List.of(
                // 1. legacy full-combination art: must look exactly like magictreasures:gold_ruby_ring
                stack("legacy: gold + ruby", JewelryIdentity.of("ring", mt("gold"), mt("ruby"), "regular")),
                stack("legacy: great silver + sapphire", JewelryIdentity.of("ring", mt("silver"), mt("sapphire"), "great")),
                // 2. preset art: must look like magictreasures:angels_ring
                stack("preset: angels_ring", JewelryIdentity.preset("ring", mt("gold"), mt("angels_ring"))),
                // 3. layered: no copper+emerald art, so ring/copper + ring/gem/emerald
                stack("layered: copper + emerald", JewelryIdentity.of("ring", mt("copper"), ResourceLocation.withDefaultNamespace("emerald"), "regular")),
                // 4. tinted fallback: a pack material and gem with no art at all
                stack("fallback: titanium + amber", JewelryIdentity.of("ring", ResourceLocation.fromNamespaceAndPath("mypack", "titanium"),
                        ResourceLocation.fromNamespaceAndPath("mymod", "amber"), "regular")),
                // 5. mixed: no titanium art, no ring/gem/ruby layer -> tinted base + tinted overlay
                stack("fallback: titanium + ruby", JewelryIdentity.of("ring", ResourceLocation.fromNamespaceAndPath("mypack", "titanium"), mt("ruby"), "regular")),
                // 6. no identity at all: the plain default model
                new ItemStack(MagicTreasuresItems.RING.get())
        );
    }

    private static ItemStack stack(String label, JewelryIdentity identity) {
        ItemStack stack = new ItemStack(MagicTreasuresItems.RING.get());
        stack.set(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get(), identity);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("[M4] " + label));
        return stack;
    }
}
