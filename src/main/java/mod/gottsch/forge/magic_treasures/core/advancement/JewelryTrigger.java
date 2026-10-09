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

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.advancements.critereon.SerializationContext;

/**
 * {@code magictreasures:jewelry}: fires when a player does something with jewelry that vanilla criteria can't
 * see (anvil actions, gem extraction, wearing a full set). Conditions: {@code {"action": "<action>"}}, one of
 * the constants in {@link MagicTreasuresCriteria}.
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
        return new TriggerInstance(player, GsonHelper.getAsString(json, "action"));
    }

    public void trigger(ServerPlayer player, String action) {
        this.trigger(player, instance -> instance.action.equals(action));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final String action;

        public TriggerInstance(ContextAwarePredicate player, String action) {
            super(ID, player);
            this.action = action;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("action", action);
            return json;
        }
    }
}
