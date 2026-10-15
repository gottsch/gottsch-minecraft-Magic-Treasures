package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

/**
 * A jewelry material ({@code magictreasures:jewelry_material}), e.g. gold. The stats are the base values that the
 * gem's stone tier and the style add to.
 *
 * @param ingredient  what crafts it; absent = loot only
 * @param rarity      used for loot and the jewelry's rarity; absent for materials only presets use
 * @param color       RGB tint for the shared fallback art, when no texture exists for this material
 * @param acceptsGems whether a gem can be set in it
 * @param limits      types, styles and gems it can be made with ({@code types}, {@code styles}, {@code gems},
 *                    {@code requires_gem} at the top level of the JSON)
 * @param disabled    a pack can turn a built-in entry off by overriding its file with {@code "disabled": true}
 */
public record JewelryMaterialDef(
        Optional<Ingredient> ingredient,
        Optional<MagicTreasuresRarity> rarity,
        int color,
        int uses,
        int mana,
        int repairs,
        int recharges,
        int maxLevel,
        int spells,
        SpellFactors factors,
        boolean acceptsGems,
        MaterialLimits limits,
        boolean disabled) {

    public static final int DEFAULT_COLOR = 0xFFFFFF;

    public static final Codec<JewelryMaterialDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DataCodecs.INGREDIENT.optionalFieldOf("ingredient").forGetter(JewelryMaterialDef::ingredient),
            DataCodecs.RARITY.optionalFieldOf("rarity").forGetter(JewelryMaterialDef::rarity),
            DataCodecs.COLOR.optionalFieldOf("color", DEFAULT_COLOR).forGetter(JewelryMaterialDef::color),
            Codec.INT.fieldOf("uses").forGetter(JewelryMaterialDef::uses),
            Codec.INT.fieldOf("mana").forGetter(JewelryMaterialDef::mana),
            Codec.INT.optionalFieldOf("repairs", 0).forGetter(JewelryMaterialDef::repairs),
            Codec.INT.optionalFieldOf("recharges", 0).forGetter(JewelryMaterialDef::recharges),
            Codec.INT.optionalFieldOf("max_level", 1).forGetter(JewelryMaterialDef::maxLevel),
            Codec.INT.optionalFieldOf("spells", 1).forGetter(JewelryMaterialDef::spells),
            SpellFactors.CODEC.optionalFieldOf("factors", SpellFactors.NONE).forGetter(JewelryMaterialDef::factors),
            Codec.BOOL.optionalFieldOf("accepts_gems", true).forGetter(JewelryMaterialDef::acceptsGems),
            MaterialLimits.MAP_CODEC.forGetter(JewelryMaterialDef::limits),
            Codec.BOOL.optionalFieldOf("disabled", false).forGetter(JewelryMaterialDef::disabled)
    ).apply(instance, JewelryMaterialDef::new));
}
