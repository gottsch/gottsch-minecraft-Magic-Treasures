package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import mod.gottsch.neo.gottschcore.enums.IRarity;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.item.GenericJewelry;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * What random loot can be, built from the registries: the 2.0 replacement for 1.x's jewelry and stone rarity tags
 * (the 1.x {@code JewelryRegistry} and {@code StoneRegistry}, deleted in M9).
 * <p>
 * Jewelry candidates are every {@link JewelryRules#featured featured} combination the rules allow (for the built-in
 * data, the 387 1.x combination items) plus every preset with {@code loot}. A preset with a {@code gems} rule and no
 * gem of its own (the castle ring) is a family: it also rolls once per featured gem it accepts, as 1.x had one item
 * per castle gem.
 * Combinations weigh 1, presets their {@code loot.weight}, so with the built-in data every 1.x item is as likely as
 * before. Rarity is the computed one ({@link JewelryStats#rarity()}), which equals the 1.x rarity tags.
 * <p>
 * Gem candidates are every enabled gemstone with a rarity whose item exists, Treasure2's included (they were in the
 * 1.x stone rarity tags).
 */
public final class JewelryLoot {
    public record Candidate(JewelryIdentity identity, JewelryStats stats, int weight) {
        public MagicTreasuresRarity rarity() {
            return stats.rarity();
        }

        public ItemStack stack() {
            return item(identity.type()).map(item -> item.stack(identity)).orElse(ItemStack.EMPTY);
        }
    }

    private static volatile List<Candidate> jewelry = List.of();
    private static volatile int generation = -1;

    private JewelryLoot() {}

    /** every jewelry candidate; rebuilt when the data index is */
    public static List<Candidate> jewelry() {
        int current = JewelryDataIndex.generation();
        if (generation != current) {
            jewelry = build();
            generation = current;
        }
        return jewelry;
    }

    /** the enabled gemstones with a rarity whose item exists */
    public static List<Holder.Reference<GemstoneDef>> gemstones() {
        return JewelryDataIndex.gemstones().values().stream()
                .filter(gem -> gem.value().rarity().isPresent())
                .toList();
    }

    public static Item item(Holder<GemstoneDef> gem) {
        return BuiltInRegistries.ITEM.get(gem.value().item());
    }

    /**
     * The jewelry candidates of this rarity that pass the filter. If there are none, the nearest lower rarity that has
     * some (#14.9: a pack may leave a rarity empty).
     */
    public static List<Candidate> jewelry(IRarity rarity, Predicate<Candidate> filter) {
        return withFallback(rarity, code -> jewelry().stream()
                .filter(candidate -> candidate.rarity().getCode() == code)
                .filter(filter)
                .toList());
    }

    /** as {@link #jewelry(IRarity, Predicate)}, for gems */
    public static List<Holder.Reference<GemstoneDef>> gemstones(IRarity rarity, Predicate<Holder.Reference<GemstoneDef>> filter) {
        return withFallback(rarity, code -> gemstones().stream()
                .filter(gem -> gem.value().rarity().map(MagicTreasuresRarity::getCode).orElse(-1) == code)
                .filter(filter)
                .toList());
    }

    private static <T> List<T> withFallback(IRarity rarity, java.util.function.IntFunction<List<T>> ofCode) {
        for (int code = rarity.getCode(); code >= MagicTreasuresRarity.COMMON.getCode(); code--) {
            List<T> found = ofCode.apply(code);
            if (!found.isEmpty()) {
                return found;
            }
        }
        return List.of();
    }

    /** a candidate picked by weight */
    public static Optional<Candidate> pick(List<Candidate> candidates, RandomSource random) {
        int total = candidates.stream().mapToInt(Candidate::weight).sum();
        if (total <= 0) {
            return Optional.empty();
        }
        int roll = random.nextInt(total);
        for (Candidate candidate : candidates) {
            roll -= candidate.weight();
            if (roll < 0) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    private static List<Candidate> build() {
        List<Candidate> candidates = new ArrayList<>();
        List<Optional<Holder.Reference<GemstoneDef>>> gems = new ArrayList<>();
        gems.add(Optional.empty());
        JewelryDataIndex.gemstones().values().forEach(gem -> gems.add(Optional.of(gem)));

        for (var deferred : MagicTreasuresItems.GENERIC_JEWELRY) {
            String type = ((GenericJewelry) deferred.get()).getJewelryType();
            for (Holder.Reference<JewelryMaterialDef> material : JewelryDataIndex.materials().values()) {
                for (Holder.Reference<JewelryStyleDef> style : JewelryDataIndex.styles().values()) {
                    for (Optional<Holder.Reference<GemstoneDef>> gem : gems) {
                        if (JewelryRules.allows(type, material, style, gem.map(g -> g)) && JewelryRules.featured(material, gem)) {
                            add(candidates, new JewelryIdentity(type, material.key().location(),
                                    gem.map(g -> g.key().location()), style.key().location(), Optional.empty()), 1);
                        }
                    }
                }
            }
        }

        for (Holder.Reference<JewelryPresetDef> preset : JewelryDataIndex.presets().values()) {
            JewelryPresetDef def = preset.value();
            if (def.loot().isEmpty() || item(def.type()).isEmpty()) {
                continue;
            }
            int weight = def.loot().get().weight();
            Optional<Holder.Reference<JewelryMaterialDef>> material = JewelryDataIndex.material(def.material());
            add(candidates, new JewelryIdentity(def.type(), def.material(), def.gem(), def.style(),
                    Optional.of(preset.key().location())), weight);
            // a family (castle ring: no gem of its own, a gems rule): one more per featured gem it accepts. A piece with
            // its own gem and a gems rule (Ring of Fortitude: sapphire, may be swapped for ruby) drops as itself only
            if (def.gem().isEmpty() && def.rules().gems().isPresent() && material.isPresent()) {
                for (Holder.Reference<GemstoneDef> gem : JewelryDataIndex.gemstones().values()) {
                    if (JewelryRules.allowsGem(material.get(), gem, Optional.of(preset))
                            && JewelryRules.featured(material.get(), Optional.of(gem))) {
                        add(candidates, new JewelryIdentity(def.type(), def.material(), Optional.of(gem.key().location()),
                                def.style(), Optional.of(preset.key().location())), weight);
                    }
                }
            }
        }
        return List.copyOf(candidates);
    }

    private static void add(List<Candidate> candidates, JewelryIdentity identity, int weight) {
        JewelryStats stats = JewelryStats.of(identity);
        if (stats.known() && weight > 0) {
            candidates.add(new Candidate(identity, stats, weight));
        }
    }

    private static Optional<GenericJewelry> item(String type) {
        return MagicTreasuresItems.GENERIC_JEWELRY.stream().map(item -> (GenericJewelry) item.get())
                .filter(item -> item.getJewelryType().equals(type)).findFirst();
    }
}
