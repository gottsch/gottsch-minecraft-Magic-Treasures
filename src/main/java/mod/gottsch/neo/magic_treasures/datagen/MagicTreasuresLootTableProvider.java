package mod.gottsch.neo.magic_treasures.datagen;

import mod.gottsch.neo.magic_treasures.datagen.loot.MagicTreasuresBlockLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MagicTreasuresLootTableProvider {
    public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(MagicTreasuresBlockLootTables::new, LootContextParamSets.BLOCK)
        ), registries);
    }
}

