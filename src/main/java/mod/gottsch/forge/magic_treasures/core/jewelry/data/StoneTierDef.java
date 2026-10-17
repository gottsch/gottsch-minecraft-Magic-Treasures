package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * A stone tier ({@code magictreasures:stone_tier}): what a set gem adds to the jewelry's stats. Gems share a tier by
 * id, or a gem can define its own tier inline.
 */
public record StoneTierDef(int mana, int recharges, SpellFactors factors, boolean disabled) {

    public static final Codec<StoneTierDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("mana").forGetter(StoneTierDef::mana),
            Codec.INT.optionalFieldOf("recharges", 0).forGetter(StoneTierDef::recharges),
            SpellFactors.CODEC.optionalFieldOf("factors", SpellFactors.NONE).forGetter(StoneTierDef::factors),
            Codec.BOOL.optionalFieldOf("disabled", false).forGetter(StoneTierDef::disabled)
    ).apply(instance, StoneTierDef::new));
}
