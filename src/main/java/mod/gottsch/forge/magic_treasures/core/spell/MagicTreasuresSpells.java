package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;

/**
 *
 */
public class MagicTreasuresSpells {
    public static final String HEALING = "healing";
    public static final String GREATER_HEALING = "greater_healing";
    public static final String REGENERATION = "regeneration";
    public static final String SATIETY = "satiety";
    public static final String MANA_TOWER_SHIELD = "mana_tower_shield";
    public static final String MANA_PAVISE_SHIELD = "mana_pavise_shield";
    public static final String CHEAT_DEATH = "cheat_death";
    public static final String HARM = "harm";
    public static final String DRAIN = "drain";
    public static final String GREATER_DRAIN = "greater_drain";
    public static final String DESICCATE = "desiccate";
    public static final String GHOSTLY_ARMOR = "ghostly_armor";
    public static final String SPECTRAL_ARMOR = "spectral_armor";
    public static final String SHADOW_ARMOR = "shadow_armor";
    public static final String REFLECTION = "reflection";
    public static final String FIRE_RESISTANCE = "fire_resistance";
    public static final String FIRE_WARD = "fire_ward";
    public static final String MAGIC_RESISTANCE = "magic_resistance";
    public static final String BLESSING_OF_THE_PHOENIX = "blessing_of_the_phoenix";
    public static final String QUICK_STRENGTH = "quick_strength";
    public static final String STRENGTH = "strength";
    public static final String SPEED = "speed";
    public static final String NIGHT_VISION = "night_vision";
    public static final String WATER_BREATHING = "water_breathing";

    // TODO need to change variable name
    // default spell
    public static final ISpell DEFAULT_HEALING = SpellRegistry.register(new HealingSpell.Builder(ModUtil.asLocation("lesser_healing"), 1, MagicTreasuresRarity.COMMON)
            .with($ -> {
                $.spellCost = 2.0;
                $.effectAmount = 1.0;
                $.frequency = 200; // 10 seconds
                $.effectStackable = true;
            }).build());

    public static final ISpell MAGIC_RESISTANCE_SPELL;
    public static final ISpell NIGHT_VISION_SPELL;
    public static final ISpell SPEED_SPELL;
    public static final ISpell WATER_BREATHING_SPELL;

    // NOTE - this spell is not registered as it only occurs on a special item (belt of silbro)
   public static final ISpell SILBROS_INVISIBILITY = new InvisibilitySpell.Builder(ModUtil.asLocation("silbros_invisibility"), 8, MagicTreasuresRarity.EPIC)
           .withAmplifier(0)
           .with($ -> {
                $.spellCost = 5;
                $.duration = 600;
                $.cooldown = 400;
                $.effectStackable = false;
            }).build();

