package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.google.gson.JsonElement;
import mod.gottsch.forge.magic_treasures.core.capability.JewelryCapability;
import mod.gottsch.forge.magic_treasures.core.capability.MagicTreasuresCapabilities;
import mod.gottsch.forge.magic_treasures.core.component.SpellData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.forge.magic_treasures.core.component.JewelryState;
import mod.gottsch.forge.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.forge.gottschcore.nbt.StackData;
import mod.gottsch.forge.magic_treasures.core.item.GenericJewelry;
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
 * comes from its 1.x item capability's saved NBT (see JewelryCapability):
 * <ul>
 *     <li>its stone wins over the table's gem: the 1.x anvil could swap a gem in place (Ring of Fortitude) or set one
 *     on a plain item (#14.1). {@code minecraft:air} and {@code minecraft:bedrock} mean no gem;</li>
 *     <li>uses, mana, repairs, recharges and spells (with their cooldowns) become the {@code jewelry_state}. Max values
 *     aren't copied: 2.0 computes them, and reads cap anything above;</li>
 *     <li>the rest of the stack tag (custom name, enchantments, Curse of Vanishing) is kept.</li>
 * </ul>
 * A stack never read by a 1.x handler has no {@code jewelry} component: it gets the table's identity and fills its
 * state on first read, like a /give.
 */
public final class LegacyConversion {
    /* the 1.x Forge handler's NBT keys (1.4.0 JewelryHandler.serializeNBT; SpellEntity / CooldownSpellEntity.save) */
    private static final String STONE = "stone";
    private static final String USES = "uses";
    private static final String MANA = "mana";
    private static final String REPAIRS = "repairs";
    private static final String RECHARGES = "recharges";
    private static final String SPELLS = "spells";
    private static final String SPELL_NAME = "name";
    private static final String SPELL_COOLDOWN = "cooldownExpireTime";
    /** 1.x put the handler's state here in the copy of the tag it sent to clients; never kept */
    private static final String SHARE_TAG = "magictreasures_jewelry";

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
                    .map(ResourceLocation::new).sorted().toList();
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    /**
     * The 1.x item ids by jewelry type ({@code ring}, {@code necklace}, {@code bracelet}), read straight from the
     * table. Datagen puts them in the Curios slot tags: Curios moves an item its slot doesn't accept out of the slot
     * when a player loads, before the conversion has run.
     */
    public static Map<String, java.util.List<ResourceLocation>> idsByType() {
        try (InputStream in = LegacyConversion.class.getResourceAsStream(TABLE)) {
            if (in == null) {
                throw new IllegalStateException("legacy jewelry table " + TABLE + " not found");
            }
            Map<String, java.util.List<ResourceLocation>> byType = new java.util.TreeMap<>();
            for (Map.Entry<String, JsonElement> entry : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject().entrySet()) {
                String type = entry.getValue().getAsJsonObject().get("type").getAsString();
                byType.computeIfAbsent(type, t -> new java.util.ArrayList<>()).add(new ResourceLocation(entry.getKey()));
            }
            byType.values().forEach(ids -> ids.sort(null));
            return byType;
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
        // 1.x state: the item capability's saved NBT (ForgeCaps.Parent), kept by the legacy item's JewelryCapability
        CompoundTag old = stack.getCapability(MagicTreasuresCapabilities.LEGACY_JEWELRY)
                .map(JewelryCapability::data).filter(tag -> !tag.isEmpty()).orElse(null);
        if (old != null) {
            Optional<ResourceLocation> stone = old.contains(STONE) ? Optional.ofNullable(ResourceLocation.tryParse(old.getString(STONE)))
                    : Optional.empty();
            identity = new JewelryIdentity(identity.type(), identity.material(), gem(stone), identity.style(), identity.preset());
        }
        // every other part of the stack tag (custom name, enchantments, Curse of Vanishing) is kept
        ItemStack converted = new ItemStack(item.get(), stack.getCount());
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag().copy();
            tag.remove(SHARE_TAG);
            converted.setTag(tag.isEmpty() ? null : tag);
        }
        StackData.set(converted, MagicTreasuresDataComponents.JEWELRY_IDENTITY, identity);
        if (old != null) {
            List<SpellData> spells = new ArrayList<>();
            for (Tag spellTag : old.getList(SPELLS, Tag.TAG_COMPOUND)) {
                CompoundTag spell = (CompoundTag) spellTag;
                ResourceLocation name = ResourceLocation.tryParse(spell.getString(SPELL_NAME));
                if (name != null) {
                    spells.add(new SpellData(name, spell.getDouble(SPELL_COOLDOWN)));
                }
            }
            StackData.set(converted, MagicTreasuresDataComponents.JEWELRY_STATE, new JewelryState(old.getInt(USES),
                    old.getDouble(MANA), old.getInt(REPAIRS), old.getInt(RECHARGES), spells));
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
                Optional<Item> item = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(entry.getKey()));
                JewelryIdentity identity = JewelryIdentity.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow(false, error -> {});
                item.ifPresent(i -> map.put(i, identity));
            }
            MagicTreasures.LOGGER.info("legacy jewelry table: {} items", map.size());
        } catch (Exception e) {
            MagicTreasures.LOGGER.error("legacy jewelry table {} unreadable: 1.x jewelry won't convert", TABLE, e);
        }
        return Map.copyOf(map);
    }
}
