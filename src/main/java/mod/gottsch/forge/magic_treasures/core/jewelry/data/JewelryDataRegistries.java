package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.forge.magic_treasures.core.lore.LoreBookDef;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * Registers the 2.0 datapack registries: the five jewelry ones and the lore books. Each is given a network codec, so clients receive the entries (and
 * their tags) for tooltips and colors.
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class JewelryDataRegistries {

    private JewelryDataRegistries() {}

    @SubscribeEvent
    // the second codec is the network one: on 1.20.1 it can't refer to other datapack registries (NetworkHolderSet)
    public static void onNewDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(MagicTreasuresRegistries.JEWELRY_MATERIAL, JewelryMaterialDef.CODEC, JewelryMaterialDef.NETWORK_CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.JEWELRY_STYLE, JewelryStyleDef.CODEC, JewelryStyleDef.CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.STONE_TIER, StoneTierDef.CODEC, StoneTierDef.CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.GEMSTONE, GemstoneDef.CODEC, GemstoneDef.NETWORK_CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.JEWELRY_PRESET, JewelryPresetDef.CODEC, JewelryPresetDef.NETWORK_CODEC);
        event.dataPackRegistry(MagicTreasuresRegistries.LORE_BOOK, LoreBookDef.CODEC, LoreBookDef.CODEC);
    }
}
