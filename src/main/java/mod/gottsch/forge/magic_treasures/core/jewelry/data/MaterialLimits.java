package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;

import java.util.List;
import java.util.Optional;

/**
 * What a material can be made into. Written as top-level keys of the material JSON. Each limit left out means no
 * limit; an empty list means none.
 *
 * @param types       jewelry types, e.g. {@code ["ring", "necklace"]}
 * @param styles      jewelry_style ids, a list or a {@code #tag}
 * @param gems        gemstone ids, a list or a {@code #tag}; checked together with the gem's own {@code affixes_to}
 * @param requiresGem true: never made without a gem
 */
public record MaterialLimits(
        Optional<List<String>> types,
        Optional<HolderSet<JewelryStyleDef>> styles,
        Optional<HolderSet<GemstoneDef>> gems,
        boolean requiresGem) {

    public static final MaterialLimits NONE = new MaterialLimits(Optional.empty(), Optional.empty(), Optional.empty(), false);

    public static final MapCodec<MaterialLimits> MAP_CODEC = mapCodec(false);
    /** what the client receives on Forge 1.20.1: references to other jewelry registries as ids (NetworkHolderSet) */
    public static final MapCodec<MaterialLimits> NETWORK_MAP_CODEC = mapCodec(true);

    /** {@code network}: refer to other jewelry registries by id (see NetworkHolderSet) */
    static MapCodec<MaterialLimits> mapCodec(boolean network) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.listOf().optionalFieldOf("types").forGetter(MaterialLimits::types),
                DataCodecs.holderSet(MagicTreasuresRegistries.JEWELRY_STYLE, network).optionalFieldOf("styles").forGetter(MaterialLimits::styles),
                DataCodecs.holderSet(MagicTreasuresRegistries.GEMSTONE, network).optionalFieldOf("gems").forGetter(MaterialLimits::gems),
                Codec.BOOL.optionalFieldOf("requires_gem", false).forGetter(MaterialLimits::requiresGem)
        ).apply(instance, MaterialLimits::new));
    }

    public boolean allowsType(String type) {
        return types.map(list -> list.contains(type)).orElse(true);
    }

    public boolean allowsStyle(Holder<JewelryStyleDef> style) {
        return styles.map(set -> set.contains(style)).orElse(true);
    }

    public boolean allowsGem(Holder<GemstoneDef> gem) {
        return gems.map(set -> set.contains(gem)).orElse(true);
    }
}
