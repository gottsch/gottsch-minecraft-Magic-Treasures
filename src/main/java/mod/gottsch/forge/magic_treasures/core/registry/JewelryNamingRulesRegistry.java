package mod.gottsch.forge.magic_treasures.core.registry;

import com.google.common.collect.Maps;
import mod.gottsch.forge.magic_treasures.core.item.generator.JewelryGenerator;
import net.minecraft.world.item.Item;

import java.util.Map;

// TODO refactor, rename and use - see Treasure2 WishableHandler
/**
 * Every Jewelry item must be registered with a JewelryGenerator here or
 * variations will not be generator and instead the original item will be used for
 * all variations.
 * Ex. Peasent's Fortune is a great iron ring. If not registered, then if you add
 * a sapphire, the original item will be used with a modified JewelryCapability.
 * @author Mark Gottschling May 6, 2024
 *
 */
public class JewelryNamingRulesRegistry {
//    public static final Map<Item, JewelryGenerator.NamingRules> REGISTRY = Maps.newHashMap();
//
//    public static final JewelryGenerator.NamingRules STANDARD_RULES = new JewelryGenerator.NamingRules.Builder()
//            .with($ -> {
//                $.useBaseName = false;
//                $.useType = true;
//                $.useMaterial = true;
//                $.useStone = true;
//                $.useSize = true;
//            }).build();
//
//    private JewelryNamingRulesRegistry() {}
}
