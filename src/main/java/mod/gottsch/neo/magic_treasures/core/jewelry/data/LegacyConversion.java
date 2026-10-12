package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.component.JewelryData;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.JewelryState;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.item.GenericJewelry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * M9: turns a 1.x jewelry stack into 2.0 generic jewelry. What the item is comes from the M0 table
 * ({@code data/magictreasures/legacy/jewelry.json}, read from the jar, not a datapack); what the stack has been through
 * comes from its 1.x {@code jewelry} component:
 * <ul>
 *     <li>its stone wins over the table's gem: the 1.x anvil could swap a gem in place (Ring of Fortitude) or set one
 *     on a plain item (#14.1). {@code minecraft:air} and {@code minecraft:bedrock} mean no gem;</li>
 *     <li>uses, mana, repairs, recharges and spells (with their cooldowns) become the {@code jewelry_state}. Max values
 *     aren't copied: 2.0 computes them, and reads cap anything above;</li>
 *     <li>every other component (custom name, enchantments, Curse of Vanishing) is kept ({@code transmuteCopy}).</li>
 * </ul>
 * A stack never read by a 1.x handler has no {@code jewelry} component: it gets the table's identity and fills its
 * state on first read, like a /give.
 */
public final class LegacyConversion {
    public static final String TABLE = "/data/" + MagicTreasures.MOD_ID + "/legacy/jewelry.json";
    private static final ResourceLocation AIR = BuiltInRegistries.ITEM.getKey(Items.AIR);
    private static final ResourceLocation BEDROCK = BuiltInRegistries.ITEM.getKey(Items.BEDROCK);

    private static volatile Map<Item, JewelryIdentity> table;

    private LegacyConversion() {}

    /**
     * The 418 1.x item ids, read straight from the table (no item lookup), for registering the legacy items while
     * the item registry is being built.
     */
    public static java.util.List<ResourceLocation> ids() {
        try (InputStream in = LegacyConversion.class.getResourceAsStream(TABLE)) {
            if (in == null) {
                throw new IllegalStateException("legacy jewelry table " + TABLE + " not found");
            }
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject().keySet().stream()
                    .map(ResourceLocation::parse).sorted().toList();
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    /** whether the item is one of the 418 1.x jewelry items */
    public static boolean isLegacy(Item item) {
        return table().containsKey(item);
    }

    /** the converted stack, or empty if this isn't a 1.x jewelry stack */
    public static Optional<ItemStack> convert(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        JewelryIdentity tableIdentity = table().get(stack.getItem());
        if (tableIdentity == null) {
            return Optional.empty();
        }
        Optional<GenericJewelry> item = GenericJewelry.forType(tableIdentity.type());
        if (item.isEmpty()) {
            return Optional.empty();
        }
        JewelryIdentity identity = tableIdentity;
        JewelryData old = stack.get(MagicTreasuresDataComponents.JEWELRY.get());
        if (old != null) {
            identity = new JewelryIdentity(identity.type(), identity.material(), gem(old.stone()), identity.style(), identity.preset());
        }
        ItemStack converted = stack.transmuteCopy(item.get(), stack.getCount());
        converted.remove(MagicTreasuresDataComponents.JEWELRY.get());
        converted.set(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get(), identity);
        if (old != null) {
            JewelryData.Stats stats = old.stats();
            converted.set(MagicTreasuresDataComponents.JEWELRY_STATE.get(),
                    new JewelryState(stats.uses(), stats.mana(), stats.repairs(), stats.recharges(), old.spells()));
        }
        return Optional.of(converted);
    }

    /** a 1.x stone (an item id) as a gemstone id; a pack's gemstone id can differ from its item's */
    private static Optional<ResourceLocation> gem(Optional<ResourceLocation> stone) {
        return stone.filter(id -> !id.equals(AIR) && !id.equals(BEDROCK))
                .map(id -> JewelryDataIndex.gemstone(BuiltInRegistries.ITEM.get(id)).map(h -> h.key().location()).orElse(id));
    }

    private static Map<Item, JewelryIdentity> table() {
        Map<Item, JewelryIdentity> loaded = table;
        if (loaded == null) {
            loaded = load();
            table = loaded;
        }
        return loaded;
    }

    private static Map<Item, JewelryIdentity> load() {
        Map<Item, JewelryIdentity> map = new HashMap<>();
        try (InputStream in = LegacyConversion.class.getResourceAsStream(TABLE)) {
            if (in == null) {
                MagicTreasures.LOGGER.error("legacy jewelry table {} not found: 1.x jewelry won't convert", TABLE);
                return map;
            }
            JsonObject json = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                Optional<Item> item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(entry.getKey()));
                JewelryIdentity identity = JewelryIdentity.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                item.ifPresent(i -> map.put(i, identity));
            }
            MagicTreasures.LOGGER.info("legacy jewelry table: {} items", map.size());
        } catch (Exception e) {
            MagicTreasures.LOGGER.error("legacy jewelry table {} unreadable: 1.x jewelry won't convert", TABLE, e);
        }
        return Map.copyOf(map);
    }
}
