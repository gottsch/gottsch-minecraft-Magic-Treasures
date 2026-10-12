package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.component.JewelryData;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterial;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterials;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryStoneTier;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryStoneTiers;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.neo.magic_treasures.core.tag.MagicTreasuresTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * M1/M2/M6/M8 checkpoint: compares the registry entries with the 1.x constants and tags they were generated from, and the
 * stats computed from them with every 1.x combination item's own values, and lists every difference. Runs alongside
 * the old system; delete it with the old system in M9.
 */
public final class LegacyDataCheck {

    private static final List<JewelryMaterial> MATERIALS = List.of(JewelryMaterials.WOOD, JewelryMaterials.IRON,
            JewelryMaterials.COPPER, JewelryMaterials.SILVER, JewelryMaterials.GOLD, JewelryMaterials.BONE,
            JewelryMaterials.BLOOD, JewelryMaterials.SHADOW, JewelryMaterials.ATIUM);

    private static final List<JewelryStoneTier> TIERS = List.of(JewelryStoneTiers.TIER1, JewelryStoneTiers.TIER2,
            JewelryStoneTiers.TIER3, JewelryStoneTiers.TIER4, JewelryStoneTiers.TIER5, JewelryStoneTiers.TIER6);

    private static final Map<ResourceLocation, TagKey<Item>> TIER_TAGS = Map.of(
            JewelryStoneTiers.TIER1.getName(), MagicTreasuresTags.Items.STONE_TIER1,
            JewelryStoneTiers.TIER2.getName(), MagicTreasuresTags.Items.STONE_TIER2,
            JewelryStoneTiers.TIER3.getName(), MagicTreasuresTags.Items.STONE_TIER3,
            JewelryStoneTiers.TIER4.getName(), MagicTreasuresTags.Items.STONE_TIER4,
            JewelryStoneTiers.TIER5.getName(), MagicTreasuresTags.Items.STONE_TIER5,
            JewelryStoneTiers.TIER6.getName(), MagicTreasuresTags.Items.STONE_TIER6);

    private static final Map<MagicTreasuresRarity, TagKey<Item>> RARITY_TAGS = Map.of(
            MagicTreasuresRarity.COMMON, MagicTreasuresTags.Items.STONE_RARITY_COMMON,
            MagicTreasuresRarity.UNCOMMON, MagicTreasuresTags.Items.STONE_RARITY_UNCOMMON,
            MagicTreasuresRarity.SCARCE, MagicTreasuresTags.Items.STONE_RARITY_SCARCE,
            MagicTreasuresRarity.RARE, MagicTreasuresTags.Items.STONE_RARITY_RARE,
            MagicTreasuresRarity.EPIC, MagicTreasuresTags.Items.STONE_RARITY_EPIC);

    private static final Set<String> BUILT_IN_NAMESPACES = Set.of(MagicTreasures.MOD_ID, "minecraft", "treasure2");

    private LegacyDataCheck() {}

