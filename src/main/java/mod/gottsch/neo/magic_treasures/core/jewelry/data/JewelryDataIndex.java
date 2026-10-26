package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Fast lookups over the 2.0 datapack registries: id → entry, and gem item → gemstone. Disabled entries are left
 * out, and so are gemstones whose item doesn't exist or whose stone tier is disabled.
 * <p>
 * Built from the {@link RegistryAccess} when tags are bound: on the server when it loads its data, and on a remote
 * client when it receives the server's registries. In single player only the server side builds it (both sides see
 * the same entries). Cleared when the server stops.
 */
@EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JewelryDataIndex {

    private record Snapshot(
            Map<ResourceLocation, Holder.Reference<JewelryMaterialDef>> materials,
            Map<ResourceLocation, Holder.Reference<JewelryStyleDef>> styles,
            Map<ResourceLocation, Holder.Reference<StoneTierDef>> tiers,
            Map<ResourceLocation, Holder.Reference<GemstoneDef>> gemstones,
            Map<Item, Holder.Reference<GemstoneDef>> gemstonesByItem,
            Map<ResourceLocation, Holder.Reference<JewelryPresetDef>> presets) {

        static final Snapshot EMPTY = new Snapshot(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
    }

    private static volatile Snapshot snapshot = Snapshot.EMPTY;
    /** changes on every build and clear, so caches derived from the data (the item model) know to reset */
    private static volatile int generation;

    private JewelryDataIndex() {}

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.shouldUpdateStaticData()) {
            build(event.getRegistryAccess());
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        clear();
    }

    // NOTE no clear on ClientPlayerNetworkEvent.LoggingOut: Minecraft.doWorldLoad() calls disconnect(), which fires
    // LoggingOut *after* the single player server has loaded its data and built this index, and wiped it. A remote
    // client keeps the last server's entries until the next server's registries arrive and rebuild it.

    public static void build(RegistryAccess access) {
        Map<ResourceLocation, Holder.Reference<JewelryMaterialDef>> materials =
                enabled(access, MagicTreasuresRegistries.JEWELRY_MATERIAL, JewelryMaterialDef::disabled);
        Map<ResourceLocation, Holder.Reference<JewelryStyleDef>> styles =
                enabled(access, MagicTreasuresRegistries.JEWELRY_STYLE, JewelryStyleDef::disabled);
        Map<ResourceLocation, Holder.Reference<StoneTierDef>> tiers =
                enabled(access, MagicTreasuresRegistries.STONE_TIER, StoneTierDef::disabled);
        Map<ResourceLocation, Holder.Reference<JewelryPresetDef>> presets =
                enabled(access, MagicTreasuresRegistries.JEWELRY_PRESET, JewelryPresetDef::disabled);

        Map<ResourceLocation, Holder.Reference<GemstoneDef>> gemstones = new LinkedHashMap<>();
        Map<Item, Holder.Reference<GemstoneDef>> gemstonesByItem = new LinkedHashMap<>();
        enabled(access, MagicTreasuresRegistries.GEMSTONE, GemstoneDef::disabled).forEach((id, holder) -> {
            GemstoneDef gem = holder.value();
            Optional<Item> item = BuiltInRegistries.ITEM.getOptional(gem.item());
            if (item.isEmpty()) {
                MagicTreasures.LOGGER.warn("gemstone {}: item {} doesn't exist; skipping it", id, gem.item());
                return;
            }
            if (gem.tier().value().disabled()) {
                MagicTreasures.LOGGER.warn("gemstone {}: its stone tier {} is disabled; skipping it", id,
                        gem.tier().unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElse("(inline)"));
                return;
            }
            Holder.Reference<GemstoneDef> previous = gemstonesByItem.putIfAbsent(item.get(), holder);
            if (previous != null) {
                MagicTreasures.LOGGER.warn("gemstone {}: item {} already belongs to gemstone {}; skipping it", id,
                        gem.item(), previous.key().location());
                return;
            }
            gemstones.put(id, holder);
        });

        snapshot = new Snapshot(
                Collections.unmodifiableMap(materials),
                Collections.unmodifiableMap(styles),
                Collections.unmodifiableMap(tiers),
                Collections.unmodifiableMap(gemstones),
                Collections.unmodifiableMap(gemstonesByItem),
                Collections.unmodifiableMap(presets));
        generation++;
        MagicTreasures.LOGGER.info("jewelry data index built: {} materials, {} styles, {} stone tiers, {} gemstones, {} presets",
                materials.size(), styles.size(), tiers.size(), gemstones.size(), presets.size());
    }

    public static void clear() {
        snapshot = Snapshot.EMPTY;
        generation++;
    }

    public static int generation() {
        return generation;
    }

    private static <T> Map<ResourceLocation, Holder.Reference<T>> enabled(RegistryAccess access,
            ResourceKey<Registry<T>> key, Predicate<T> disabled) {
        Map<ResourceLocation, Holder.Reference<T>> map = new LinkedHashMap<>();
        access.registry(key).ifPresent(registry -> registry.holders()
                .filter(holder -> !disabled.test(holder.value()))
                .sorted((a, b) -> a.key().location().compareTo(b.key().location()))
                .forEach(holder -> map.put(holder.key().location(), holder)));
        return map;
    }

    /*
     * lookups
     */
    public static Optional<Holder.Reference<JewelryMaterialDef>> material(ResourceLocation id) {
        return Optional.ofNullable(snapshot.materials().get(id));
    }

    public static Optional<Holder.Reference<JewelryStyleDef>> style(ResourceLocation id) {
        return Optional.ofNullable(snapshot.styles().get(id));
    }

    public static Optional<Holder.Reference<StoneTierDef>> stoneTier(ResourceLocation id) {
        return Optional.ofNullable(snapshot.tiers().get(id));
    }

    public static Optional<Holder.Reference<GemstoneDef>> gemstone(ResourceLocation id) {
        return Optional.ofNullable(snapshot.gemstones().get(id));
    }

    public static Optional<Holder.Reference<GemstoneDef>> gemstone(Item item) {
        return Optional.ofNullable(snapshot.gemstonesByItem().get(item));
    }

    /** whether the stack is an enabled gemstone's item (the 2.0 test; 1.x jewelry still uses the all_stones tag) */
    public static boolean isGemstone(net.minecraft.world.item.ItemStack stack) {
        return !stack.isEmpty() && gemstone(stack.getItem()).isPresent();
    }

    public static Optional<Holder.Reference<JewelryPresetDef>> preset(ResourceLocation id) {
        return Optional.ofNullable(snapshot.presets().get(id));
    }

    public static Map<ResourceLocation, Holder.Reference<JewelryMaterialDef>> materials() {
        return snapshot.materials();
    }

    public static Map<ResourceLocation, Holder.Reference<JewelryStyleDef>> styles() {
        return snapshot.styles();
    }

    public static Map<ResourceLocation, Holder.Reference<StoneTierDef>> stoneTiers() {
        return snapshot.tiers();
    }

    public static Map<ResourceLocation, Holder.Reference<GemstoneDef>> gemstones() {
        return snapshot.gemstones();
    }

    public static Map<ResourceLocation, Holder.Reference<JewelryPresetDef>> presets() {
        return snapshot.presets();
    }
}
