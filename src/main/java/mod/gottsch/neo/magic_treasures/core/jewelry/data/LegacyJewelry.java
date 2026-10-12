package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.item.Jewelry;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelrySizeTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * M0: what each of the 418 1.x jewelry items is in 2.0, read from its builder (the same source the M8 presets were
 * generated from). Datagen writes this to {@code data/magictreasures/legacy/jewelry.json} for M9's conversion, which
 * must work after the builders are deleted; {@link LegacyDataCheck} checks it computes every item's 1.x stats.
 * <ul>
 *     <li>combination item → type, material, gem, style (the size), no preset;</li>
 *     <li>named piece → its preset (same id), with the piece's own gem;</li>
 *     <li>hawk ring, gemless castle ring → its preset (same id);</li>
 *     <li>castle ring with a gem → the gemless castle preset ({@code [size_]material_castle_ring}) + that gem.</li>
 * </ul>
 * 1.x stores "no gem" as {@code minecraft:air}, and "no gem, none may be added" as {@code minecraft:bedrock}: both
 * become no gem. Gem ids are the stone's item id, which is the gemstone id for every built-in gem.
 * Delete with the old system in M9 (the generated JSON stays).
 */
public final class LegacyJewelry {
    private static final ResourceLocation AIR = BuiltInRegistries.ITEM.getKey(Items.AIR);
    private static final ResourceLocation BEDROCK = BuiltInRegistries.ITEM.getKey(Items.BEDROCK);

    private LegacyJewelry() {}

    /** item id → 2.0 identity, for all 1.x jewelry items, sorted by id */
    public static Map<ResourceLocation, JewelryIdentity> identities() {
        Map<ResourceLocation, JewelryIdentity> identities = new TreeMap<>();
        for (DeferredItem<Item> item : MagicTreasuresItems.namedJewelry()) {
            identities.put(item.getId(), identity(item, true));
        }
        for (DeferredItem<Item> item : MagicTreasuresItems.STANDARD_JEWELRY) {
            identities.put(item.getId(), identity(item, false));
        }
        return identities;
    }

    /** the 2.0 identity of a 1.x jewelry item; {@code named} for {@link MagicTreasuresItems#namedJewelry()} */
    public static JewelryIdentity identity(DeferredItem<Item> item, boolean named) {
        JewelryHandler.Builder b = ((Jewelry) item.get()).jewelryDefaults();
        String type = b.type.getValue().toLowerCase(Locale.ROOT);
        String size = b.sizeTier.getValue().toLowerCase(Locale.ROOT);
        ResourceLocation material = b.material.getId();
        Optional<ResourceLocation> gem = Optional.ofNullable(b.stone).filter(stone -> !stone.equals(AIR) && !stone.equals(BEDROCK));
        ResourceLocation style = mt(size);

        Optional<ResourceLocation> preset = Optional.empty();
        if (named) {
            preset = Optional.of(item.getId());
        } else if (b.baseName != null) {
            // hawk and the gemless castle ring are their own presets; a castle ring with a gem is the gemless one + gem
            String affix = JewelrySizeTier.REGULAR.getValue().equalsIgnoreCase(size) ? "" : size + "_";
            preset = Optional.of(gem.isEmpty() ? item.getId() : mt(affix + material.getPath() + "_" + b.baseName));
        }
        return new JewelryIdentity(type, material, gem, style, preset);
    }

    private static ResourceLocation mt(String path) {
        return ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, path);
    }
}
