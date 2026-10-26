package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;

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

    private DataCodecs() {}

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
