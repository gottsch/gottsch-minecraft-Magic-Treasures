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
 * M1/M2 checkpoint: compares the registry entries with the 1.x constants and tags they were generated from, and the
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
        return problems;
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
            Optional<ResourceLocation> gem = Optional.empty();
            // 1.x writes minecraft:air, not an empty stone, for jewelry without a gem (M9's conversion must too)
            if (old.stone().isPresent() && !old.stone().get().equals(BuiltInRegistries.ITEM.getKey(Items.AIR))) {
                gem = JewelryDataIndex.gemstone(BuiltInRegistries.ITEM.get(old.stone().get())).map(h -> h.key().location());
                if (gem.isEmpty()) {
                    problems.add(what + ": stone " + old.stone().get() + " has no gemstone");
                    continue;
                }
            }
            JewelryIdentity identity = new JewelryIdentity(old.type().toLowerCase(Locale.ROOT), old.material(), gem,
                    mt(old.sizeTier().toLowerCase(Locale.ROOT)), Optional.empty());
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
