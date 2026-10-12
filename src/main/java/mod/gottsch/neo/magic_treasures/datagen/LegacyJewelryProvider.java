package mod.gottsch.neo.magic_treasures.datagen;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.LegacyJewelry;
import mod.gottsch.neo.magic_treasures.core.spell.MagicTreasuresSpells;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * M0: writes {@code data/magictreasures/legacy/jewelry.json}, every 1.x jewelry item id → its 2.0
 * {@code jewelry_identity} ({@link LegacyJewelry}). M9's conversion reads it; it is not a registry or a datapack
 * format, just a table shipped in the jar.
 */
public class LegacyJewelryProvider implements DataProvider {
    private final PackOutput output;

    public LegacyJewelryProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        // the builders look spells up by name; they register in MagicTreasuresSpells' static initializer (journal 8.1)
        MagicTreasuresSpells.DEFAULT_HEALING.getName();
        JsonObject json = new JsonObject();
        LegacyJewelry.identities().forEach((id, identity) ->
                json.add(id.toString(), JewelryIdentity.CODEC.encodeStart(JsonOps.INSTANCE, identity).getOrThrow()));
        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(MagicTreasures.MOD_ID).resolve("legacy/jewelry.json");
        return DataProvider.saveStable(cache, json, path);
    }

    @Override
    public String getName() {
        return "Magic Treasures legacy jewelry table";
    }
}
