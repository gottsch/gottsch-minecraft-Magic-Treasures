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
package mod.gottsch.forge.magic_treasures.core.advancement;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.jewelry.data.DataCodecs;
import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import java.util.Optional;

/**
 * {@code magictreasures:jewelry}: fires when a player does something with jewelry that vanilla criteria can't
 * see (anvil actions, gem extraction, wearing a full set). Conditions: {@code {"action": "<action>"}}, one of
 * the constants in {@link MagicTreasuresCriteria}, plus the optional {@code min_level}, {@code set}, {@code style}
 * and {@code rarity}, matched against what the action reports ({@link Details}).
 *
 * @author Mark Gottschling on 10/9/2026
 */
public class JewelryTrigger extends SimpleCriterionTrigger<JewelryTrigger.TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation(MagicTreasures.MOD_ID, "jewelry");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        return new TriggerInstance(player,
                GsonHelper.getAsString(json, "action"),
                json.has("min_level") ? Optional.of(GsonHelper.getAsInt(json, "min_level")) : Optional.empty(),
                optionalLocation(json, "set"),
                optionalLocation(json, "style"),
                json.has("rarity")
                        ? DataCodecs.RARITY.parse(JsonOps.INSTANCE, json.get("rarity")).result()
                        : Optional.empty());
    }

    private static Optional<ResourceLocation> optionalLocation(JsonObject json, String key) {
        return json.has(key) ? Optional.of(new ResourceLocation(GsonHelper.getAsString(json, key))) : Optional.empty();
    }

    public void trigger(ServerPlayer player, String action) {
        trigger(player, action, Details.NONE);
    }

    public void trigger(ServerPlayer player, String action, Details details) {
        this.trigger(player, instance -> instance.matches(action, details));
    }

    /** what an action reports about itself, for the optional conditions; empty when it doesn't apply */
    public record Details(int level, Optional<ResourceLocation> set, Optional<ResourceLocation> style,
                          Optional<MagicTreasuresRarity> rarity) {
        public static final Details NONE = new Details(0, Optional.empty(), Optional.empty(), Optional.empty());

        public static Details level(int level) {
            return new Details(level, Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static Details set(ResourceLocation set) {
            return new Details(0, Optional.of(set), Optional.empty(), Optional.empty());
        }

        public static Details piece(ResourceLocation style, MagicTreasuresRarity rarity) {
            return new Details(0, Optional.empty(), Optional.of(style), Optional.of(rarity));
        }
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final String action;
        private final Optional<Integer> minLevel;
        private final Optional<ResourceLocation> set;
        private final Optional<ResourceLocation> style;
        private final Optional<MagicTreasuresRarity> rarity;

        public TriggerInstance(ContextAwarePredicate player, String action, Optional<Integer> minLevel,
                               Optional<ResourceLocation> set, Optional<ResourceLocation> style,
                               Optional<MagicTreasuresRarity> rarity) {
            super(ID, player);
            this.action = action;
            this.minLevel = minLevel;
            this.set = set;
            this.style = style;
            this.rarity = rarity;
        }

        public boolean matches(String action, Details details) {
            return this.action.equals(action)
                    && minLevel.map(min -> details.level() >= min).orElse(true)
                    && (set.isEmpty() || set.equals(details.set()))
                    && (style.isEmpty() || style.equals(details.style()))
                    && (rarity.isEmpty() || rarity.equals(details.rarity()));
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("action", action);
            minLevel.ifPresent(level -> json.addProperty("min_level", level));
            set.ifPresent(id -> json.addProperty("set", id.toString()));
            style.ifPresent(id -> json.addProperty("style", id.toString()));
            rarity.ifPresent(r -> json.addProperty("rarity", r.getValue()));
            return json;
        }
    }
}
