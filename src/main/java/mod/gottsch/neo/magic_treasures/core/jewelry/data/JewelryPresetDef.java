package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * A unique piece of jewelry ({@code magictreasures:jewelry_preset}), e.g. Angel's Ring. A preset names only what
 * differs; everything else is calculated from material + gem + style like any other piece.
 * Spells and stat overrides are added in M8. Unknown keys are ignored, so JSON written for M8 already loads.
 *
 * @param material    a jewelry_material id
 * @param gem         a gemstone id
 * @param style       a jewelry_style id
 * @param name        translation key of the display name
 * @param lore        translation key of the lore line
 * @param model       an item model to draw instead of the composed one
 * @param rarity      overrides the calculated rarity
 * @param extractable false stops pliers taking the gem out
 * @param loot        makes the preset eligible for random_jewelry rolls of its rarity
 */
public record JewelryPresetDef(
        String type,
        ResourceLocation material,
        Optional<ResourceLocation> gem,
        ResourceLocation style,
        Optional<String> name,
        Optional<String> lore,
        Optional<ResourceLocation> model,
        Optional<MagicTreasuresRarity> rarity,
        boolean acceptsGems,
        boolean extractable,
        Optional<Loot> loot,
        boolean disabled) {

    public static final Codec<JewelryPresetDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(JewelryPresetDef::type),
            ResourceLocation.CODEC.fieldOf("material").forGetter(JewelryPresetDef::material),
            ResourceLocation.CODEC.optionalFieldOf("gem").forGetter(JewelryPresetDef::gem),
            ResourceLocation.CODEC.optionalFieldOf("style", JewelryRules.REGULAR).forGetter(JewelryPresetDef::style),
            Codec.STRING.optionalFieldOf("name").forGetter(JewelryPresetDef::name),
            Codec.STRING.optionalFieldOf("lore").forGetter(JewelryPresetDef::lore),
            ResourceLocation.CODEC.optionalFieldOf("model").forGetter(JewelryPresetDef::model),
            DataCodecs.RARITY.optionalFieldOf("rarity").forGetter(JewelryPresetDef::rarity),
            Codec.BOOL.optionalFieldOf("accepts_gems", true).forGetter(JewelryPresetDef::acceptsGems),
            Codec.BOOL.optionalFieldOf("extractable", true).forGetter(JewelryPresetDef::extractable),
            Loot.CODEC.optionalFieldOf("loot").forGetter(JewelryPresetDef::loot),
            Codec.BOOL.optionalFieldOf("disabled", false).forGetter(JewelryPresetDef::disabled)
    ).apply(instance, JewelryPresetDef::new));

    public record Loot(int weight) {
        public static final Codec<Loot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("weight", 1).forGetter(Loot::weight)
        ).apply(instance, Loot::new));
    }
}
