package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.forge.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/**
 * A unique piece of jewelry ({@code magictreasures:jewelry_preset}), e.g. Angel's Ring. A preset names only what
 * differs; everything else is calculated from material + gem + style like any other piece.
 *
 * @param material       a jewelry_material id
 * @param gem            a gemstone id
 * @param style          a jewelry_style id
 * @param name           translation key of the display name; absent = composed like any piece (with base_name)
 * @param lore           translation key of the lore line ({@code ~} separates lines)
 * @param texture        the sprite to draw (e.g. magictreasures:item/jewelry/angels_ring); absent =
 *                       {@code <ns>:item/jewelry/<path>} if it exists, else composed
 * @param baseName       replaces the type in composed texture and name lookups: castle_ring gives
 *                       silver_ruby_castle_ring (art) and item.magictreasures.silver_ruby_castle_ring (name)
 * @param rarity         overrides the calculated rarity
 * @param spells         spells it starts with (spell ids)
 * @param overrides      stats that replace the computed ones
 * @param rules          {@code accepts_gems}, {@code extractable} and {@code gems}, as top-level keys
 * @param vanishingCurse gets Curse of Vanishing when first carried
 * @param loot           makes the preset eligible for random_jewelry rolls of its rarity
 */
public record JewelryPresetDef(
        String type,
        ResourceLocation material,
        Optional<ResourceLocation> gem,
        ResourceLocation style,
        Optional<String> name,
        Optional<String> lore,
        Optional<ResourceLocation> texture,
        Optional<String> baseName,
        Optional<MagicTreasuresRarity> rarity,
        List<ResourceLocation> spells,
        PresetOverrides overrides,
        Rules rules,
        boolean vanishingCurse,
        Optional<Loot> loot,
        boolean disabled) {

    public static final Codec<JewelryPresetDef> CODEC = codec(false);
    /** what the client receives on Forge 1.20.1: references to other jewelry registries as ids (NetworkHolderSet) */
    public static final Codec<JewelryPresetDef> NETWORK_CODEC = codec(true);

    /** {@code network}: refer to other jewelry registries by id (see NetworkHolderSet) */
    private static Codec<JewelryPresetDef> codec(boolean network) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("type").forGetter(JewelryPresetDef::type),
                ResourceLocation.CODEC.fieldOf("material").forGetter(JewelryPresetDef::material),
                ResourceLocation.CODEC.optionalFieldOf("gem").forGetter(JewelryPresetDef::gem),
                ResourceLocation.CODEC.optionalFieldOf("style", JewelryRules.REGULAR).forGetter(JewelryPresetDef::style),
                Codec.STRING.optionalFieldOf("name").forGetter(JewelryPresetDef::name),
                Codec.STRING.optionalFieldOf("lore").forGetter(JewelryPresetDef::lore),
                ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(JewelryPresetDef::texture),
                Codec.STRING.optionalFieldOf("base_name").forGetter(JewelryPresetDef::baseName),
                DataCodecs.RARITY.optionalFieldOf("rarity").forGetter(JewelryPresetDef::rarity),
                ResourceLocation.CODEC.listOf().optionalFieldOf("spells", List.of()).forGetter(JewelryPresetDef::spells),
                PresetOverrides.CODEC.optionalFieldOf("overrides", PresetOverrides.NONE).forGetter(JewelryPresetDef::overrides),
                Rules.mapCodec(network).forGetter(JewelryPresetDef::rules),
                Codec.BOOL.optionalFieldOf("vanishing_curse", false).forGetter(JewelryPresetDef::vanishingCurse),
                Loot.CODEC.optionalFieldOf("loot").forGetter(JewelryPresetDef::loot),
                Codec.BOOL.optionalFieldOf("disabled", false).forGetter(JewelryPresetDef::disabled)
        ).apply(instance, JewelryPresetDef::new));
    }

    public boolean acceptsGems() {
        return rules.acceptsGems();
    }

    public boolean extractable() {
        return rules.extractable();
    }

    /**
     * Gem rules, written as top-level keys.
     *
     * @param acceptsGems false: no gem may be set or removed on the anvil
     * @param extractable false: the pliers can't take the gem out
     * @param gems        the only gems that may be set (ids, a list or a #tag); checked with the material's rules
     */
    public record Rules(boolean acceptsGems, boolean extractable, Optional<HolderSet<GemstoneDef>> gems) {
        public static final Rules DEFAULT = new Rules(true, true, Optional.empty());

        public static final MapCodec<Rules> MAP_CODEC = mapCodec(false);
        /** what the client receives on Forge 1.20.1: references to other jewelry registries as ids (NetworkHolderSet) */
        public static final MapCodec<Rules> NETWORK_MAP_CODEC = mapCodec(true);

        /** {@code network}: refer to other jewelry registries by id (see NetworkHolderSet) */
        static MapCodec<Rules> mapCodec(boolean network) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("accepts_gems", true).forGetter(Rules::acceptsGems),
                    Codec.BOOL.optionalFieldOf("extractable", true).forGetter(Rules::extractable),
                    DataCodecs.holderSet(MagicTreasuresRegistries.GEMSTONE, network).optionalFieldOf("gems").forGetter(Rules::gems)
            ).apply(instance, Rules::new));
        }

        public boolean allowsGem(Holder<GemstoneDef> gem) {
            return gems.map(set -> set.contains(gem)).orElse(true);
        }
    }

    public record Loot(int weight) {
        public static final Codec<Loot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("weight", 1).forGetter(Loot::weight)
        ).apply(instance, Loot::new));
    }
}
