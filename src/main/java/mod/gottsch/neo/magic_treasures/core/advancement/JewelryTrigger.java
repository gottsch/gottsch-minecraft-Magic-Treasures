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
package mod.gottsch.neo.magic_treasures.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.DataCodecs;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * {@code magictreasures:jewelry}: fires when a player does something with jewelry that vanilla criteria can't
 * see (anvil actions, gem extraction, wearing jewelry, carrying a piece). Conditions: {@code {"action": "<action>"}},
 * one of the constants in {@link MagicTreasuresCriteria}, plus optional narrowing fields, each checked only when the
 * action reports it:
 * <ul>
 *     <li>{@code min_level}: the spell level is at least this ({@code imbue});</li>
 *     <li>{@code set}: the worn set's id ({@code full_set});</li>
 *     <li>{@code style}, {@code rarity}: the piece's style id and rarity ({@code carry}).</li>
 * </ul>
 *
 * @author Mark Gottschling on 10/9/2026
 */
public class JewelryTrigger extends SimpleCriterionTrigger<JewelryTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
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

    public record TriggerInstance(Optional<ContextAwarePredicate> player, String action, Optional<Integer> minLevel,
                                  Optional<ResourceLocation> set, Optional<ResourceLocation> style,
                                  Optional<MagicTreasuresRarity> rarity) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.STRING.fieldOf("action").forGetter(TriggerInstance::action),
                Codec.INT.optionalFieldOf("min_level").forGetter(TriggerInstance::minLevel),
                ResourceLocation.CODEC.optionalFieldOf("set").forGetter(TriggerInstance::set),
                ResourceLocation.CODEC.optionalFieldOf("style").forGetter(TriggerInstance::style),
                DataCodecs.RARITY.optionalFieldOf("rarity").forGetter(TriggerInstance::rarity)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(String action, Details details) {
            return this.action.equals(action)
                    && minLevel.map(min -> details.level() >= min).orElse(true)
                    && (set.isEmpty() || set.equals(details.set()))
                    && (style.isEmpty() || style.equals(details.style()))
                    && (rarity.isEmpty() || rarity.equals(details.rarity()));
        }
    }
}
