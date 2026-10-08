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
package mod.gottsch.neo.magic_treasures.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The persisted state of a piece of jewelry (the data component).
 * Immutable: change it with the with* methods and stack.set(...), which is what JewelryHandler does.
 * Being a record gives it value equality, which vanilla and Curios rely on to detect a changed stack
 * and sync it to the client.
 * Replaces the 1.20.1 JewelryCapability NBT.
 *
 * @param type     IJewelryType name (EnumRegistry key)
 * @param material JewelryMaterial id
 * @param sizeTier IJewelrySizeTier name (EnumRegistry key)
 * @param stone    stone item id, if any
 * @param baseName base name used for naming rules, if any
 */
public record JewelryData(
        String type,
        ResourceLocation material,
        String sizeTier,
        Optional<ResourceLocation> stone,
        Optional<String> baseName,
        Stats stats,
        Factors factors,
        List<SpellData> spells) {

    public static final Codec<JewelryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(JewelryData::type),
            ResourceLocation.CODEC.fieldOf("material").forGetter(JewelryData::material),
            Codec.STRING.fieldOf("size_tier").forGetter(JewelryData::sizeTier),
            ResourceLocation.CODEC.optionalFieldOf("stone").forGetter(JewelryData::stone),
            Codec.STRING.optionalFieldOf("base_name").forGetter(JewelryData::baseName),
            Stats.CODEC.fieldOf("stats").forGetter(JewelryData::stats),
            Factors.CODEC.fieldOf("factors").forGetter(JewelryData::factors),
            SpellData.CODEC.listOf().optionalFieldOf("spells", List.of()).forGetter(JewelryData::spells)
    ).apply(instance, JewelryData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, JewelryData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public JewelryData {
        spells = List.copyOf(spells);
    }

    public JewelryData withStone(Optional<ResourceLocation> stone) {
        return new JewelryData(type, material, sizeTier, stone, baseName, stats, factors, spells);
    }

    public JewelryData withBaseName(Optional<String> baseName) {
        return new JewelryData(type, material, sizeTier, stone, baseName, stats, factors, spells);
    }

    public JewelryData withStats(Stats stats) {
        return new JewelryData(type, material, sizeTier, stone, baseName, stats, factors, spells);
    }

    public JewelryData withFactors(Factors factors) {
        return new JewelryData(type, material, sizeTier, stone, baseName, stats, factors, spells);
    }

    public JewelryData withSpells(List<SpellData> spells) {
        return new JewelryData(type, material, sizeTier, stone, baseName, stats, factors, spells);
    }

    public JewelryData withSpell(int index, SpellData spell) {
        List<SpellData> list = new ArrayList<>(spells);
        list.set(index, spell);
        return withSpells(list);
    }

    public JewelryData addSpell(SpellData spell) {
        List<SpellData> list = new ArrayList<>(spells);
        list.add(spell);
        return withSpells(list);
    }

    /**
     * Uses, level, mana, repairs and recharges.
     */
    public record Stats(int maxUses, int uses, int maxLevel, double maxMana, double mana,
                        int maxRepairs, int repairs, int maxRecharges, int recharges) {

        public static final Codec<Stats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("max_uses").forGetter(Stats::maxUses),
                Codec.INT.fieldOf("uses").forGetter(Stats::uses),
                Codec.INT.fieldOf("max_level").forGetter(Stats::maxLevel),
                Codec.DOUBLE.fieldOf("max_mana").forGetter(Stats::maxMana),
                Codec.DOUBLE.fieldOf("mana").forGetter(Stats::mana),
                Codec.INT.fieldOf("max_repairs").forGetter(Stats::maxRepairs),
                Codec.INT.fieldOf("repairs").forGetter(Stats::repairs),
                Codec.INT.fieldOf("max_recharges").forGetter(Stats::maxRecharges),
                Codec.INT.fieldOf("recharges").forGetter(Stats::recharges)
        ).apply(instance, Stats::new));

        public Stats withMaxUses(int v) { return new Stats(v, uses, maxLevel, maxMana, mana, maxRepairs, repairs, maxRecharges, recharges); }
        public Stats withUses(int v) { return new Stats(maxUses, v, maxLevel, maxMana, mana, maxRepairs, repairs, maxRecharges, recharges); }
        public Stats withMaxLevel(int v) { return new Stats(maxUses, uses, v, maxMana, mana, maxRepairs, repairs, maxRecharges, recharges); }
        public Stats withMaxMana(double v) { return new Stats(maxUses, uses, maxLevel, v, mana, maxRepairs, repairs, maxRecharges, recharges); }
        public Stats withMana(double v) { return new Stats(maxUses, uses, maxLevel, maxMana, v, maxRepairs, repairs, maxRecharges, recharges); }
        public Stats withMaxRepairs(int v) { return new Stats(maxUses, uses, maxLevel, maxMana, mana, v, repairs, maxRecharges, recharges); }
        public Stats withRepairs(int v) { return new Stats(maxUses, uses, maxLevel, maxMana, mana, maxRepairs, v, maxRecharges, recharges); }
        public Stats withMaxRecharges(int v) { return new Stats(maxUses, uses, maxLevel, maxMana, mana, maxRepairs, repairs, v, recharges); }
        public Stats withRecharges(int v) { return new Stats(maxUses, uses, maxLevel, maxMana, mana, maxRepairs, repairs, maxRecharges, v); }
    }

    /**
     * Spell modifier factors (1.0 = no change).
     */
    public record Factors(double spellCost, double spellEffectAmount, double spellFrequency,
                          double spellDuration, double spellCooldown, double spellRange) {

        public static final Codec<Factors> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("spell_cost").forGetter(Factors::spellCost),
                Codec.DOUBLE.fieldOf("spell_effect_amount").forGetter(Factors::spellEffectAmount),
                Codec.DOUBLE.fieldOf("spell_frequency").forGetter(Factors::spellFrequency),
                Codec.DOUBLE.fieldOf("spell_duration").forGetter(Factors::spellDuration),
                Codec.DOUBLE.fieldOf("spell_cooldown").forGetter(Factors::spellCooldown),
                Codec.DOUBLE.fieldOf("spell_range").forGetter(Factors::spellRange)
        ).apply(instance, Factors::new));

        public Factors withSpellCost(double v) { return new Factors(v, spellEffectAmount, spellFrequency, spellDuration, spellCooldown, spellRange); }
        public Factors withSpellEffectAmount(double v) { return new Factors(spellCost, v, spellFrequency, spellDuration, spellCooldown, spellRange); }
        public Factors withSpellFrequency(double v) { return new Factors(spellCost, spellEffectAmount, v, spellDuration, spellCooldown, spellRange); }
        public Factors withSpellDuration(double v) { return new Factors(spellCost, spellEffectAmount, spellFrequency, v, spellCooldown, spellRange); }
        public Factors withSpellCooldown(double v) { return new Factors(spellCost, spellEffectAmount, spellFrequency, spellDuration, v, spellRange); }
        public Factors withSpellRange(double v) { return new Factors(spellCost, spellEffectAmount, spellFrequency, spellDuration, spellCooldown, v); }
    }
}
