package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Spell multipliers of a material or stone tier. A key left out of the JSON is 1.0, and a 1.0 is left out when
 * encoding, so the JSON names only what differs.
 */
public record SpellFactors(double cost, double effect, double duration, double cooldown, double range) {
    public static final SpellFactors NONE = new SpellFactors(1.0, 1.0, 1.0, 1.0, 1.0);

    public static final Codec<SpellFactors> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("cost", 1.0).forGetter(SpellFactors::cost),
            Codec.DOUBLE.optionalFieldOf("effect", 1.0).forGetter(SpellFactors::effect),
            Codec.DOUBLE.optionalFieldOf("duration", 1.0).forGetter(SpellFactors::duration),
            Codec.DOUBLE.optionalFieldOf("cooldown", 1.0).forGetter(SpellFactors::cooldown),
            Codec.DOUBLE.optionalFieldOf("range", 1.0).forGetter(SpellFactors::range)
    ).apply(instance, SpellFactors::new));
}
