package mod.gottsch.neo.magic_treasures.datagen;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterial;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterials;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryStoneTier;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryStoneTiers;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.item.Jewelry;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.GemstoneDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryPresetDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.PresetOverrides;
import mod.gottsch.neo.magic_treasures.core.spell.MagicTreasuresSpells;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryMaterialDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryRules;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryStyleDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.MaterialLimits;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.SpellFactors;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.StoneTierDef;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.neo.magic_treasures.core.tag.MagicTreasuresTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ItemExistsCondition;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Generates the built-in 2.0 jewelry data (materials, styles, stone tiers, gemstones) from the 1.x constants, so the numbers
 * match exactly. The presets were generated from the 1.x items' builders until M9 deleted those; they are fixed JSON in
 * {@code src/main/resources/data/magictreasures/magictreasures/jewelry_preset/} now. The extra facts the constants don't hold (ingredient, rarity, color, sizes, gem membership) come
 * from today's recipes, item tags and art.
 * <p>
 * Gemstone ids are the gem's item id (minecraft:diamond, treasure2:ruby), so 1.x stacks, which store the item id,
 * convert without a table. Treasure2's gems carry a {@code neoforge:conditions} item-exists check and are skipped
 * without Treasure2.
 */
public class JewelryDataProvider extends DatapackBuiltinEntriesProvider {
    private static final String TREASURE2 = "treasure2";

    private static final List<JewelryStoneTier> TIERS = List.of(JewelryStoneTiers.TIER1, JewelryStoneTiers.TIER2,
            JewelryStoneTiers.TIER3, JewelryStoneTiers.TIER4, JewelryStoneTiers.TIER5, JewelryStoneTiers.TIER6);

