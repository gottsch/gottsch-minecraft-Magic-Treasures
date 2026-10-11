package mod.gottsch.neo.magic_treasures.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * What a generic piece of jewelry (magictreasures:ring etc.) is: its type, material, gem, size and, for unique
 * jewelry, its preset. Kept apart from the jewelry's play state (mana, uses, spells) so the item model can key its
 * cache on this alone: it changes only when a gem is set or removed.
 * Ids are registry ids of the 2.0 data-driven registries (jewelry_material, gemstone, jewelry_preset).
 *
 * @param type     jewelry type value, e.g. "ring"
 * @param material jewelry_material id
 * @param gem      gemstone id, if a gem is set
 * @param size     size tier value: "regular", "great" or "lords"
 * @param preset   jewelry_preset id, for unique jewelry
 */
public record JewelryIdentity(
        String type,
        ResourceLocation material,
        Optional<ResourceLocation> gem,
        String size,
        Optional<ResourceLocation> preset) {

    public static final String REGULAR = "regular";

    public static final Codec<JewelryIdentity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(JewelryIdentity::type),
            ResourceLocation.CODEC.fieldOf("material").forGetter(JewelryIdentity::material),
            ResourceLocation.CODEC.optionalFieldOf("gem").forGetter(JewelryIdentity::gem),
            Codec.STRING.optionalFieldOf("size", REGULAR).forGetter(JewelryIdentity::size),
            ResourceLocation.CODEC.optionalFieldOf("preset").forGetter(JewelryIdentity::preset)
    ).apply(instance, JewelryIdentity::new));

    public static final StreamCodec<ByteBuf, JewelryIdentity> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, JewelryIdentity::type,
            ResourceLocation.STREAM_CODEC, JewelryIdentity::material,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), JewelryIdentity::gem,
            ByteBufCodecs.STRING_UTF8, JewelryIdentity::size,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), JewelryIdentity::preset,
            JewelryIdentity::new);

    public static JewelryIdentity of(String type, ResourceLocation material, ResourceLocation gem, String size) {
        return new JewelryIdentity(type, material, Optional.ofNullable(gem), size, Optional.empty());
    }

    public static JewelryIdentity preset(String type, ResourceLocation material, ResourceLocation preset) {
        return new JewelryIdentity(type, material, Optional.empty(), REGULAR, Optional.of(preset));
    }
}
