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
                stack("legacy: gold + ruby", JewelryIdentity.of("ring", mt("gold"), mt("ruby"), mt("regular"))),
                stack("legacy: great silver + sapphire", JewelryIdentity.of("ring", mt("silver"), mt("sapphire"), mt("great"))),
                // 2. preset art: must look like magictreasures:angels_ring
                stack("preset: angels_ring", JewelryIdentity.preset("ring", mt("gold"), mt("angels_ring"))),
                // 3. layered: no copper+emerald art, so ring/copper + ring/gem/emerald
                stack("layered: copper + emerald", JewelryIdentity.of("ring", mt("copper"), ResourceLocation.withDefaultNamespace("emerald"), mt("regular"))),
                // 4. tinted fallback, built-in data: no atium+sapphire art, so both layers take the registry colors
                stack("fallback: atium + sapphire", JewelryIdentity.of("ring", mt("atium"), mt("sapphire"), mt("regular"))),
                stack("fallback: shadow + topaz", JewelryIdentity.of("ring", mt("shadow"), mt("topaz"), mt("regular"))),
                // 5. tinted fallback, pack data: plain gray (untinted) until the mypack test datapack is loaded
                stack("fallback: titanium + amber", JewelryIdentity.of("ring", ResourceLocation.fromNamespaceAndPath("mypack", "titanium"),
                        ResourceLocation.fromNamespaceAndPath("mypack", "amber"), mt("regular"))),
                stack("fallback: titanium + ruby", JewelryIdentity.of("ring", ResourceLocation.fromNamespaceAndPath("mypack", "titanium"), mt("ruby"), mt("regular"))),
                // a pack style with its own shape and no art yet: ring/_base_pixaltier etc. when drawn, MT's plain
                // tinted fallback until then
                stack("style: pixaltier titanium + amber", JewelryIdentity.of("ring", ResourceLocation.fromNamespaceAndPath("mypack", "titanium"),
                        ResourceLocation.fromNamespaceAndPath("mypack", "amber"), ResourceLocation.fromNamespaceAndPath("mypack", "pixaltier"))),
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