    public JewelryDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, builder(), conditions(), Set.of(MagicTreasures.MOD_ID, "minecraft", TREASURE2));
    }

    /** Gems from other mods load only when their item exists. */
    private static Map<ResourceKey<?>, List<ICondition>> conditions() {
        Map<ResourceKey<?>, List<ICondition>> conditions = new HashMap<>();
        for (String gem : List.of("topaz", "onyx", "ruby", "white_pearl", "sapphire", "black_pearl")) {
            conditions.put(ResourceKey.create(MagicTreasuresRegistries.GEMSTONE, t2(gem)), List.of(new ItemExistsCondition(t2(gem))));
        }
        return conditions;
    }

    private static RegistrySetBuilder builder() {
        return new RegistrySetBuilder()
                .add(MagicTreasuresRegistries.JEWELRY_STYLE, JewelryDataProvider::styles)
                .add(MagicTreasuresRegistries.STONE_TIER, JewelryDataProvider::stoneTiers)
                .add(MagicTreasuresRegistries.JEWELRY_MATERIAL, JewelryDataProvider::materials)
                .add(MagicTreasuresRegistries.GEMSTONE, JewelryDataProvider::gemstones);
    }

    /*
     * styles: the 1.x sizes (JewelrySizeTier); max level and rarity steps were the size's code
     */
    private static void styles(BootstrapContext<JewelryStyleDef> context) {
        style(context, JewelrySizeTier.REGULAR, Optional.empty(), Optional.empty());
        style(context, JewelrySizeTier.GREAT, Optional.of(JewelryRules.REGULAR), Optional.empty());
        // lords only came as rings and necklaces
        style(context, JewelrySizeTier.LORDS, Optional.of(JewelryRules.REGULAR), Optional.of(List.of("ring", "necklace")));
    }

    private static void style(BootstrapContext<JewelryStyleDef> context, JewelrySizeTier size,
            Optional<ResourceLocation> textureFallback, Optional<List<String>> types) {
        boolean regular = size == JewelrySizeTier.REGULAR;
        context.register(ResourceKey.create(MagicTreasuresRegistries.JEWELRY_STYLE, mt(size.getValue())),
                new JewelryStyleDef(size.getUsesMultiplier(), size.getManaMultiplier(), size.getCode(), size.getRepairs(),
                        size.getCode(),
                        regular ? Optional.empty() : Optional.of("jewelry_style." + MagicTreasures.MOD_ID + "." + size.getValue()),
                        // regular has no affix (gold_ruby_ring); the others use the id path (great_gold_ruby_ring)
                        regular ? Optional.of("") : Optional.empty(),
                        textureFallback, types, false));
    }

    /*
     * stone tiers
     */
    private static void stoneTiers(BootstrapContext<StoneTierDef> context) {
        // Skeleton's Heart is left out: it was an unbuilt expansion idea with no gem item (Mark, 2026-10-11)
        TIERS.forEach(tier -> context.register(ResourceKey.create(MagicTreasuresRegistries.STONE_TIER, tier.getName()),
                new StoneTierDef(tier.getMana(), tier.getRecharges(), new SpellFactors(tier.getSpellCostFactor(),
                        tier.getSpellEffectAmountFactor(), tier.getSpellDurationFactor(), tier.getSpellCooldownFactor(),
                        tier.getSpellRangeFactor()), false)));
    }

    /*
     * materials
     */
    private static void materials(BootstrapContext<JewelryMaterialDef> context) {
        HolderGetter<JewelryStyleDef> styles = context.lookup(MagicTreasuresRegistries.JEWELRY_STYLE);
        // wood only came in regular; every other material in every style. No material limits types or gems today.
        MaterialLimits regularOnly = new MaterialLimits(Optional.empty(),
                Optional.of(HolderSet.direct(styles.getOrThrow(ResourceKey.create(MagicTreasuresRegistries.JEWELRY_STYLE, JewelryRules.REGULAR)))),
                Optional.empty(), false);
        material(context, JewelryMaterials.WOOD, null, MagicTreasuresRarity.COMMON, 0x7C633A, regularOnly);
        material(context, JewelryMaterials.IRON, Tags.Items.INGOTS_IRON, MagicTreasuresRarity.COMMON, 0x7C7C7C, MaterialLimits.NONE);
        material(context, JewelryMaterials.COPPER, Tags.Items.INGOTS_COPPER, MagicTreasuresRarity.UNCOMMON, 0xB65737, MaterialLimits.NONE);
        material(context, JewelryMaterials.SILVER, MagicTreasuresTags.Items.INGOTS_SILVER, MagicTreasuresRarity.SCARCE, 0x94A3AC, MaterialLimits.NONE);
        material(context, JewelryMaterials.GOLD, Tags.Items.INGOTS_GOLD, MagicTreasuresRarity.RARE, 0xD49821, MaterialLimits.NONE);
        material(context, JewelryMaterials.BONE, Tags.Items.BONES, MagicTreasuresRarity.SCARCE, 0xB4B194, MaterialLimits.NONE);
        // blood, shadow and atium: unplanned expansion materials that only named pieces use today. No rarity and
        // no limits until they are designed (Mark, 2026-10-11)
        material(context, JewelryMaterials.BLOOD, null, null, 0x640900, MaterialLimits.NONE);
        // the art is near black (#060606); lifted so a tinted fallback is still visible in a dark slot
        material(context, JewelryMaterials.SHADOW, null, null, 0x2E2B36, MaterialLimits.NONE);
        material(context, JewelryMaterials.ATIUM, null, null, 0x9C9870, MaterialLimits.NONE);
    }

    private static void material(BootstrapContext<JewelryMaterialDef> context, JewelryMaterial material,
            TagKey<Item> ingredient, MagicTreasuresRarity rarity, int color, MaterialLimits limits) {
        context.register(ResourceKey.create(MagicTreasuresRegistries.JEWELRY_MATERIAL, material.getId()),
                new JewelryMaterialDef(
                        Optional.ofNullable(ingredient).map(Ingredient::of),
                        Optional.ofNullable(rarity),
                        color,
                        material.getUses(),
                        material.getMana(),
                        material.getRepairs(),
                        material.getRecharges(),
                        material.getMaxLevel(),
                        material.getSpells(),
                        new SpellFactors(material.getSpellCostFactor(), material.getSpellEffectAmountFactor(),
                                material.getSpellDurationFactor(), material.getSpellCooldownFactor(),
                                material.getSpellRangeFactor()),
                        true,
                        limits,
                        false));
    }

    /*
     * gemstones: tier and rarity from the 1.x stone tier and stone rarity tags
     */
    private static void gemstones(BootstrapContext<GemstoneDef> context) {
        HolderGetter<StoneTierDef> tiers = context.lookup(MagicTreasuresRegistries.STONE_TIER);
        gem(context, tiers, mt("topaz"), JewelryStoneTiers.TIER1, MagicTreasuresRarity.COMMON, 0xD38F3C);
        gem(context, tiers, mt("onyx"), JewelryStoneTiers.TIER2, MagicTreasuresRarity.UNCOMMON, 0x373B3B);
        gem(context, tiers, ResourceLocation.withDefaultNamespace("diamond"), JewelryStoneTiers.TIER3, MagicTreasuresRarity.SCARCE, 0x3BC6BB);
        gem(context, tiers, mt("jadeite"), JewelryStoneTiers.TIER4, MagicTreasuresRarity.SCARCE, 0x018118);
        gem(context, tiers, mt("ruby"), JewelryStoneTiers.TIER5, MagicTreasuresRarity.RARE, 0xD0203A);
        gem(context, tiers, mt("white_pearl"), JewelryStoneTiers.TIER5, MagicTreasuresRarity.RARE, 0xDDE3F0);
        gem(context, tiers, mt("sapphire"), JewelryStoneTiers.TIER6, MagicTreasuresRarity.EPIC, 0x0353B3);
        gem(context, tiers, mt("black_pearl"), JewelryStoneTiers.TIER6, MagicTreasuresRarity.EPIC, 0x484E5F);

        // Treasure2's gems: same tiers, rarities and colors as MT's own
        gem(context, tiers, t2("topaz"), JewelryStoneTiers.TIER1, MagicTreasuresRarity.COMMON, 0xD38F3C);
        gem(context, tiers, t2("onyx"), JewelryStoneTiers.TIER2, MagicTreasuresRarity.UNCOMMON, 0x373B3B);
        gem(context, tiers, t2("ruby"), JewelryStoneTiers.TIER5, MagicTreasuresRarity.RARE, 0xD0203A);
        gem(context, tiers, t2("white_pearl"), JewelryStoneTiers.TIER5, MagicTreasuresRarity.RARE, 0xDDE3F0);
        gem(context, tiers, t2("sapphire"), JewelryStoneTiers.TIER6, MagicTreasuresRarity.EPIC, 0x0353B3);
        gem(context, tiers, t2("black_pearl"), JewelryStoneTiers.TIER6, MagicTreasuresRarity.EPIC, 0x484E5F);
    }

    private static void gem(BootstrapContext<GemstoneDef> context, HolderGetter<StoneTierDef> tiers, ResourceLocation item,
            JewelryStoneTier tier, MagicTreasuresRarity rarity, int color) {
        context.register(ResourceKey.create(MagicTreasuresRegistries.GEMSTONE, item), new GemstoneDef(item,
                tiers.getOrThrow(ResourceKey.create(MagicTreasuresRegistries.STONE_TIER, tier.getName())),
                Optional.of(rarity), color, Optional.empty(), false));
    }

    private static ResourceLocation mt(String path) {
        return ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, path);
    }

    private static ResourceLocation t2(String path) {
        return ResourceLocation.fromNamespaceAndPath(TREASURE2, path);
    }

    /**
     * Registry tags of {@code magictreasures:gemstone}.
     */
    public static class GemstoneTags extends TagsProvider<GemstoneDef> {

        public GemstoneTags(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
            super(output, MagicTreasuresRegistries.GEMSTONE, registries, MagicTreasures.MOD_ID, helper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            // as the 1.x castle_ring item tags; Treasure2's gems only load with Treasure2
            tag(MagicTreasuresRegistries.Tags.CASTLE_RING_GEMS)
                    .add(ResourceKey.create(MagicTreasuresRegistries.GEMSTONE, mt("ruby")),
                            ResourceKey.create(MagicTreasuresRegistries.GEMSTONE, mt("sapphire")))
                    .addOptional(t2("ruby"))
                    .addOptional(t2("sapphire"));
        }
    }

    /**
     * Registry tags of {@code magictreasures:jewelry_material}.
     */
    public static class MaterialTags extends TagsProvider<JewelryMaterialDef> {

        public MaterialTags(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
            super(output, MagicTreasuresRegistries.JEWELRY_MATERIAL, registries, MagicTreasures.MOD_ID, helper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(MagicTreasuresRegistries.Tags.METALS).add(
                    key(JewelryMaterials.IRON), key(JewelryMaterials.COPPER), key(JewelryMaterials.SILVER),
                    key(JewelryMaterials.GOLD), key(JewelryMaterials.BLOOD), key(JewelryMaterials.SHADOW),
                    key(JewelryMaterials.ATIUM));
        }

        private static ResourceKey<JewelryMaterialDef> key(JewelryMaterial material) {
            return ResourceKey.create(MagicTreasuresRegistries.JEWELRY_MATERIAL, material.getId());
        }
    }
}