    /** @return one line per difference; empty when the registries match the old constants and tags */
    public static List<String> run() {
        List<String> problems = new ArrayList<>();

        for (JewelryMaterial old : MATERIALS) {
            Optional<Holder.Reference<JewelryMaterialDef>> entry = JewelryDataIndex.material(old.getId());
            if (entry.isEmpty()) {
                problems.add("material " + old.getId() + ": missing");
                continue;
            }
            JewelryMaterialDef def = entry.get().value();
            String what = "material " + old.getId();
            check(problems, what, "uses", old.getUses(), def.uses());
            check(problems, what, "mana", old.getMana(), def.mana());
            check(problems, what, "repairs", old.getRepairs(), def.repairs());
            check(problems, what, "recharges", old.getRecharges(), def.recharges());
            check(problems, what, "max_level", old.getMaxLevel(), def.maxLevel());
            check(problems, what, "spells", old.getSpells(), def.spells());
            check(problems, what, "factors", new SpellFactors(old.getSpellCostFactor(), old.getSpellEffectAmountFactor(),
                    old.getSpellDurationFactor(), old.getSpellCooldownFactor(), old.getSpellRangeFactor()), def.factors());
        }

        // styles were 1.x sizes: stats from JewelrySizeTier, max level and rarity steps from its code
        for (JewelrySizeTier old : List.of(JewelrySizeTier.REGULAR, JewelrySizeTier.GREAT, JewelrySizeTier.LORDS)) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, old.getValue());
            Optional<Holder.Reference<JewelryStyleDef>> entry = JewelryDataIndex.style(id);
            if (entry.isEmpty()) {
                problems.add("jewelry_style " + id + ": missing");
                continue;
            }
            JewelryStyleDef def = entry.get().value();
            String what = "jewelry_style " + id;
            check(problems, what, "uses_multiplier", (double) old.getUsesMultiplier(), def.usesMultiplier());
            check(problems, what, "mana_multiplier", (double) old.getManaMultiplier(), def.manaMultiplier());
            check(problems, what, "repairs", old.getRepairs(), def.repairs());
            check(problems, what, "max_level", old.getCode(), def.maxLevel());
            check(problems, what, "rarity_steps", old.getCode(), def.raritySteps());
        }

        for (JewelryStoneTier old : TIERS) {
            Optional<Holder.Reference<StoneTierDef>> entry = JewelryDataIndex.stoneTier(old.getName());
            if (entry.isEmpty()) {
                problems.add("stone_tier " + old.getName() + ": missing");
                continue;
            }
            StoneTierDef def = entry.get().value();
            String what = "stone_tier " + old.getName();
            check(problems, what, "mana", old.getMana(), def.mana());
            check(problems, what, "recharges", old.getRecharges(), def.recharges());
            check(problems, what, "factors", new SpellFactors(old.getSpellCostFactor(), old.getSpellEffectAmountFactor(),
                    old.getSpellDurationFactor(), old.getSpellCooldownFactor(), old.getSpellRangeFactor()), def.factors());
        }

        // every gem in a tier tag has a gemstone entry with that tier and the rarity of its rarity tag
        TIER_TAGS.forEach((tierId, tag) -> BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(itemHolder -> {
            Item item = itemHolder.value();
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            Optional<Holder.Reference<GemstoneDef>> entry = JewelryDataIndex.gemstone(item);
            if (entry.isEmpty()) {
                problems.add("item " + itemId + " is in tag " + tag.location() + " but has no gemstone");
                return;
            }
            GemstoneDef gem = entry.get().value();
            String what = "gemstone " + entry.get().key().location();
            check(problems, what, "tier", Optional.of(tierId), gem.tier().unwrapKey().map(ResourceKey::location));
            Optional<MagicTreasuresRarity> tagRarity = RARITY_TAGS.entrySet().stream()
                    .filter(e -> itemHolder.is(e.getValue())).map(Map.Entry::getKey).findFirst();
            check(problems, what, "rarity", tagRarity, gem.rarity());
        }));

        // and no built-in gemstone exists that the tags don't know about (pack gems aren't in the 1.x tags)
        JewelryDataIndex.gemstones().forEach((id, holder) -> {
            if (!BUILT_IN_NAMESPACES.contains(id.getNamespace())) {
                return;
            }
            Holder<Item> item = BuiltInRegistries.ITEM.wrapAsHolder(BuiltInRegistries.ITEM.get(holder.value().item()));
            if (TIER_TAGS.values().stream().noneMatch(item::is)) {
                problems.add("gemstone " + id + ": item " + holder.value().item() + " is in no tier tag");
            }
        });
        // the combination rules allow exactly the 1.x combination items: JewelryBuilder made 270 base (5 materials
        // x 3 types x regular/great x 9 gem options) + 27 wood (regular only) + 90 lords (ring and necklace only)
        List<String> materials = List.of("wood", "iron", "copper", "silver", "gold", "bone");
        List<String> gems = List.of("topaz", "onyx", "jadeite", "ruby", "white_pearl", "sapphire", "black_pearl");
        int allowed = 0;
        for (String materialPath : materials) {
            Optional<Holder.Reference<JewelryMaterialDef>> material = JewelryDataIndex.material(mt(materialPath));
            if (material.isEmpty()) {
                continue;
            }
            for (Holder.Reference<JewelryStyleDef> style : JewelryDataIndex.styles().values()) {
                if (!style.key().location().getNamespace().equals(MagicTreasures.MOD_ID)) {
                    continue; // a pack's style isn't a 1.x size
                }
                for (String type : List.of("ring", "necklace", "bracelet")) {
                    List<Optional<Holder<GemstoneDef>>> gemOptions = new ArrayList<>();
                    gemOptions.add(Optional.empty());
                    JewelryDataIndex.gemstone(ResourceLocation.withDefaultNamespace("diamond")).ifPresent(h -> gemOptions.add(Optional.of(h)));
                    gems.forEach(gem -> JewelryDataIndex.gemstone(mt(gem)).ifPresent(h -> gemOptions.add(Optional.of(h))));
                    for (Optional<Holder<GemstoneDef>> gem : gemOptions) {
                        if (JewelryRules.allows(type, material.get(), style, gem)) {
                            allowed++;
                        }
                    }
                }
            }
        }
        check(problems, "combination rules", "combinations of the 1.x materials and gems", 387, allowed);

        checkStats(problems);
        checkPresets(problems);
        checkLoot(problems);
        checkTable(problems);
        return problems;
    }

    /**
     * M6 checkpoint: per rarity, the built-in random loot ({@link JewelryLoot}) must be exactly as large as the 1.x
     * jewelry rarity tag that {@code JewelryRegistry.get(rarity)} rolled from, and roll the same gem items as the 1.x
     * stone rarity tag. A pack's content is left out (it wasn't in 1.x). Together with the stats and preset checks,
     * which prove each 1.x item's identity computes its tag's rarity, equal counts mean equal pools.
     */
    private static void checkLoot(List<String> problems) {
        int total = 0;
        for (MagicTreasuresRarity rarity : JEWELRY_RARITY_TAGS.keySet()) {
            int expected = 0;
            for (Holder<Item> ignored : BuiltInRegistries.ITEM.getTagOrEmpty(JEWELRY_RARITY_TAGS.get(rarity))) {
                expected++;
            }
            List<JewelryLoot.Candidate> candidates = JewelryLoot.jewelry().stream()
                    .filter(c -> c.rarity() == rarity && builtIn(c.identity()))
                    .toList();
            int weight = candidates.stream().mapToInt(JewelryLoot.Candidate::weight).sum();
            check(problems, "loot " + rarity.getName(), "jewelry candidates (by weight)", expected, weight);
            total += candidates.size();

            Set<ResourceLocation> oldGems = new java.util.TreeSet<>();
            Optional.ofNullable(RARITY_TAGS.get(rarity)).ifPresent(tag -> BuiltInRegistries.ITEM.getTagOrEmpty(tag)
                    .forEach(item -> oldGems.add(BuiltInRegistries.ITEM.getKey(item.value()))));
            Set<ResourceLocation> newGems = new java.util.TreeSet<>();
            JewelryLoot.gemstones().stream()
                    .filter(gem -> gem.value().rarity().equals(Optional.of(rarity))
                            && BUILT_IN_NAMESPACES.contains(gem.key().location().getNamespace()))
                    .forEach(gem -> newGems.add(gem.value().item()));
            check(problems, "loot " + rarity.getName(), "gems", oldGems, newGems);
        }
        // 387 combinations + 15 named + 13 hawk + 3 castle (one preset, three gem options)
        check(problems, "loot", "built-in jewelry candidates", 418, total);
    }

    /**
     * M0: the legacy table M9 converts with covers every 1.x jewelry item once, gives each a different identity, and
     * names only gems and presets that exist. (Stats and presets above already compute through the same mapping.)
     */
    private static void checkTable(List<String> problems) {
        Map<ResourceLocation, JewelryIdentity> table = LegacyJewelry.identities();
        check(problems, "legacy table", "items", 418, table.size());
        check(problems, "legacy table", "distinct identities", table.size(), (int) table.values().stream().distinct().count());
        table.forEach((id, identity) -> {
            identity.gem().filter(gem -> JewelryDataIndex.gemstone(gem).isEmpty())
                    .ifPresent(gem -> problems.add("legacy table " + id + ": gem " + gem + " has no gemstone"));
            identity.preset().filter(preset -> JewelryDataIndex.preset(preset).isEmpty())
                    .ifPresent(preset -> problems.add("legacy table " + id + ": preset " + preset + " missing"));
            if (!JewelryStats.of(identity).known()) {
                problems.add("legacy table " + id + ": unknown material or style");
            }
        });
    }

    private static boolean builtIn(JewelryIdentity identity) {
        return BUILT_IN_NAMESPACES.contains(identity.material().getNamespace())
                && identity.gem().map(gem -> BUILT_IN_NAMESPACES.contains(gem.getNamespace())).orElse(true)
                && identity.style().getNamespace().equals(MagicTreasures.MOD_ID)
                && identity.preset().map(preset -> preset.getNamespace().equals(MagicTreasures.MOD_ID)).orElse(true);
    }

    /**
     * M8 checkpoint: every 1.x named, hawk and castle item, as the preset a 2.0 stack would carry (the castle ring is
     * one preset whose gem varies), must compute the item's own 1.x stats, rarity and spells.
     */
    private static void checkPresets(List<String> problems) {
        List<DeferredItem<Item>> items = new ArrayList<>(MagicTreasuresItems.namedJewelry());
        for (DeferredItem<Item> deferred : MagicTreasuresItems.STANDARD_JEWELRY) {
            JewelryData data = JewelryHandler.get(new ItemStack(deferred.get())).map(h -> h.getStack().get(MagicTreasuresDataComponents.JEWELRY.get())).orElse(null);
            if (data != null && data.baseName().isPresent()) {
                items.add(deferred);
            }
        }
        int compared = 0;
        for (DeferredItem<Item> deferred : items) {
            ItemStack stack = new ItemStack(deferred.get());
            if (JewelryHandler.get(stack).isEmpty()) {
                continue;
            }
            JewelryData old = stack.get(MagicTreasuresDataComponents.JEWELRY.get());
            String what = "preset of " + deferred.getId();
            // the M0 table's identity: the item's own preset, or for a castle ring with a gem the gemless one + gem
            JewelryIdentity identity = LegacyJewelry.identity(deferred, MagicTreasuresItems.namedJewelry().contains(deferred));
            ResourceLocation presetId = identity.preset().orElse(null);
            Optional<Holder.Reference<JewelryPresetDef>> preset = presetId == null ? Optional.empty() : JewelryDataIndex.preset(presetId);
            if (preset.isEmpty()) {
                problems.add(what + ": preset " + presetId + " missing");
                continue;
            }
            JewelryPresetDef def = preset.get().value();
            JewelryStats stats = JewelryStats.compute(identity);
            JewelryData.Stats oldStats = old.stats();
            JewelryData.Factors oldFactors = old.factors();
            check(problems, what, "known", true, stats.known());
            check(problems, what, "max_uses", oldStats.maxUses(), stats.maxUses());
            check(problems, what, "max_level", oldStats.maxLevel(), stats.maxLevel());
            check(problems, what, "max_mana", oldStats.maxMana(), stats.maxMana());
            check(problems, what, "max_repairs", oldStats.maxRepairs(), stats.maxRepairs());
            check(problems, what, "max_recharges", oldStats.maxRecharges(), stats.maxRecharges());
            check(problems, what, "factors", new SpellFactors(oldFactors.spellCost(), oldFactors.spellEffectAmount(),
                    oldFactors.spellDuration(), oldFactors.spellCooldown(), oldFactors.spellRange()), stats.factors());
            check(problems, what, "spells", old.spells().stream().map(s -> s.name()).toList(), def.spells());
            Holder<Item> item = deferred.get().builtInRegistryHolder();
            Optional<MagicTreasuresRarity> tagRarity = JEWELRY_RARITY_TAGS.entrySet().stream()
                    .filter(e -> item.is(e.getValue())).map(Map.Entry::getKey).findFirst();
            check(problems, what, "rarity", tagRarity, Optional.of(stats.rarity()));
            compared++;
        }
        // 15 named + 13 hawk + 3 castle
        check(problems, "presets", "1.x named, hawk and castle items compared", 31, compared);
    }

    private static final Map<MagicTreasuresRarity, TagKey<Item>> JEWELRY_RARITY_TAGS = Map.of(
            MagicTreasuresRarity.COMMON, MagicTreasuresTags.Items.JEWELRY_COMMON,
            MagicTreasuresRarity.UNCOMMON, MagicTreasuresTags.Items.JEWELRY_UNCOMMON,
            MagicTreasuresRarity.SCARCE, MagicTreasuresTags.Items.JEWELRY_SCARCE,
            MagicTreasuresRarity.RARE, MagicTreasuresTags.Items.JEWELRY_RARE,
            MagicTreasuresRarity.EPIC, MagicTreasuresTags.Items.JEWELRY_EPIC,
            MagicTreasuresRarity.LEGENDARY, MagicTreasuresTags.Items.JEWELRY_LEGENDARY,
            MagicTreasuresRarity.MYTHICAL, MagicTreasuresTags.Items.JEWELRY_MYTHICAL);

    /**
     * M2 checkpoint: every 1.x combination item (no base name: not castle, hawk or named) gets the identity a 2.0
     * stack would have, and its computed stats must equal the item's own 1.x defaults, rarity tag included.
     */
    private static void checkStats(List<String> problems) {
        int compared = 0;
        for (DeferredItem<Item> deferred : MagicTreasuresItems.STANDARD_JEWELRY) {
            ItemStack stack = new ItemStack(deferred.get());
            if (JewelryHandler.get(stack).isEmpty()) {
                continue;
            }
            JewelryData old = stack.get(MagicTreasuresDataComponents.JEWELRY.get());
            if (old == null || old.baseName().isPresent()) {
                continue;
            }
            String what = "stats of " + deferred.getId();
            // the M0 table's identity (1.x writes minecraft:air for "no gem"; the table already reads it as none)
            JewelryIdentity identity = LegacyJewelry.identity(deferred, false);
            if (identity.gem().isPresent() && JewelryDataIndex.gemstone(identity.gem().get()).isEmpty()) {
                problems.add(what + ": gem " + identity.gem().get() + " has no gemstone");
                continue;
            }
            JewelryStats stats = JewelryStats.compute(identity);
            JewelryData.Stats oldStats = old.stats();
            JewelryData.Factors oldFactors = old.factors();
            check(problems, what, "known", true, stats.known());
            check(problems, what, "max_uses", oldStats.maxUses(), stats.maxUses());
            check(problems, what, "max_level", oldStats.maxLevel(), stats.maxLevel());
            check(problems, what, "max_mana", oldStats.maxMana(), stats.maxMana());
            check(problems, what, "max_repairs", oldStats.maxRepairs(), stats.maxRepairs());
            check(problems, what, "max_recharges", oldStats.maxRecharges(), stats.maxRecharges());
            check(problems, what, "factors", new SpellFactors(oldFactors.spellCost(), oldFactors.spellEffectAmount(),
                    oldFactors.spellDuration(), oldFactors.spellCooldown(), oldFactors.spellRange()), stats.factors());
            Holder<Item> item = deferred.get().builtInRegistryHolder();
            Optional<MagicTreasuresRarity> tagRarity = JEWELRY_RARITY_TAGS.entrySet().stream()
                    .filter(e -> item.is(e.getValue())).map(Map.Entry::getKey).findFirst();
            check(problems, what, "rarity", tagRarity, Optional.of(stats.rarity()));
            compared++;
        }
        check(problems, "stats", "1.x combination items compared", 387, compared);
    }

    private static ResourceLocation mt(String path) {
        return ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, path);
    }

    private static void check(List<String> problems, String what, String field, Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            problems.add(what + ": " + field + " is " + actual + ", expected " + expected);
        }
    }
}
