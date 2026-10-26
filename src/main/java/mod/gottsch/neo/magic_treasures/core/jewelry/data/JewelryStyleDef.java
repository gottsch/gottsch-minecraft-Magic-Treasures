package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/**
 * A jewelry style ({@code magictreasures:jewelry_style}): what 1.x called size (regular, great, lords), generalized.
 * A style scales the material's and gem's stats, and selects its own art. A style that shares another's shape (great
 * shares regular's gem position) falls back to that style's art; a style with its own shape has its own band, gem
 * layers and fallback art.
 *
 * @param usesMultiplier  max uses = material uses × this
 * @param manaMultiplier  max mana = (material mana + tier mana) × this
 * @param maxLevel        added to the material's max level
 * @param repairs         added to the material's repairs
 * @param raritySteps     rarity steps added to the calculated rarity
 * @param name            translation key of the name word ("Great"); absent = no word (regular)
 * @param textureAffix    texture name affix: {@code great_gold_ruby_ring}, {@code ring/gold_great}. Absent = the id's
 *                        path; {@code ""} = none (regular)
 * @param textureFallback a style whose art to use where this style has none
 * @param types           jewelry types this style exists for; absent = all
 * @param disabled        a pack can turn a built-in entry off by overriding its file with {@code "disabled": true}
 */
public record JewelryStyleDef(
        double usesMultiplier,
        double manaMultiplier,
        int maxLevel,
        int repairs,
        int raritySteps,
        Optional<String> name,
        Optional<String> textureAffix,
        Optional<ResourceLocation> textureFallback,
        Optional<List<String>> types,
        boolean disabled) {

    public static final Codec<JewelryStyleDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("uses_multiplier", 1.0).forGetter(JewelryStyleDef::usesMultiplier),
            Codec.DOUBLE.optionalFieldOf("mana_multiplier", 1.0).forGetter(JewelryStyleDef::manaMultiplier),
            Codec.INT.optionalFieldOf("max_level", 0).forGetter(JewelryStyleDef::maxLevel),
            Codec.INT.optionalFieldOf("repairs", 0).forGetter(JewelryStyleDef::repairs),
            Codec.INT.optionalFieldOf("rarity_steps", 0).forGetter(JewelryStyleDef::raritySteps),
            Codec.STRING.optionalFieldOf("name").forGetter(JewelryStyleDef::name),
            Codec.STRING.optionalFieldOf("texture_affix").forGetter(JewelryStyleDef::textureAffix),
            ResourceLocation.CODEC.optionalFieldOf("texture_fallback").forGetter(JewelryStyleDef::textureFallback),
            Codec.STRING.listOf().optionalFieldOf("types").forGetter(JewelryStyleDef::types),
            Codec.BOOL.optionalFieldOf("disabled", false).forGetter(JewelryStyleDef::disabled)
    ).apply(instance, JewelryStyleDef::new));

    /** the texture affix of a style with this id: "" for none */
    public String affix(ResourceLocation id) {
        return textureAffix.orElse(id.getPath());
    }

    public boolean allowsType(String type) {
        return types.map(list -> list.contains(type)).orElse(true);
    }
}