    static {
        // register all spells
        SpellRegistry.register(DEFAULT_HEALING);

        ///// healing spells /////
        SpellRegistry.register(new HealingSpell.Builder(ModUtil.asLocation("healing"), 3, MagicTreasuresRarity.UNCOMMON).with($ -> {
            $.spellCost = 3.0;
            $.effectAmount = 2.0;
            $.frequency = 180; // 9 seconds
            $.effectStackable = true;
        })	.build());

        SpellRegistry.register(new HealingSpell.Builder(ModUtil.asLocation("greater_healing"), 5, MagicTreasuresRarity.SCARCE).with($ -> {
            $.spellCost = 4;
            $.effectAmount = 3.0;
            $.frequency = 160; // 8 seconds
            $.effectStackable = true;
        })	.build());

        SpellRegistry.register(new HealingSpell.Builder(ModUtil.asLocation("regeneration"), 8, MagicTreasuresRarity.RARE).with($ -> {
            $.spellCost = 5.0;
            $.effectAmount = 5.0;
            $.frequency = 140; // 7 seconds
            $.effectStackable = true;
        })	.build());

        ///// shield spells /////
        SpellRegistry.register(new ManaShieldSpell.Builder(ModUtil.asLocation("mana_buckler"), 2, MagicTreasuresRarity.UNCOMMON).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.25;
            $.cooldown = 160; // 8 seconds
            $.effectStackable = true;
            $.priority = 2;
        })	.build());

        SpellRegistry.register(new ManaShieldSpell.Builder(ModUtil.asLocation("mana_shield"), 4, MagicTreasuresRarity.SCARCE).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.5;
            $.cooldown = 140; // 7 seconds
            $.effectStackable = true;
            $.priority = 2;
        })	.build());

        SpellRegistry.register(new ManaShieldSpell.Builder(ModUtil.asLocation(MANA_TOWER_SHIELD), 6, MagicTreasuresRarity.RARE).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.75;
            $.cooldown = 120; // 6 seconds
            $.effectStackable = true;
            $.priority = 2;
        })	.build());

        SpellRegistry.register(new ManaShieldSpell.Builder(ModUtil.asLocation(MANA_PAVISE_SHIELD), 8, MagicTreasuresRarity.EPIC).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 1.0;
            $.cooldown = 100; // 5 seconds
            $.effectStackable = true;
            $.priority = 2;
        })	.build());

        // spectral armor spells
        SpellRegistry.register(new SpectralArmorSpell.Builder(ModUtil.asLocation("ghostly_armor"), 1, MagicTreasuresRarity.COMMON).with($ -> {
            $.spellCost = 2;
            $.effectAmount = 3.0;
            $.cooldown = 0; // 0 seconds
            $.effectStackable = false;
            $.priority = 3;
        })	.build());

        SpellRegistry.register(new SpectralArmorSpell.Builder(ModUtil.asLocation("spectral_armor"), 3, MagicTreasuresRarity.UNCOMMON).with($ -> {
            $.spellCost = 3;
            $.effectAmount = 5.0;
            $.cooldown = 0; // 0 seconds
            $.effectStackable = false;
            $.priority = 3;
        })	.build());

        SpellRegistry.register(new SpectralArmorSpell.Builder(ModUtil.asLocation(SHADOW_ARMOR), 5, MagicTreasuresRarity.SCARCE).with($ -> {
            $.spellCost = 4;
            $.effectAmount = 6.0;
            $.cooldown = 1; // 0 seconds
            $.effectStackable = false;
            $.priority = 3;
        })	.build());

        SpellRegistry.register(new SpectralArmorSpell.Builder(ModUtil.asLocation("disembodied_armor"), 7, MagicTreasuresRarity.RARE).with($ -> {
            $.spellCost = 5;
            $.effectAmount = 8.0;
            $.cooldown = 1; // 0 seconds
            $.effectStackable = false;
            $.priority = 3;
        })	.build());

        ///// drain spells /////
        SpellRegistry.register(new DrainSpell.Builder(ModUtil.asLocation("drain"), 3, MagicTreasuresRarity.UNCOMMON)
                .with($ -> {
                    $.spellCost = 2.0;
                    $.effectAmount = 2.0;
                    $.frequency = 180; // 9 seconds
                    $.range = 2.0;
                    $.effectStackable = true;
                })	.build());

        SpellRegistry.register(new DrainSpell.Builder(ModUtil.asLocation(GREATER_DRAIN), 6, MagicTreasuresRarity.SCARCE)
                .with($ -> {
                    $.spellCost = 3.0;
                    $.effectAmount = 4.0;
                    $.frequency = 150; // 7.5 seconds
                    $.range = 4.0;
                    $.effectStackable = true;
                })	.build());

        SpellRegistry.register(new DrainSpell.Builder(ModUtil.asLocation(DESICCATE), 9, MagicTreasuresRarity.EPIC)
                .with($ -> {
                    $.spellCost = 4.0;
                    $.effectAmount = 6.0;
                    $.frequency = 120; // 6 seconds
                    $.range = 6.0;
                    $.effectStackable = true;
                })	.build());

