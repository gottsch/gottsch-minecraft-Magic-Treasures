package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.crafting.Ingredient;
import com.mojang.serialization.DataResult;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;

import java.util.Arrays;
import java.util.Locale;

/**
 * Small codecs shared by the jewelry data records.
 */
public final class DataCodecs {

    /** An RGB color written as {@code "#RRGGBB"}. */
    public static final Codec<Integer> COLOR = Codec.STRING.comapFlatMap(DataCodecs::parseColor,
            rgb -> String.format(Locale.ROOT, "#%06X", rgb & 0xFFFFFF));

    /** A rarity by its lowercase value, e.g. {@code "scarce"}. NONE is not a valid value. */
    public static final Codec<MagicTreasuresRarity> RARITY = Codec.STRING.comapFlatMap(DataCodecs::parseRarity,
            MagicTreasuresRarity::getValue);

    /**
     * An ingredient in its usual JSON form ({@code {"tag": "forge:ingots/iron"}}, an item, or a list). 1.20.1 has no
     * {@code Ingredient.CODEC}, so this goes through the JSON parser (Forge's custom ingredient types included); like
     * 1.21.1's {@code CODEC_NONEMPTY}, an empty ingredient is an error.
     */
    public static final Codec<Ingredient> INGREDIENT = Codec.PASSTHROUGH.comapFlatMap(DataCodecs::parseIngredient,
            ingredient -> new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()));

    private DataCodecs() {}

    private static DataResult<Ingredient> parseIngredient(Dynamic<?> dynamic) {
        try {
            Ingredient ingredient = Ingredient.fromJson(dynamic.convert(JsonOps.INSTANCE).getValue(), false);
            return DataResult.success(ingredient);
        } catch (RuntimeException e) {
            return DataResult.error(() -> "Bad ingredient: " + e.getMessage());
        }
    }

    private static DataResult<Integer> parseColor(String value) {
        String hex = value.startsWith("#") ? value.substring(1) : value;
        if (hex.length() != 6) {
            return DataResult.error(() -> "Color must be #RRGGBB: " + value);
        }
        try {
            return DataResult.success(Integer.parseInt(hex, 16));
        } catch (NumberFormatException e) {
            return DataResult.error(() -> "Color must be #RRGGBB: " + value);
        }
    }

    private static DataResult<MagicTreasuresRarity> parseRarity(String value) {
        return Arrays.stream(MagicTreasuresRarity.values())
                .filter(rarity -> rarity != MagicTreasuresRarity.NONE && rarity.getValue().equalsIgnoreCase(value))
                .findFirst()
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Unknown rarity: " + value));
    }
}
