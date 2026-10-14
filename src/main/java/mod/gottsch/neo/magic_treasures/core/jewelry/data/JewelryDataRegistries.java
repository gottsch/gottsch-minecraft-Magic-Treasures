package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.neo.magic_treasures.core.lore.LoreBookDef;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

/**
 * Registers the 2.0 datapack registries: the five jewelry ones and the lore books. Each is given a network codec, so clients receive the entries (and
 * their tags) for tooltips and colors.
 */
@EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class JewelryDataRegistries {

    private JewelryDataRegistries() {}

    @SubscribeEvent
    public static void onNewDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(MagicTreasuresRegistries.JEWELRY_MATERIAL, JewelryMaterialDef.CODEC, JewelryMaterialDef.CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.JEWELRY_STYLE, JewelryStyleDef.CODEC, JewelryStyleDef.CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.STONE_TIER, StoneTierDef.CODEC, StoneTierDef.CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.GEMSTONE, GemstoneDef.CODEC, GemstoneDef.CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.JEWELRY_PRESET, JewelryPresetDef.CODEC, JewelryPresetDef.CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.LORE_BOOK, LoreBookDef.CODEC, LoreBookDef.CODEC);
    }
}
