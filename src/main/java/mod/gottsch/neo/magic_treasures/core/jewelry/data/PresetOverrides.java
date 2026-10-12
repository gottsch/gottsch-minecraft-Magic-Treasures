package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * A preset's stat overrides: each one present replaces the value computed from material + gem + style; the rest
 * stay computed. Written as {@code "overrides": { "max_mana": 400, "infinite": true, "factors": { "cost": 0.95 } }}.
 *
 * @param infinite infinite uses (overrides {@code max_uses})
 * @param factors  per-key replacements of the computed factors
 */
public record PresetOverrides(
        Optional<Integer> maxUses,
        boolean infinite,
        Optional<Double> maxMana,
        Optional<Integer> maxLevel,
        Optional<Integer> maxRepairs,
        Optional<Integer> maxRecharges,
        Factors factors) {

    public static final PresetOverrides NONE = new PresetOverrides(Optional.empty(), false, Optional.empty(),
            Optional.empty(), Optional.empty(), Optional.empty(), Factors.NONE);

    public static final Codec<PresetOverrides> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("max_uses").forGetter(PresetOverrides::maxUses),
            Codec.BOOL.optionalFieldOf("infinite", false).forGetter(PresetOverrides::infinite),
            Codec.DOUBLE.optionalFieldOf("max_mana").forGetter(PresetOverrides::maxMana),
            Codec.INT.optionalFieldOf("max_level").forGetter(PresetOverrides::maxLevel),
            Codec.INT.optionalFieldOf("max_repairs").forGetter(PresetOverrides::maxRepairs),
            Codec.INT.optionalFieldOf("max_recharges").forGetter(PresetOverrides::maxRecharges),
            Factors.CODEC.optionalFieldOf("factors", Factors.NONE).forGetter(PresetOverrides::factors)
    ).apply(instance, PresetOverrides::new));

    /** factor replacements; a key left out keeps the computed factor */
    public record Factors(Optional<Double> cost, Optional<Double> effect, Optional<Double> duration,
                          Optional<Double> cooldown, Optional<Double> range) {

        public static final Factors NONE = new Factors(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty());

        public static final Codec<Factors> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("cost").forGetter(Factors::cost),
                Codec.DOUBLE.optionalFieldOf("effect").forGetter(Factors::effect),
                Codec.DOUBLE.optionalFieldOf("duration").forGetter(Factors::duration),
                Codec.DOUBLE.optionalFieldOf("cooldown").forGetter(Factors::cooldown),
                Codec.DOUBLE.optionalFieldOf("range").forGetter(Factors::range)
        ).apply(instance, Factors::new));

        public SpellFactors apply(SpellFactors computed) {
            return new SpellFactors(cost.orElse(computed.cost()), effect.orElse(computed.effect()),
                    duration.orElse(computed.duration()), cooldown.orElse(computed.cooldown()), range.orElse(computed.range()));
        }
    }
}
