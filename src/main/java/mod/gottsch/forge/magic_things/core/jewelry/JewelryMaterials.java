package mod.gottsch.forge.magic_things.core.jewelry;

/**
 * @author Mark Gottschling May 8, 2024
 *
 */
// TODO setup each metal for cost, effect, duration, freq, range modifiers
public class JewelryMaterials {
    public static final JewelryMaterial NONE = new JewelryMaterial.Builder("none", 0, 0)
            .with($ -> {
                $.maxLevel = 0;
                $.acceptsAffixer = a -> false;
            }).build();

    public static final JewelryMaterial WOOD = new JewelryMaterial.Builder("wood", 40, 20)
            .with(
                    $ -> $.maxLevel = 2
            ).build();

    public static final JewelryMaterial IRON = new JewelryMaterial.Builder("iron", 200, 50)
            .with($ -> {
                $.repairs = 3;
                $.maxLevel = 3;
                $.spellDurationFactor = 1.5;
            }).build();

    public static final JewelryMaterial COPPER = new JewelryMaterial.Builder("copper", 75, 50)
            .with($ -> {
                $.repairs = 1;
                $.maxLevel = 4;
                $.spellCooldownFactor = 0.85;
                $.spellFrequencyFactor = 0.85;
            }).build();

    public static final JewelryMaterial SILVER = new JewelryMaterial.Builder("silver", 150, 75)
            .with($ -> {
                $.repairs = 2;
                $.maxLevel = 5;
                $.spellCostFactor = 0.9;
            }).build();

    public static final JewelryMaterial GOLD = new JewelryMaterial.Builder("gold", 150, 100)
            .with($ -> {
                $.repairs = 2;
                $.maxLevel = 6;
                $.spellCostFactor = 0.75; // cost is reduced by half.
            }).build();

    public static final JewelryMaterial BONE = new JewelryMaterial.Builder("bone", 200, 50)
            .with($ -> {
                $.repairs = 0;
                $.maxLevel = 7;
                $.spellEffectAmountFactor = 1.5;
            }).build();

    // aka hemalsteel
    public static final JewelryMaterial BLOOD = new JewelryMaterial.Builder("blood", 250, 100)
            .with($ -> {
                $.repairs = 2;
                $.maxLevel = 8;
            }).build();

    public static final JewelryMaterial SHADOW = new JewelryMaterial.Builder("shadow", 275, 125)
            .with($ -> {
                $.repairs = 3;
                $.maxLevel = 9;
                $.spells = 2;
                $.spellCostFactor = 1.25; // NOTE costs more!
                $.spellEffectAmountFactor = 2.0;
            }).build();

    public static final JewelryMaterial ATIUM = new JewelryMaterial.Builder("atium", 325, 200)
            .with($ -> {
                $.repairs = 3;
                $.maxLevel = 10;
                $.spells = 2;
                $.spellCostFactor = 0.5;
                $.spellCooldownFactor = 0.5;
                $.spellDurationFactor = 1.5;
                $.spellEffectAmountFactor = 1.5;
                $.spellFrequencyFactor = 0.5;
                $.spellRangeFactor = 1.25;
            }).build();
}
