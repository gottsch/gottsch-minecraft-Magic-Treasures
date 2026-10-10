/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.magic_treasures.datagen;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.block.MagicTreasuresBlocks;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.item.SpellScroll;
import mod.gottsch.neo.magic_treasures.core.setup.Registration;
import mod.gottsch.neo.magic_treasures.core.util.LangUtil;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.commons.lang3.text.WordUtils;

/**
 * 
 * @author Mark Gottschling Jun 2, 2023
 *
 */
@SuppressWarnings("deprecation")
public class LanguageGen extends LanguageProvider {

    public LanguageGen(PackOutput output, String locale) {
        super(output, MagicTreasures.MOD_ID, locale);
    }
    
    @Override
    protected void addTranslations() {
    	// tabs
        add("itemGroup." + MagicTreasures.MOD_ID, "Magic Treasures");
        add("itemGroup." + MagicTreasures.MOD_ID + ".jewelry_tab", "Magic Treasures Jewelry");

        // metals
        add(MagicTreasuresItems.SILVER_INGOT.get(), "Silver Ingot");
        add(MagicTreasuresItems.RAW_SILVER.get(), "Raw Silver");
        // stones
        add(MagicTreasuresItems.TOPAZ.get(), "Topaz");
        add(MagicTreasuresItems.ONYX.get(), "Onyx");
        add(MagicTreasuresItems.JADEITE.get(), "Jadeite");
        add(MagicTreasuresItems.RUBY.get(), "Ruby");
        add(MagicTreasuresItems.SAPPHIRE.get(), "Sapphire");
        add(MagicTreasuresItems.WHITE_PEARL.get(), "White Pearl");
        add(MagicTreasuresItems.BLACK_PEARL.get(), "Black Pearl");
//        add(MagicTreasuresItems.SKELETONS_HEART.get(), "Skeleton's Heart");

        // regular jewelry
		MagicTreasuresItems.STANDARD_JEWELRY.forEach(item -> {
			add(item.get(), WordUtils.capitalizeFully(item.getId().getPath().replace("_", " ")));
		});

        // special jewelry
        add(MagicTreasuresItems.SILBROS_RING_OF_VITALITY.get(), WordUtils.capitalizeFully("Silbro's Ring of Vitality"));
        add(MagicTreasuresItems.STRONGMANS_BRACERS.get(), WordUtils.capitalizeFully("Strongman's Bracers"));
        add(MagicTreasuresItems.SILBROS_ACORN.get(), "Silbro's Acorn");
        add(MagicTreasuresItems.ROOTSTAFF.get(), "Rootstaff");
        add(MagicTreasuresItems.BARKSKIN_VEST.get(), "Barkskin Vest");
        add(MagicTreasuresItems.MALDRITCHS_FIRST_AMULET.get(), WordUtils.capitalizeFully("Maldritch's First Amulet"));

        add(MagicTreasuresItems.PEASANTS_FORTUNE.get(), WordUtils.capitalizeFully("Peasant's Fortune"));
        add(MagicTreasuresItems.AQUA_RING.get(), WordUtils.capitalizeFully("Aqua Ring"));
        add(MagicTreasuresItems.AMULET_OF_DEFENCE.get(), WordUtils.capitalizeFully("Amulet of Defence"));
        add(MagicTreasuresItems.JOURNEYMANS_BANDS.get(), WordUtils.capitalizeFully("Journeyman's Bands"));

        add(MagicTreasuresItems.MEDICS_TOKEN.get(), WordUtils.capitalizeFully("Medic's Token"));
        add(MagicTreasuresItems.ADEPHAGIAS_BOUNTY.get(), WordUtils.capitalizeFully("Adephagia's Bounty"));
        add(MagicTreasuresItems.ANGELS_RING.get(), WordUtils.capitalizeFully("Ring of Angels"));
        add(MagicTreasuresItems.SALANDAARS_WARD.get(), WordUtils.capitalizeFully("Sal'andaar's Ward"));
        add(MagicTreasuresItems.RING_OF_FORTITUDE.get(), WordUtils.capitalizeFully("Ring of Fortitude"));
        add(MagicTreasuresItems.EYE_OF_THE_PHOENIX.get(), WordUtils.capitalizeFully("Eye of the Phoenix"));
        add(MagicTreasuresItems.RING_LIFE_DEATH.get(), WordUtils.capitalizeFully("Ring of Life and Death"));

         // GOTTSCHS_AMULET_OF_HEAVENS
        // GOTTSCHS_RING_OF_MOON
        // SHADOWS_GIFT

        // belts
        add(MagicTreasuresItems.SKULL_BELT.get(), WordUtils.capitalizeFully("Skull Belt"));

        // scrolls
        Registration.ITEMS.getEntries().forEach(o -> {
            if (o.get() instanceof SpellScroll) {
                add(o.get(), WordUtils.capitalizeFully("Scroll of " + ((SpellScroll) o.get()).getSpell().getName().getPath().replace("_", " ")));
            }
        });

        // recipes scrolls
        add(MagicTreasuresItems.RING_RECIPE.get(), "Ring Recipe");
        add(MagicTreasuresItems.NECKLACE_RECIPE.get(), "Necklace Recipe");
        add(MagicTreasuresItems.BRACELET_RECIPE.get(), "Bracelet Recipe");

        // recharge scroll
        add(MagicTreasuresItems.RECHARGE_SCROLL.get(), "Recharge Scroll");

        // ore
        add(MagicTreasuresBlocks.TOPAZ_ORE.get(), "Topaz Ore");
        add(MagicTreasuresBlocks.ONYX_ORE.get(), "Onyx Ore");
        add(MagicTreasuresBlocks.JADEITE_ORE.get(), "Jadeite Ore");
        add(MagicTreasuresBlocks.RUBY_ORE.get(), "Ruby Ore");
        add(MagicTreasuresBlocks.SAPPHIRE_ORE.get(), "Sapphire Ore");
        add(MagicTreasuresBlocks.SILVER_ORE.get(), "Silver Ore");

        add(MagicTreasuresBlocks.DEEPSLATE_TOPAZ_ORE.get(), "Deepslate Topaz Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_ONYX_ORE.get(), "Deepslate Onyx Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_JADEITE_ORE.get(), "Deepslate Jadeite Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_RUBY_ORE.get(), "Deepslate Ruby Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_SAPPHIRE_ORE.get(), "Deepslate Sapphire Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_SILVER_ORE.get(), "Deepslate Silver Ore");

        
        /*
         *  Util.tooltips
         */
        // general
        add(LangUtil.tooltip("boolean.yes"), "Yes");
        add(LangUtil.tooltip("boolean.no"), "No");
        add(LangUtil.tooltip("infinite"), "Infinite");
        add(LangUtil.tooltip("hold_shift"), "Hold [SHIFT] to expand");
        add(LangUtil.tooltip("divider"), "--------------------");

        // tools
        add(MagicTreasuresItems.JEWELRY_PLIERS.get(), "Jewelry Pliers");
        add(LangUtil.tooltip("mana_well.usage"), "Wear or hold it. Spells draw from it when their jewelry runs out of mana.");

        // advancements
        add("advancements.magictreasures.root.title", "Magic Treasures");
        add("advancements.magictreasures.root.description", "Find a piece of magic jewelry");
        add("advancements.magictreasures.set_in_stone.title", "Set in Stone");
        add("advancements.magictreasures.set_in_stone.description", "Set a gem into jewelry at an anvil");
        add("advancements.magictreasures.spellbound.title", "Spellbound");
        add("advancements.magictreasures.spellbound.description", "Imbue jewelry with a spell scroll at an anvil");
        add("advancements.magictreasures.second_wind.title", "Second Wind");
        add("advancements.magictreasures.second_wind.description", "Recharge jewelry at an anvil");
        add("advancements.magictreasures.delicate_work.title", "Delicate Work");
        add("advancements.magictreasures.delicate_work.description", "Pull a gem out of jewelry with Jewelry Pliers");
        add("advancements.magictreasures.fully_adorned.title", "Fully Adorned");
        add("advancements.magictreasures.fully_adorned.description", "Wear a magic ring, necklace and bracelet at once");
        add(LangUtil.tooltip("tools.jewelry_pliers"), "Craft with jewelry to remove its gem. The jewelry is destroyed.");

        // gemstones
        add(LangUtil.tooltip("gemstone.usage"), "Place on an anvil with Magic Treasures jewelry to combine.");
        add(LangUtil.tooltip("gemstone.recharge.usage"), "Place on an anvil with Magic Treasures jewelry to recharge.");
        add(LangUtil.tooltip("gemstone.rarity"), "Rarity: %s");
        add(LangUtil.tooltip("gemstone.tier"), "Tier: %s");
        add(LangUtil.tooltip("gemstone.mana"), "Mana: %s");
        add(LangUtil.tooltip("gemstone.recharges"), "Recharges: %s");
        add(LangUtil.tooltip("gemstone.cost_factor"), "Mana Cost: %s");
        add(LangUtil.tooltip("gemstone.cooldown_factor"), "Cooldown Time: %s");
        add(LangUtil.tooltip("gemstone.effect_amount_factor"), "Effect Amount: %s");
        add(LangUtil.tooltip("gemstone.frequency_factor"), "Frequency: %s");
        add(LangUtil.tooltip("gemstone.range_factor"), "Range: %s");

        // gemstone tier names
//        add(LangUtil.tooltip("gemstone.tier.tier1.name"), "Tier 1");
//        add(LangUtil.tooltip("gemstone.tier.skeletons_heart.name"), "Skeleton's Heart");

        // jewelry
        add(LangUtil.tooltip("jewelry.usage"), "Hold in hand or equip for buffs.");
        add(LangUtil.tooltip("jewelry.material"), "Material: %s");
        add(LangUtil.tooltip("jewelry.stone"), "Stone: %s");

//        add(LangUtil.tooltip("jewelry.durability"), "Durability: ");
//        add(LangUtil.tooltip("jewelry.durability.gauge"), "[%s/%s]");
        add(LangUtil.tooltip("jewelry.durability.amount"), "Durability: [%s/%s]");
        add(LangUtil.tooltip("jewelry.durability.infinite"), "Durability: -%s-");
        add(LangUtil.tooltip("jewelry.durability.repairs"), "Repairs: %s");

        add(LangUtil.tooltip("jewelry.max_level"), "Spell Max. Level: %s");
        add(LangUtil.tooltip("jewelry.mana"), "Mana: [%s/%s]");
        add(LangUtil.tooltip("jewelry.mana.recharges"), "Recharges: %s");
        add(LangUtil.tooltip("jewelry.mana.gauge"), "[%s/%s]");
        add(LangUtil.tooltip("jewelry.spells"), "Spells:");

        // 1.5.0 tooltip redesign
        add(LangUtil.tooltip("jewelry.bar.mana"), "Mana");
        add(LangUtil.tooltip("jewelry.bar.durability"), "Durability");
        add(LangUtil.tooltip("jewelry.level"), "Lvl %s");
        add(LangUtil.tooltip("jewelry.type.ring"), "Ring");
        add(LangUtil.tooltip("jewelry.type.necklace"), "Necklace");
        add(LangUtil.tooltip("jewelry.type.bracelet"), "Bracelet");
        add(LangUtil.tooltip("jewelry.type.pocket"), "Pocket");
        add(LangUtil.tooltip("jewelry.type.earring"), "Earring");
        add(LangUtil.tooltip("jewelry.type.unknown"), "Jewelry");
        add(LangUtil.tooltip("jewelry.modifier.cost"), "Cost %s");
        add(LangUtil.tooltip("jewelry.modifier.cooldown"), "Cooldown %s");
        add(LangUtil.tooltip("jewelry.modifier.effect"), "Effect %s");
        add(LangUtil.tooltip("jewelry.modifier.frequency"), "Frequency %s");
        add(LangUtil.tooltip("jewelry.modifier.range"), "Range %s");
        add(LangUtil.tooltip("spell.stat.absorb"), "%s absorb");
        add(LangUtil.tooltip("spell.stat.armor"), "%s armor");
        add(LangUtil.tooltip("spell.stat.cooldown"), "%s cooldown");
        add(LangUtil.tooltip("spell.stat.damage"), "%s dmg");
        add(LangUtil.tooltip("spell.stat.drain"), "%s drain");
        add(LangUtil.tooltip("spell.stat.duration"), "%s duration");
        add(LangUtil.tooltip("spell.stat.every"), "every %s");
        add(LangUtil.tooltip("spell.stat.heal"), "%s heal");
        add(LangUtil.tooltip("spell.stat.hunger"), "%s hunger");
        add(LangUtil.tooltip("spell.stat.life"), "%s life");
        add(LangUtil.tooltip("spell.stat.mana"), "%s mana");
        add(LangUtil.tooltip("spell.stat.range"), "%s range");
        add(LangUtil.tooltip("spell.stat.reflect"), "%s reflect");
        add(LangUtil.tooltip("spell.stat.resist"), "%s resist");
        add(LangUtil.tooltip("spell.stat.stacks"), "stacks");
        add(LangUtil.tooltip("spell.stat.seconds"), "%ss");
        // sets
        add("tooltip.magictreasures.set.header", "%s (%s/%s)");
        add("tooltip.magictreasures.set.bonus", "(%s) %s");
        add(LangUtil.tooltip("set_piece.siphon"), "Siphon: full-strength hits restore %s mana to your worn jewelry");
        add(LangUtil.tooltip("set_piece.resonance"), "Resonance: +%s%% effect to this set's jewelry spells");
        add("set.magictreasures.salandaars_anvilwork", "Sal'andaar's Anvilwork");
        add("set.magictreasures.salandaars_anvilwork.bonus.2", "Ward spells cost 10%% less mana");
        add("set.magictreasures.silbros_grove", "Silbro's Grove");
        add("set.magictreasures.silbros_grove.bonus.2", "Healing spells heal 15%% more, 25%% more often");
        add("set.magictreasures.silbros_grove.bonus.4", "Standing on grass, moss or leaves slowly heals you");
        add("set.magictreasures.silbros_grove.bonus.5", "Take Root: in sunlight, worn set pieces slowly mend");
        add("set.magictreasures.maldritchs_remains", "Maldritch's Remains");
        add("set.magictreasures.maldritchs_remains.bonus.2", "Drain spells reach 1 block further");
        add("set.magictreasures.selenes_tides", "Selene's Tides");
        add("set.magictreasures.selenes_tides.bonus.2", "Water Breathing and Night Vision last 50%% longer");
        add(LangUtil.tooltip("spell.flavor.blessing_of_the_phoenix"), "Rise unburnt from the flames, as the phoenix does.");
        add(LangUtil.tooltip("spell.flavor.cat_sight"), "See in the dark as clearly as a cat.");
        add(LangUtil.tooltip("spell.flavor.cheat_death"), "Turns aside one killing blow.");
        add(LangUtil.tooltip("spell.flavor.crushing_response"), "Every blow you take is repaid in kind.");
        add(LangUtil.tooltip("spell.flavor.desiccate"), "Withers the life from all who stand near.");
        add(LangUtil.tooltip("spell.flavor.disembodied_armor"), "An armor of spirit that turns aside claws and blades.");
        add(LangUtil.tooltip("spell.flavor.drain"), "Sips the life from nearby foes.");
        add(LangUtil.tooltip("spell.flavor.fire_resistance"), "A cool ward against flame and ember.");
        add(LangUtil.tooltip("spell.flavor.fire_ward"), "A stronger ward against flame and ember.");
        add(LangUtil.tooltip("spell.flavor.ghostly_armor"), "A faint, ghostly shell against mob attacks.");
        add(LangUtil.tooltip("spell.flavor.giant_strength"), "The strength of a giant, for a time.");
        add(LangUtil.tooltip("spell.flavor.greater_drain"), "Draws deeply from the life of nearby foes.");
        add(LangUtil.tooltip("spell.flavor.greater_healing"), "A deep, steady mending of wounds.");
        add(LangUtil.tooltip("spell.flavor.greater_invisibility"), "Vanish from sight for longer.");
        add(LangUtil.tooltip("spell.flavor.greater_night_vision"), "Night is no darker than day.");
        add(LangUtil.tooltip("spell.flavor.greater_speed"), "Your feet barely touch the ground.");
        add(LangUtil.tooltip("spell.flavor.greater_strength"), "Your blows land with greater force.");
        add(LangUtil.tooltip("spell.flavor.greater_water_breathing"), "Breathe beneath the waves for a long while.");
        add(LangUtil.tooltip("spell.flavor.harm"), "A bolt of force at the nearest mob.");
        add(LangUtil.tooltip("spell.flavor.healing"), "Mends your wounds over time.");
        add(LangUtil.tooltip("spell.flavor.horse_power"), "Run like a horse at full gallop.");
        add(LangUtil.tooltip("spell.flavor.invisibility"), "Fade from sight for a moment.");
        add(LangUtil.tooltip("spell.flavor.lesser_healing"), "Slowly knits small wounds closed.");
        add(LangUtil.tooltip("spell.flavor.magic_resistance"), "Dulls the bite of hostile magic.");
        add(LangUtil.tooltip("spell.flavor.magic_ward"), "A firm ward against hostile magic.");
        add(LangUtil.tooltip("spell.flavor.mana_buckler"), "Your mana takes part of the blow.");
        add(LangUtil.tooltip("spell.flavor.mana_pavise_shield"), "A great wall of mana between you and harm.");
        add(LangUtil.tooltip("spell.flavor.mana_shield"), "Your mana takes the blow before you do.");
        add(LangUtil.tooltip("spell.flavor.mana_tower_shield"), "A towering shield of mana that blocks most blows.");
        add(LangUtil.tooltip("spell.flavor.mind_fist"), "A crushing blow of pure will.");
        add(LangUtil.tooltip("spell.flavor.mind_jab"), "A quick jab of mental force.");
        add(LangUtil.tooltip("spell.flavor.night_vision"), "Lets you see in the dark.");
        add(LangUtil.tooltip("spell.flavor.paladin_smite"), "Your life fuels a holy smite.");
        add(LangUtil.tooltip("spell.flavor.paladin_strike"), "Trade a little of your life for a harder strike.");
        add(LangUtil.tooltip("spell.flavor.quick_strength"), "A short burst of strength.");
        add(LangUtil.tooltip("spell.flavor.reflection"), "Turns part of each blow back on the attacker.");
        add(LangUtil.tooltip("spell.flavor.regeneration"), "Your wounds close almost as fast as they open.");
        add(LangUtil.tooltip("spell.flavor.salandaars_magic_coat"), "Sal'andaar's own coat against hostile magic.");
        add(LangUtil.tooltip("spell.flavor.satiety"), "Keeps hunger at bay.");
        add(LangUtil.tooltip("spell.flavor.shadow_armor"), "Shadows gather to soften each blow.");
        add(LangUtil.tooltip("spell.flavor.spectral_armor"), "A spectral shell that softens mob attacks.");
        add(LangUtil.tooltip("spell.flavor.speed"), "Quickens your step.");
        add(LangUtil.tooltip("spell.flavor.strength"), "Lends your arm more force.");
        add(LangUtil.tooltip("spell.flavor.water_breathing"), "Breathe underwater for a while.");
        add(LangUtil.tooltip("spell.flavor.wither_resistance"), "Slows the creeping rot of wither.");
        add(LangUtil.tooltip("spell.flavor.wither_ward"), "A firm ward against wither.");
        add(LangUtil.tooltip("spell.flavor.withers_skin"), "Skin as tough as the Wither's own.");
        add(LangUtil.tooltip("jewelry.stats.cost_factor"), "C:%s");
        add(LangUtil.tooltip("jewelry.stats.cooldown_factor"), "Cd:%s");
        add(LangUtil.tooltip("jewelry.stats.effect_amount_factor"), "E:%s");
        add(LangUtil.tooltip("jewelry.stats.frequency_factor"), "Fq:%s");
        add(LangUtil.tooltip("jewelry.stats.range_factor"), "R:%s");

        /*
         * specific jewelry
         */
        // lore
        add(LangUtil.tooltip("jewelry.castle_ring.lore"), "Castle rings contain an abundance of mana and durability.~Can only be affixed with Rubies & Sapphires.");
        add(LangUtil.tooltip("jewelry.hawk_ring.lore"), "Hawk rings contain an abundance of mana and a higher max spell level.~Cannot be affixed with any gems.");
        add(LangUtil.tooltip("jewelry.silbros_ring_of_vitality.lore"), "Silbro grew this ring into shape.~Living wood holds mana as well as any metal.");
        add(LangUtil.tooltip("jewelry.strongmans_bracers.lore"), "Silbro grew these for a woodcutter who wore out every axe.~Living wood holds strength as well as mana.");
        add(LangUtil.tooltip("jewelry.silbros_acorn.lore"), "Silbro gave an acorn from his oldest oak to anyone who went hungry.~Planted, it feeds a village; worn, it feeds you.");
        add(LangUtil.tooltip("set_piece.rootstaff.lore"), "Silbro never cut a staff; he asked a root to grow straight.~Every blow it lands sends a little mana home.");
        add(LangUtil.tooltip("set_piece.barkskin_vest.lore"), "Bark from a living tree, which grew it back the next spring.~It hums when Silbro's other pieces are near.");
        add(LangUtil.tooltip("jewelry.maldritchs_first_amulet.lore"), "Maldritch is a powerful lich, but he wasn't always one.~This is his first amulet when he was a mere apprentice.");

        /*
         * spells
         */

        // spell scroll stats
        add(LangUtil.tooltip("spell.name"), "Name: %s");
        add(LangUtil.tooltip("spell.level"), "Level: %s");
        add(LangUtil.tooltip("spell.rarity"), "Rarity: %s");
        add(LangUtil.tooltip("spell.cost"), "Cost: %s");
        add(LangUtil.tooltip("spell.cost.varies"), "Cost: Varies");
        add(LangUtil.tooltip("spell.effect_amount"), "Effect Amount: %s");
        add(LangUtil.tooltip("spell.cooldown"), "Cooldown Time: %s seconds.");
        add(LangUtil.tooltip("spell.frequency"), "Frequency: Every %s seconds.");
        add(LangUtil.tooltip("spell.range"), "Range: %s blocks.");

        ///// healing /////
        add(LangUtil.tooltip("spell.name.lesser_healing"), "Lesser Healing");
        add(LangUtil.tooltip("spell.name.healing"), "Healing");
        add(LangUtil.tooltip("spell.name.greater_healing"), "Greater Healing");
        add(LangUtil.tooltip("spell.name.regeneration"), "Regeneration");

        add(LangUtil.tooltip("spell.healing.rate"), "Heals %s hp every %s seconds");

        ///// mana shield /////
        add(LangUtil.tooltip("spell.name.mana_buckler"), "Mana Buckler");
        add(LangUtil.tooltip("spell.name.mana_shield"), "Mana Shield");
        add(LangUtil.tooltip("spell.name.mana_tower_shield"), "Mana Tower Shield");
        add(LangUtil.tooltip("spell.name.mana_pavise_shield"), "Mana Pavise Shield");

        add(LangUtil.tooltip("spell.mana_shield.rate"), "Absorbs %s damage. Cooldown: %s seconds.");

        ///// spectral armor /////
        add(LangUtil.tooltip("spell.name.ghostly_armor"), "Ghostly Armor");
        add(LangUtil.tooltip("spell.name.spectral_armor"), "Spectral Armor");
        add(LangUtil.tooltip("spell.name.shadow_armor"), "Shadow Armor");
        add(LangUtil.tooltip("spell.name.disembodied_armor"), "Disembodied Armor");

        add(LangUtil.tooltip("spell.spectral_armor.rate"), "Reduces %s mob damage.");

        ///// drain /////
        add(LangUtil.tooltip("spell.name.drain"), "Drain");
        add(LangUtil.tooltip("spell.name.greater_drain"), "Greater Drain");
        add(LangUtil.tooltip("spell.name.desiccate"), "Desiccate");

        add(LangUtil.tooltip("spell.drain.rate"), "Drains %s hp from mobs within %s blocks every %s seconds.");

        ///// fire resistance /////
        add(LangUtil.tooltip("spell.name.fire_resistance"), "Fire Resistance");
        add(LangUtil.tooltip("spell.name.fire_ward"), "Fire Ward");
        add(LangUtil.tooltip("spell.name.blessing_of_the_phoenix"), "Blessing of the Phoenix");

        add(LangUtil.tooltip("spell.fire_resistance.rate"), "Resists %s fire damage.");

        ///// magic resistance /////
        add(LangUtil.tooltip("spell.name.magic_resistance"), "Magic Resistance");
        add(LangUtil.tooltip("spell.name.magic_ward"), "Magic Ward");
        add(LangUtil.tooltip("spell.name.salandaars_magic_coat"), "Sal'andaar's Magic Coat");

        add(LangUtil.tooltip("spell.magic_resistance.rate"), "Resists %s magic damage.");

        ///// wither resistance /////
        add(LangUtil.tooltip("spell.name.wither_resistance"), "Wither Resistance");
        add(LangUtil.tooltip("spell.name.wither_ward"), "Wither Ward");
        add(LangUtil.tooltip("spell.name.withers_skin"), "Wither's Skin");

        add(LangUtil.tooltip("spell.wither_resistance.rate"), "Resists %s wither damage.");

        ///// reflection /////
        add(LangUtil.tooltip("spell.name.reflection"), "Reflection");
        add(LangUtil.tooltip("spell.name.crushing_response"), "Crushing Response");

        add(LangUtil.tooltip("spell.reflection.rate"), "Reflects %s damage back onto mob. Cooldown: %s seconds.");

        ///// paladin strike /////
        add(LangUtil.tooltip("spell.name.paladin_strike"), "Paladin Strike");
        add(LangUtil.tooltip("spell.name.paladin_smite"), "Paladin Smite");

        add(LangUtil.tooltip("spell.paladin_strike.rate"), "Inflicts extra %s hp every %s seconds costing %s mana and %s hp.");

        ///// satiety /////
        add(LangUtil.tooltip("spell.name.satiety"), "Satiety");
        add(LangUtil.tooltip("spell.satiety.rate"), "Restores 0.5 hunger every %s seconds.");

        ///// cheat death /////
        add(LangUtil.tooltip("spell.name.cheat_death"), "Cheat Death");
        add(LangUtil.tooltip("spell.cheat_death.rate"), "Prevents death. Cooldown: %s seconds.");

        ///// strength /////
        add(LangUtil.tooltip("spell.name.quick_strength"), "Quick Strength");
        add(LangUtil.tooltip("spell.name.strength"), "Strength");
        add(LangUtil.tooltip("spell.name.greater_strength"), "Greater Strength");
        add(LangUtil.tooltip("spell.name.giant_strength"), "Giant Strength");
        add(LangUtil.tooltip("spell.strength.rate"), "Bestows Strength effect for %s seconds. Cooldown: %s seconds.");

        ///// speed /////
        add(LangUtil.tooltip("spell.name.speed"), "Speed");
        add(LangUtil.tooltip("spell.name.greater_speed"), "Greater Speed");
        add(LangUtil.tooltip("spell.name.horse_power"), "Horse Power");
        add(LangUtil.tooltip("spell.speed.rate"), "Bestows Speed effect for %s seconds. Cooldown: %s seconds.");

        ///// night vision /////
        add(LangUtil.tooltip("spell.name.night_vision"), "Night Vision");
        add(LangUtil.tooltip("spell.name.greater_night_vision"), "Greater Night Vision");
        add(LangUtil.tooltip("spell.name.cat_sight"), "Cat Sight");
        add(LangUtil.tooltip("spell.night_vision.rate"), "Bestows Night Vision effect for %s seconds. Cooldown: %s seconds.");

        ///// invisibility /////
        add(LangUtil.tooltip("spell.name.invisibility"), "Invisibility");
        add(LangUtil.tooltip("spell.name.greater_invisibility"), "Greater Invisibility");
        add(LangUtil.tooltip("spell.invisibility.rate"), "Bestows Invisibility effect for %s seconds. Cooldown: %s seconds.");

        ///// water-breathing /////
        add(LangUtil.tooltip("spell.name.water_breathing"), "Water Breathing");
        add(LangUtil.tooltip("spell.name.greater_water_breathing"), "Greater Water Breathing");
        add(LangUtil.tooltip("spell.water_breathing.rate"), "Bestows Water Breathing effect for %s seconds. Cooldown: %s seconds.");

        ///// harm /////
        add(LangUtil.tooltip("spell.name.harm"), "Harm");
        add(LangUtil.tooltip("spell.name.mind_jab"), "Mind Jab");
        add(LangUtil.tooltip("spell.name.mind_fist"), "Mind Fist");
        add(LangUtil.tooltip("spell.harm.rate"), "Inflicts %s hp to a single mob within %s blocks. Cooldown: %s seconds.");


        // scrolls
        add(LangUtil.tooltip("spell_scroll.usage"), "Place on an anvil with Magic Treasures jewelry to combine.");

        add(LangUtil.tooltip("jewelry_recipe_scroll.usage"), "Combine with 4 ingots of respective material on crafting table to craft jewelry item.");

        /*
         * screens
         */
        // chests
        //add(LangUtil.screen("wood_chest.name"), "Wood Chest");
   
        /*
         *  chat
         */
        // keys
        //add(LangUtil.chat("key.key_break"), "Your key broke whilst attempting to unlock the lock!");
    
    }
}
