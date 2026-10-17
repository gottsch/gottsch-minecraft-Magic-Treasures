package mod.gottsch.forge.magic_treasures.core.jewelry;

import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.JewelryCapability;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
//import mod.gottsch.forge.magic_treasures.core.item.JewelryMaterialTier;

@Deprecated
public class JewelryStoneHandlers {
    public static final JewelryStoneHandler STANDARD = new JewelryStoneHandler()
            .setCanAffix(p -> true);

    // POC
    public static final JewelryStoneHandler IRON_ONLY = new JewelryStoneHandler()
            .setCanAffix(p -> {
                IJewelryHandler handler = p.getCapability(MagicTreasuresCapabilities.JEWELRY_CAPABILITY).orElseThrow(IllegalStateException::new);
                return handler.getMaterial() == JewelryMaterials.IRON;
            });
}
