package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.core.Holder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The computed stats of a generic piece of jewelry: everything that follows from its identity, nothing that changes
 * in play. Uses the 1.x formulas ({@code JewelryHandler.Builder.build()} and the jewelry rarity tags), with the
 * size's numbers now coming from the style. A preset's {@code overrides} replace computed values.
 * <p>
 * An unknown or disabled material or style gives {@link #NONE}: the stack keeps its ids and shows as unknown, but
 * can't cast. An unknown or disabled gem is ignored ({@code gemKnown} false): the piece works as if it had no gem.
 *
 * @param known      false when the material or style is unknown or disabled
 * @param gemKnown   false when the identity names a gem that is unknown or disabled
 * @param factors    material factors × stone tier factors, per key
 */
public record JewelryStats(
        boolean known,
        boolean gemKnown,
        int maxUses,
        int maxLevel,
        double maxMana,
        int maxRepairs,
        int maxRecharges,
        SpellFactors factors,
        MagicTreasuresRarity rarity) {

    public static final JewelryStats NONE = new JewelryStats(false, false, 0, 0, 0, 0, 0, SpellFactors.NONE, MagicTreasuresRarity.COMMON);

    private static final Map<JewelryIdentity, JewelryStats> CACHE = new ConcurrentHashMap<>();
    private static volatile int cacheGeneration = -1;

    /** cached per identity; the cache resets whenever the data index is rebuilt */
    public static JewelryStats of(JewelryIdentity identity) {
        int generation = JewelryDataIndex.generation();
        if (cacheGeneration != generation) {
            CACHE.clear();
            cacheGeneration = generation;
        }
        return CACHE.computeIfAbsent(identity, JewelryStats::compute);
    }

    public static JewelryStats compute(JewelryIdentity identity) {
        Optional<Holder.Reference<JewelryMaterialDef>> materialHolder = JewelryDataIndex.material(identity.material());
        Optional<Holder.Reference<JewelryStyleDef>> styleHolder = JewelryDataIndex.style(identity.style());
        if (materialHolder.isEmpty() || styleHolder.isEmpty()) {
            return NONE;
        }
        JewelryMaterialDef material = materialHolder.get().value();
        JewelryStyleDef style = styleHolder.get().value();
        Optional<GemstoneDef> gem = identity.gem().flatMap(JewelryDataIndex::gemstone).map(Holder::value);
        Optional<StoneTierDef> tier = gem.map(g -> g.tier().value());

        int maxUses = (int) Math.round(material.uses() * style.usesMultiplier());
        int maxLevel = material.maxLevel() + style.maxLevel();
        double maxMana = Math.round((material.mana() + tier.map(StoneTierDef::mana).orElse(0)) * style.manaMultiplier());
        int maxRepairs = material.repairs() + style.repairs();
        int maxRecharges = material.recharges() + tier.map(StoneTierDef::recharges).orElse(0);
        SpellFactors factors = tier.map(t -> multiply(material.factors(), t.factors())).orElse(material.factors());

        // a preset's overrides replace computed values, as the 1.x builder's explicit values did
        Optional<PresetOverrides> overrides = identity.preset().flatMap(JewelryDataIndex::preset).map(p -> p.value().overrides());
        if (overrides.isPresent()) {
            PresetOverrides o = overrides.get();
            maxUses = o.infinite() ? Integer.MAX_VALUE : o.maxUses().orElse(maxUses);
            maxMana = o.maxMana().orElse(maxMana);
            maxLevel = o.maxLevel().orElse(maxLevel);
            maxRepairs = o.maxRepairs().orElse(maxRepairs);
            maxRecharges = o.maxRecharges().orElse(maxRecharges);
            factors = o.factors().apply(factors);
        }

        return new JewelryStats(true, identity.gem().isEmpty() || gem.isPresent(), maxUses, maxLevel, maxMana,
                maxRepairs, maxRecharges, factors, rarity(identity, material, style, gem));
    }

    /**
     * The higher of the material's and the gem's rarity, plus the style's rarity steps; a preset's rarity replaces
     * it. A material with no rarity (one only presets use) counts as common.
     */
    static MagicTreasuresRarity rarity(JewelryIdentity identity, JewelryMaterialDef material, JewelryStyleDef style,
            Optional<GemstoneDef> gem) {
        Optional<MagicTreasuresRarity> presetRarity = identity.preset().flatMap(JewelryDataIndex::preset)
                .flatMap(preset -> preset.value().rarity());
        if (presetRarity.isPresent()) {
            return presetRarity.get();
        }
        int code = material.rarity().orElse(MagicTreasuresRarity.COMMON).getCode();
        Optional<MagicTreasuresRarity> gemRarity = gem.flatMap(GemstoneDef::rarity);
        if (gemRarity.isPresent()) {
            code = Math.max(code, gemRarity.get().getCode());
        }
        code = Math.min(code + style.raritySteps(), MagicTreasuresRarity.MYTHICAL.getCode());
        return MagicTreasuresRarity.getByCode(code);
    }

    private static SpellFactors multiply(SpellFactors a, SpellFactors b) {
        return new SpellFactors(a.cost() * b.cost(), a.effect() * b.effect(), a.duration() * b.duration(),
                a.cooldown() * b.cooldown(), a.range() * b.range());
    }
}
