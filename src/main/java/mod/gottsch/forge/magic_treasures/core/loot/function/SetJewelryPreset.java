/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * Magic Treasures is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Magic Treasures is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Magic Treasures.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.magic_treasures.core.loot.function;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import mod.gottsch.forge.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.forge.magic_treasures.core.item.GenericJewelry;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.GemstoneDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryMaterialDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryPresetDef;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.JewelryRules;
import mod.gottsch.forge.magic_treasures.core.loot.MagicTreasuresLootFunctions;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

/**
 * Replaces the stack with a 2.0 preset: {@code {"function": "magictreasures:set_jewelry_preset", "preset":
 * "magictreasures:angels_ring"}}. An optional {@code gem} replaces the preset's own (the castle ring's ruby), if the
 * preset and material accept it. An unknown or disabled preset gives an empty stack.
 *
 * @author Mark Gottschling on 10/12/2026
 */
public class SetJewelryPreset extends LootItemConditionalFunction {
    private final ResourceLocation preset;
    private final Optional<ResourceLocation> gem;

    public SetJewelryPreset(LootItemCondition[] conditions, ResourceLocation preset, Optional<ResourceLocation> gem) {
        super(conditions);
        this.preset = preset;
        this.gem = gem;
    }

    @Override
    public LootItemFunctionType getType() {
        return MagicTreasuresLootFunctions.SET_JEWELRY_PRESET.get();
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        Optional<Holder.Reference<JewelryPresetDef>> holder = JewelryDataIndex.preset(preset);
        if (holder.isEmpty()) {
            MagicTreasures.LOGGER.warn("set_jewelry_preset: unknown or disabled preset {}", preset);
            return ItemStack.EMPTY;
        }
        JewelryPresetDef def = holder.get().value();
        Optional<ResourceLocation> stone = def.gem();
        if (gem.isPresent()) {
            Optional<Holder.Reference<JewelryMaterialDef>> material = JewelryDataIndex.material(def.material());
            Optional<Holder.Reference<GemstoneDef>> gemstone = JewelryDataIndex.gemstone(gem.get());
            if (material.isPresent() && gemstone.isPresent()
                    && JewelryRules.allowsGem(material.get(), gemstone.get(), holder)) {
                stone = gem;
            } else {
                MagicTreasures.LOGGER.warn("set_jewelry_preset: {} doesn't accept gem {}; using its own", preset, gem.get());
            }
        }
        JewelryIdentity identity = new JewelryIdentity(def.type(), def.material(), stone, def.style(), Optional.of(preset));
        return GenericJewelry.forType(def.type()).map(item -> item.stack(identity)).orElse(ItemStack.EMPTY);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetJewelryPreset> {
        @Override
        public void serialize(JsonObject json, SetJewelryPreset value, JsonSerializationContext context) {
            super.serialize(json, value, context);
            json.addProperty("preset", value.preset.toString());
            value.gem.ifPresent(gem -> json.addProperty("gem", gem.toString()));
        }

        @Override
        public SetJewelryPreset deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new SetJewelryPreset(conditions, new ResourceLocation(GsonHelper.getAsString(json, "preset")),
                    json.has("gem") ? Optional.of(new ResourceLocation(GsonHelper.getAsString(json, "gem"))) : Optional.empty());
        }
    }
}
