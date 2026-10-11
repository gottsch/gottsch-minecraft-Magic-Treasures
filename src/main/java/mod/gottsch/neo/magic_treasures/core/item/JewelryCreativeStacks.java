package mod.gottsch.neo.magic_treasures.core.item;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.GemstoneDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryMaterialDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryPresetDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryRules;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryStyleDef;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The generic jewelry stacks of the creative tab, built from the registries the client has
 * ({@link CreativeModeTab.ItemDisplayParameters#holders()}), so they follow the server's datapacks.
 * <p>
 * Every combination {@link JewelryRules} allows is listed. The tab shows today's built-in set (the 387 1.x
 * combinations: built-in materials with a rarity, with MT's or vanilla gems or none), every preset, and every
 * combination with a pack's material or gem (Mark, 2026-10-11). The rest, i.e. Treasure2's duplicate gems and the
 * unplanned materials with no rarity (blood, shadow, atium), is in the search tab only.
 */
public final class JewelryCreativeStacks {
    /** namespaces whose content is built in; anything else came from a pack */
    private static final Set<String> BUILT_IN = Set.of(MagicTreasures.MOD_ID, "minecraft", "treasure2");
    /** built-in gems shown in the tab; Treasure2's are duplicates of MT's */
    private static final Set<String> TAB_GEM_NAMESPACES = Set.of(MagicTreasures.MOD_ID, "minecraft");

    public record Entry(ItemStack stack, TabVisibility visibility) {}

    private JewelryCreativeStacks() {}

    public static List<Entry> build(HolderLookup.Provider holders) {
        List<Holder.Reference<JewelryMaterialDef>> materials = enabled(holders, MagicTreasuresRegistries.JEWELRY_MATERIAL,
                JewelryMaterialDef::disabled, m -> m.rarity().map(MagicTreasuresRarity::getCode).orElse(99));
        List<Holder.Reference<JewelryStyleDef>> styles = enabled(holders, MagicTreasuresRegistries.JEWELRY_STYLE,
                JewelryStyleDef::disabled, JewelryStyleDef::raritySteps);
        List<Holder.Reference<GemstoneDef>> gems = enabled(holders, MagicTreasuresRegistries.GEMSTONE,
                GemstoneDef::disabled, g -> g.rarity().map(MagicTreasuresRarity::getCode).orElse(99)).stream()
                .filter(gem -> BuiltInRegistries.ITEM.containsKey(gem.value().item()))
                .filter(gem -> !gem.value().tier().value().disabled())
                .toList();

        List<Entry> entries = new ArrayList<>();
        // presets first: they are the named pieces
        enabled(holders, MagicTreasuresRegistries.JEWELRY_PRESET, JewelryPresetDef::disabled, p -> p.rarity()
                .map(MagicTreasuresRarity::getCode).orElse(99)).forEach(preset -> item(preset.value().type()).ifPresent(item -> {
            JewelryPresetDef def = preset.value();
            entries.add(new Entry(item.stack(new JewelryIdentity(def.type(), def.material(), def.gem(), def.style(),
                    Optional.of(preset.key().location()))), TabVisibility.PARENT_AND_SEARCH_TABS));
        }));

        for (DeferredItem<?> deferred : MagicTreasuresItems.GENERIC_JEWELRY) {
            GenericJewelry item = (GenericJewelry) deferred.get();
            String type = item.getJewelryType();
            for (Holder.Reference<JewelryMaterialDef> material : materials) {
                for (Holder.Reference<JewelryStyleDef> style : styles) {
                    List<Optional<Holder.Reference<GemstoneDef>>> options = new ArrayList<>();
                    options.add(Optional.empty());
                    gems.forEach(gem -> options.add(Optional.of(gem)));
                    for (Optional<Holder.Reference<GemstoneDef>> gem : options) {
                        if (!JewelryRules.allows(type, material, style, gem.map(Function.identity()))) {
                            continue;
                        }
                        JewelryIdentity identity = new JewelryIdentity(type, material.key().location(),
                                gem.map(g -> g.key().location()), style.key().location(), Optional.empty());
                        entries.add(new Entry(item.stack(identity), inTab(material, gem)
                                ? TabVisibility.PARENT_AND_SEARCH_TABS : TabVisibility.SEARCH_TAB_ONLY));
                    }
                }
            }
        }
        return entries;
    }

    private static boolean inTab(Holder.Reference<JewelryMaterialDef> material, Optional<Holder.Reference<GemstoneDef>> gem) {
        boolean packMaterial = !BUILT_IN.contains(material.key().location().getNamespace());
        boolean packGem = gem.map(g -> !BUILT_IN.contains(g.key().location().getNamespace())).orElse(false);
        if (packMaterial || packGem) {
            return true;
        }
        return material.value().rarity().isPresent()
                && gem.map(g -> TAB_GEM_NAMESPACES.contains(g.key().location().getNamespace())).orElse(true);
    }

    private static Optional<GenericJewelry> item(String type) {
        return MagicTreasuresItems.GENERIC_JEWELRY.stream().map(DeferredItem::get)
                .map(GenericJewelry.class::cast)
                .filter(item -> item.getJewelryType().equals(type))
                .findFirst();
    }

    /** enabled entries, ordered by the given rank (rarity etc.) then id */
    private static <T> List<Holder.Reference<T>> enabled(HolderLookup.Provider holders, ResourceKey<? extends net.minecraft.core.Registry<? extends T>> key,
            Predicate<T> disabled, Function<T, Integer> rank) {
        return holders.lookup(key).map(lookup -> lookup.listElements()
                        .filter(holder -> !disabled.test(holder.value()))
                        .sorted(Comparator.<Holder.Reference<T>>comparingInt(holder -> rank.apply(holder.value()))
                                .thenComparing(holder -> holder.key().location()))
                        .map(holder -> (Holder.Reference<T>) holder)
                        .toList())
                .orElse(List.of());
    }
}
