package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.Set;

/**
 * Which combinations of type, material, style and gem may exist. The creative tab, loot, crafting and the anvil all
 * ask here, so they can't disagree. Pass only enabled entries (from {@link JewelryDataIndex}).
 */
public final class JewelryRules {
    public static final ResourceLocation REGULAR = ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "regular");

    /** namespaces whose content is built in; anything else came from a pack */
    private static final Set<String> BUILT_IN = Set.of(MagicTreasures.MOD_ID, "minecraft", "treasure2");
    /** built-in gems that are featured; Treasure2's are duplicates of MT's */
    private static final Set<String> FEATURED_GEM_NAMESPACES = Set.of(MagicTreasures.MOD_ID, "minecraft");

    private JewelryRules() {}

    /**
     * Whether an allowed combination is featured: shown in the creative tab (not search-only) and rolled as random
     * loot. A pack's material always is. A built-in material is only if it has a rarity (not the unplanned blood,
     * shadow and atium), with no gem, MT's or vanilla gems, or a pack's gem; never Treasure2's duplicates. For the
     * built-in data this is exactly the 387 1.x combination items (Mark, 2026-10-11).
     */
    public static boolean featured(Holder.Reference<JewelryMaterialDef> material, Optional<? extends Holder.Reference<GemstoneDef>> gem) {
        if (!BUILT_IN.contains(material.key().location().getNamespace())) {
            return true;
        }
        if (material.value().rarity().isEmpty()) {
            return false;
        }
        return gem.map(g -> {
            String namespace = g.key().location().getNamespace();
            return FEATURED_GEM_NAMESPACES.contains(namespace) || !BUILT_IN.contains(namespace);
        }).orElse(true);
    }

    public static boolean allows(String type, Holder<JewelryMaterialDef> material, Holder<JewelryStyleDef> style,
            Optional<Holder<GemstoneDef>> gem) {
        MaterialLimits limits = material.value().limits();
        if (!limits.allowsType(type) || !style.value().allowsType(type) || !limits.allowsStyle(style)) {
            return false;
        }
        return gem.map(g -> allowsGem(material, g)).orElse(!limits.requiresGem());
    }

    /** as {@link #allowsGem(Holder, Holder)}, and also within a preset's own gem rules */
    public static boolean allowsGem(Holder<JewelryMaterialDef> material, Holder<GemstoneDef> gem,
            Optional<? extends Holder<JewelryPresetDef>> preset) {
        return allowsGem(material, gem)
                && preset.map(p -> p.value().acceptsGems() && p.value().rules().allowsGem(gem)).orElse(true);
    }

    /** whether this gem may be set in this material, checked from both sides */
    public static boolean allowsGem(Holder<JewelryMaterialDef> material, Holder<GemstoneDef> gem) {
        return material.value().acceptsGems()
                && material.value().limits().allowsGem(gem)
                && gem.value().affixesTo(material);
    }
}
