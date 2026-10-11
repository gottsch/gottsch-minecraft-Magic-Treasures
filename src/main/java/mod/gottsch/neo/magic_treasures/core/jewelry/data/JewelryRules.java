package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Which combinations of type, material, style and gem may exist. The creative tab, loot, crafting and the anvil all
 * ask here, so they can't disagree. Pass only enabled entries (from {@link JewelryDataIndex}).
 */
public final class JewelryRules {
    public static final ResourceLocation REGULAR = ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "regular");

    private JewelryRules() {}

    public static boolean allows(String type, Holder<JewelryMaterialDef> material, Holder<JewelryStyleDef> style,
            Optional<Holder<GemstoneDef>> gem) {
        MaterialLimits limits = material.value().limits();
        if (!limits.allowsType(type) || !style.value().allowsType(type) || !limits.allowsStyle(style)) {
            return false;
        }
        return gem.map(g -> allowsGem(material, g)).orElse(!limits.requiresGem());
    }

    /** whether this gem may be set in this material, checked from both sides */
    public static boolean allowsGem(Holder<JewelryMaterialDef> material, Holder<GemstoneDef> gem) {
        return material.value().acceptsGems()
                && material.value().limits().allowsGem(gem)
                && gem.value().affixesTo(material);
    }
}
