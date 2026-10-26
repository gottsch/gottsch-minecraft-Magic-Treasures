package mod.gottsch.forge.magic_treasures.api;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.GemstoneDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryMaterialDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryPresetDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryStyleDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.StoneTierDef;
import mod.gottsch.forge.magic_treasures.core.lore.LoreBookDef;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

/**
 * Keys of the 2.0 datapack registries. Entries live at {@code data/<namespace>/magictreasures/<registry>/<path>.json}
 * and registry tags at {@code data/<namespace>/tags/magictreasures/<registry>/<path>.json}.
 */
public final class MagicTreasuresRegistries {
    public static final ResourceKey<Registry<JewelryMaterialDef>> JEWELRY_MATERIAL = key("jewelry_material");
    public static final ResourceKey<Registry<JewelryStyleDef>> JEWELRY_STYLE = key("jewelry_style");
    public static final ResourceKey<Registry<StoneTierDef>> STONE_TIER = key("stone_tier");
    public static final ResourceKey<Registry<GemstoneDef>> GEMSTONE = key("gemstone");
    public static final ResourceKey<Registry<JewelryPresetDef>> JEWELRY_PRESET = key("jewelry_preset");
    /** the lore books (see {@link LoreBookDef}) */
    public static final ResourceKey<Registry<LoreBookDef>> LORE_BOOK = key("lore_book");

    public static final class Tags {
        /** Metal materials; a gem can say {@code "affixes_to": "#magictreasures:metals"}. */
        public static final TagKey<JewelryMaterialDef> METALS = TagKey.create(JEWELRY_MATERIAL, id("metals"));
        /** the gems a castle ring takes: rubies and sapphires (MT's and Treasure2's) */
        public static final TagKey<GemstoneDef> CASTLE_RING_GEMS = TagKey.create(GEMSTONE, id("castle_ring_gems"));

        private Tags() {}
    }

    private MagicTreasuresRegistries() {}

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(id(name));
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(MagicTreasures.MOD_ID, path);
    }
}
