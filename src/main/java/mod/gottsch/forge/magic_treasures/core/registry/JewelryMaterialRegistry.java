package mod.gottsch.forge.magic_treasures.core.registry;

import com.google.common.collect.Lists;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryMaterial;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * 
 * @author Mark Gottschling May 8, 2024
 *
 */
public class JewelryMaterialRegistry {
    private static final Map<ResourceLocation, JewelryMaterial> REGISTRY = new HashMap<>();

    public static Optional<JewelryMaterial> register(JewelryMaterial material) {
        if (!REGISTRY.containsKey(material.getId())) {
            REGISTRY.put(material.getId(), material);
            return Optional.of(material);
        }
        return Optional.empty();
    }

    public static boolean has(ResourceLocation name) {
        return REGISTRY.containsKey(name);
    }

    /**
     *
     * @param name
     * @return
     */
    public static Optional<JewelryMaterial> get(ResourceLocation name) {

        if (name != null && REGISTRY.containsKey(name)) {
            return Optional.of(REGISTRY.get(name));
        }
        return Optional.empty();
    }

    /**
     *
     * @return
     */
    public static List<ResourceLocation> getNames() {
        return Lists.newArrayList(REGISTRY.keySet());
    }

    public static List<JewelryMaterial> getMaterials() {
        return Lists.newArrayList(REGISTRY.values());
    }
}