//        SpellRegistry.register(new VampiresBiteSpell.Builder(ModUtil.asLocation("vampires_bite"), 10, MagicTreasuresRarity.LEGENDARY)
//                .with($ -> {
//                    $.spellCost = 4.0;
//                    $.effectAmount = 0;
//                    $.frequency = 100; // 6 seconds
//                    $.range = 6.0;
//                    $.effectStackable = false;
//                })	.build());

        ///// fire resistance spells /////
        SpellRegistry.register(new FireResistanceSpell.Builder(ModUtil.asLocation("fire_resistance"), 1, MagicTreasuresRarity.COMMON).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.3;
            $.effectStackable = true;
            $.priority = 2;
        })	.build());

        SpellRegistry.register(new FireResistanceSpell.Builder(ModUtil.asLocation("fire_ward"), 4, MagicTreasuresRarity.SCARCE).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.7;
            $.effectStackable = true;
            $.priority = 2;
        })	.build());

        SpellRegistry.register(new FireResistanceSpell.Builder(ModUtil.asLocation(BLESSING_OF_THE_PHOENIX), 7, MagicTreasuresRarity.RARE).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 1.0;
            $.effectStackable = false;
            $.priority = 1;
        })	.build());

        ///// magic resistance spells /////
        MAGIC_RESISTANCE_SPELL = SpellRegistry.register(new MagicResistanceSpell.Builder(ModUtil.asLocation(MAGIC_RESISTANCE), 2, MagicTreasuresRarity.COMMON).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.3;
            $.effectStackable = true;
            $.priority = 1;
        })	.build());

        SpellRegistry.register(new MagicResistanceSpell.Builder(ModUtil.asLocation("magic_ward"), 5, MagicTreasuresRarity.SCARCE).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.7;
            $.effectStackable = true;
            $.priority = 1;
        })	.build());

        SpellRegistry.register(new MagicResistanceSpell.Builder(ModUtil.asLocation("salandaars_magic_coat"), 8, MagicTreasuresRarity.EPIC).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 1.0;
            $.effectStackable = false;
            $.priority = 0;
        })	.build());

        ///// wither resistance spells /////
        SpellRegistry.register(new WitherResistanceSpell.Builder(ModUtil.asLocation("wither_resistance"), 3, MagicTreasuresRarity.UNCOMMON).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.3;
            $.effectStackable = true;
            $.priority = 1;
        })	.build());

        SpellRegistry.register(new WitherResistanceSpell.Builder(ModUtil.asLocation("wither_ward"), 6, MagicTreasuresRarity.RARE).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.7;
            $.effectStackable = true;
            $.priority = 1;
        })	.build());

        SpellRegistry.register(new WitherResistanceSpell.Builder(ModUtil.asLocation("withers_skin"), 8, MagicTreasuresRarity.EPIC).with($ -> {
            $.spellCost = 0;
            $.effectAmount = 0.9;
            $.effectStackable = true;
            $.priority = 1;
        })	.build());

        ///// satiety spells /////
        SpellRegistry.register(new SatietySpell.Builder(ModUtil.asLocation(SATIETY), 2, MagicTreasuresRarity.COMMON).with($ -> {
            $.spellCost = 3.0;
            $.effectAmount = 1.0;
            $.frequency = 100; // 5 seconds
            $.effectStackable = true;
            $.priority = 1;
        })	.build());

        // reflection spells
        SpellRegistry.register(new ReflectionSpell.Builder(ModUtil.asLocation(REFLECTION), 3, MagicTreasuresRarity.UNCOMMON).with($ -> {
            $.spellCost = 5; // cost = min(spellCost, reflected amount)
            $.effectAmount = 1.0;
            $.cooldown = 100; // 5 seconds
            $.range = 3.0;
            $.effectStackable = true;
        })	.build());
        SpellRegistry.register(new ReflectionSpell.Builder(ModUtil.asLocation("crushing_response"), 6, MagicTreasuresRarity.RARE).with($ -> {
            $.spellCost = 8; // cost = min(spellCost, reflected amount)
            $.effectAmount = 2.0;
            $.cooldown = 100; // 5 seconds
            $.range = 6.0;
            $.effectStackable = true;
        })	.build());

        // paladin strike spells /////
        SpellRegistry.register(new PaladinStrikeSpell.Builder(ModUtil.asLocation("paladin_strike"), 4, MagicTreasuresRarity.SCARCE)
                .withLifeCost(2.0)
                .with($ -> {
                    $.spellCost = 1.0;
                    $.effectAmount = 2.0;
                    $.cooldown = 100; // 5 seconds
                    $.effectStackable = false;
                })	.build());

        SpellRegistry.register(new PaladinStrikeSpell.Builder(ModUtil.asLocation("paladin_smite"), 8, MagicTreasuresRarity.SCARCE)
                .withLifeCost(2.0)
                .with($ -> {
                    $.spellCost = 2.0;
                    $.effectAmount = 4.0;
                    $.cooldown = 100; // 5 seconds
                    $.effectStackable = false;
                })	.build());

        ///// cheat death spells /////
        SpellRegistry.register(new CheatDeathSpell.Builder(ModUtil.asLocation(CHEAT_DEATH), 8, MagicTreasuresRarity.EPIC).with($ -> {
            $.spellCost = 50.0;
            $.effectAmount = 6.0;
            $.cooldown = 2400; // 2 minutes
            $.effectStackable = false;
            $.priority = 0;
        })	.build());

        // harm spells
        SpellRegistry.register(new HarmSpell.Builder(ModUtil.asLocation(HARM), 2, MagicTreasuresRarity.COMMON).with($ -> {
            $.spellCost = 2; // cost = min(spellCost, reflected amount)
            $.effectAmount = 2.0;
            $.cooldown = 200; // 10 seconds
            $.range = 2.0;
            $.effectStackable = true;
        })	.build());

        SpellRegistry.register(new HarmSpell.Builder(ModUtil.asLocation("mind_jab"), 4, MagicTreasuresRarity.UNCOMMON).with($ -> {
            $.spellCost = 4; // cost = min(spellCost, reflected amount)
            $.effectAmount = 3.0;
            $.cooldown = 180;
            $.range = 2.5;
            $.effectStackable = true;
        })	.build());

        SpellRegistry.register(new HarmSpell.Builder(ModUtil.asLocation("mind_fist"), 6, MagicTreasuresRarity.RARE).with($ -> {
            $.spellCost = 5; // cost = min(spellCost, reflected amount)
            $.effectAmount = 4.0;
            $.cooldown = 160;
            $.range = 3.0;
            $.effectStackable = true;
        })	.build());

        // strength spells
        SpellRegistry.register(new StrengthSpell.Builder(ModUtil.asLocation(QUICK_STRENGTH), 1, MagicTreasuresRarity.COMMON)
            .withAmplifier(0)
                .with($ -> {
                $.spellCost = 4; // cost = min(spellCost, reflected amount)
                $.duration = 1200;
                $.cooldown = 2400;
                $.effectStackable = false;
            })	.build());

        SpellRegistry.register(new StrengthSpell.Builder(ModUtil.asLocation(STRENGTH), 3, MagicTreasuresRarity.UNCOMMON)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 6; // cost = min(spellCost, reflected amount)
                    $.duration = 3600;
                    $.cooldown = 4800;
                    $.effectStackable = false;
                })	.build());

        SpellRegistry.register(new StrengthSpell.Builder(ModUtil.asLocation("greater_strength"), 5, MagicTreasuresRarity.SCARCE)
                .withAmplifier(1)
                .with($ -> {
                    $.spellCost = 10; // cost = min(spellCost, reflected amount)
                    $.duration = 4800;
                    $.cooldown = 6000;
                    $.effectStackable = false;
                })	.build());

        SpellRegistry.register(new StrengthSpell.Builder(ModUtil.asLocation("giant_strength"), 7, MagicTreasuresRarity.RARE)
                .withAmplifier(2)
                .with($ -> {
                    $.spellCost = 15;
                    $.duration = 6000;
                    $.cooldown = 6000;
                    $.effectStackable = false;
                })	.build());

        // speed spells
        SPEED_SPELL = SpellRegistry.register(new SpeedSpell.Builder(ModUtil.asLocation(SPEED), 2, MagicTreasuresRarity.COMMON)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 4; // cost = min(spellCost, reflected amount)
                    $.duration = 1200;
                    $.cooldown = 2400;
                    $.effectStackable = false;
                })	.build());

        SpellRegistry.register(new SpeedSpell.Builder(ModUtil.asLocation("greater_speed"), 4, MagicTreasuresRarity.UNCOMMON)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 6; // cost = min(spellCost, reflected amount)
                    $.duration = 2400;
                    $.cooldown = 3600;
                    $.effectStackable = false;
                })	.build());

        SpellRegistry.register(new SpeedSpell.Builder(ModUtil.asLocation("horse_power"), 6, MagicTreasuresRarity.SCARCE)
                .withAmplifier(1)
                .with($ -> {
                    $.spellCost = 8; // cost = min(spellCost, reflected amount)
                    $.duration = 3600;
                    $.cooldown = 3600;
                    $.effectStackable = false;
                })	.build());

        // night vision spells
        NIGHT_VISION_SPELL = SpellRegistry.register(new NightVisionSpell.Builder(ModUtil.asLocation(NIGHT_VISION), 1, MagicTreasuresRarity.COMMON)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 4;
                    $.duration = 1200;
                    $.cooldown = 2400;
                    $.effectStackable = false;
                })	.build());

        SpellRegistry.register(new NightVisionSpell.Builder(ModUtil.asLocation("greater_night_vision"), 3, MagicTreasuresRarity.UNCOMMON)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 6;
                    $.duration = 2400;
                    $.cooldown = 3600;
                    $.effectStackable = false;
                })	.build());

        SpellRegistry.register(new NightVisionSpell.Builder(ModUtil.asLocation("cat_sight"), 5, MagicTreasuresRarity.SCARCE)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 6;
                    $.duration = 3600;
                    $.cooldown = 3600;
                    $.effectStackable = false;
                })	.build());

        // invisibility spells
        SpellRegistry.register(new InvisibilitySpell.Builder(ModUtil.asLocation("invisibility"), 5, MagicTreasuresRarity.SCARCE)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 10;
                    $.duration = 1200;
                    $.cooldown = 2400;
                    $.effectStackable = false;
                })	.build());

        SpellRegistry.register(new InvisibilitySpell.Builder(ModUtil.asLocation("greater_invisibility"), 7, MagicTreasuresRarity.RARE)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 20;
                    $.duration = 2400;
                    $.cooldown = 3600;
                    $.effectStackable = false;
                })	.build());

        // water breathing spells
        WATER_BREATHING_SPELL = SpellRegistry.register(new WaterBreathingSpell.Builder(ModUtil.asLocation(WATER_BREATHING), 2, MagicTreasuresRarity.COMMON)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 5;
                    $.duration = 1200;
                    $.cooldown = 2400;
                    $.effectStackable = false;
                })	.build());

        SpellRegistry.register(new WaterBreathingSpell.Builder(ModUtil.asLocation("greater_water_breathing"), 4, MagicTreasuresRarity.SCARCE)
                .withAmplifier(0)
                .with($ -> {
                    $.spellCost = 10;
                    $.duration = 2400;
                    $.cooldown = 2400;
                    $.effectStackable = false;
                })	.build());
    }

    /*
     * a do-nothing method used to force-load static properties
     */
    public static void init() {
    }
}
