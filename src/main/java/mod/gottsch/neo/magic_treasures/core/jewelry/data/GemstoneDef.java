package mod.gottsch.neo.magic_treasures.core.jewelry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresRegistries;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * A gemstone ({@code magictreasures:gemstone}): an item that can be set in jewelry.
 *
 * @param item      the gem item. Kept as an id so that a missing item skips this gem (with a warning) instead of
 *                  failing the whole datapack load
 * @param tier      a {@code stone_tier} id, or an inline tier object
 * @param rarity    used for gem loot and the jewelry's rarity, not for stats
 * @param color     RGB tint for the shared gem overlay, when no gem texture exists
 * @param affixesTo a material id, a list of ids or a {@code #tag}; absent = every material that accepts gems
 * @param disabled  a pack can turn a built-in entry off by overriding its file with {@code "disabled": true}
 */
public record GemstoneDef(
        ResourceLocation item,
        Holder<StoneTierDef> tier,
        Optional<MagicTreasuresRarity> rarity,
        int color,
        Optional<HolderSet<JewelryMaterialDef>> affixesTo,
        boolean disabled) {

    public static final int DEFAULT_COLOR = 0xFFFFFF;

    public static final Codec<GemstoneDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("item").forGetter(GemstoneDef::item),
            // RegistryFileCodec reads a string as a reference and an object as an inline (direct) holder
            RegistryFileCodec.create(MagicTreasuresRegistries.STONE_TIER, StoneTierDef.CODEC).fieldOf("tier").forGetter(GemstoneDef::tier),
            DataCodecs.RARITY.optionalFieldOf("rarity").forGetter(GemstoneDef::rarity),
            DataCodecs.COLOR.optionalFieldOf("color", DEFAULT_COLOR).forGetter(GemstoneDef::color),
            RegistryCodecs.homogeneousList(MagicTreasuresRegistries.JEWELRY_MATERIAL).optionalFieldOf("affixes_to").forGetter(GemstoneDef::affixesTo),
            Codec.BOOL.optionalFieldOf("disabled", false).forGetter(GemstoneDef::disabled)
    ).apply(instance, GemstoneDef::new));

    /** Whether this gem may be set in the given material. The material's own {@code accepts_gems} is checked separately. */
    public boolean affixesTo(Holder<JewelryMaterialDef> material) {
        return affixesTo.map(set -> set.contains(material)).orElse(true);
    }
}
