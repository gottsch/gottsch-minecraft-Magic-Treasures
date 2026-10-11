package mod.gottsch.neo.magic_treasures.core.capability;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.api.MagicTreasuresApi;
import mod.gottsch.neo.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.neo.magic_treasures.core.component.JewelryState;
import mod.gottsch.neo.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.neo.magic_treasures.core.component.SpellData;
import mod.gottsch.neo.magic_treasures.core.item.IJewelrySizeTier;
import mod.gottsch.neo.magic_treasures.core.item.IJewelryType;
import mod.gottsch.neo.magic_treasures.core.item.JewelryType;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterial;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryMaterials;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryStoneTier;
import mod.gottsch.neo.magic_treasures.core.jewelry.JewelryStoneTiers;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.GemstoneDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryDataIndex;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryMaterialDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryPresetDef;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryRules;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryStats;
import mod.gottsch.neo.magic_treasures.core.jewelry.data.SpellFactors;
import mod.gottsch.neo.magic_treasures.core.rarity.MagicTreasuresRarity;
import mod.gottsch.neo.magic_treasures.core.registry.JewelryStoneTierRegistry;
import mod.gottsch.neo.magic_treasures.core.spell.SpellEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * A write-through view of a 2.0 generic jewelry stack: its {@link JewelryIdentity} (what it is) and
 * {@link JewelryState} (uses, mana, repairs, recharges, spells). Max values, level and factors come from
 * {@link JewelryStats}, computed from the identity and the datapack registries, and are never stored.
 * <p>
 * Implements the 1.x {@link IJewelryHandler}, so spells, the cost evaluator, the HUD, tooltips and sets work on
 * generic jewelry unchanged. The setters for computed values ({@code setMaxMana} etc.) do nothing here: only the 1.x
 * anvil code calls them, and M5 replaces it.
 */
public class GenericJewelryHandler extends AbstractJewelryHandler {

    private GenericJewelryHandler(ItemStack stack) {
        super(stack);
    }

    /**
     * A handler for a stack with a jewelry identity, or empty. A stack without state (made by /give, or a loot
     * function that set only the identity) gets full uses, mana, repairs and recharges here, the first time it's read.
     * That waits until its stats can be computed, i.e. until the data is loaded and the material is known.
     */
    public static Optional<IJewelryHandler> get(ItemStack stack) {
        JewelryIdentity identity = stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
        if (identity == null) {
            return Optional.empty();
        }
        if (!stack.has(MagicTreasuresDataComponents.JEWELRY_STATE.get())) {
            JewelryStats stats = JewelryStats.of(identity);
            if (stats.known()) {
                stack.set(MagicTreasuresDataComponents.JEWELRY_STATE.get(), fullState(stats));
            }
        }
        return Optional.of(new GenericJewelryHandler(stack));
    }

    /** full uses, mana, repairs and recharges; spells come from presets in M8 */
    public static JewelryState fullState(JewelryStats stats) {
        return new JewelryState(stats.maxUses(), stats.maxMana(), stats.maxRepairs(), stats.maxRecharges(), List.of());
    }

    /** write one spell's persisted state back to the stack (used by bound SpellEntity instances) */
    static void updateSpell(ItemStack stack, int index, SpellData spell) {
        JewelryState state = stack.get(MagicTreasuresDataComponents.JEWELRY_STATE.get());
        if (state != null && index >= 0 && index < state.spells().size()) {
            stack.set(MagicTreasuresDataComponents.JEWELRY_STATE.get(), state.withSpell(index, spell));
        }
    }

    /*
     * identity, state and stats
     */
    public JewelryIdentity identity() {
        return stack.get(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get());
    }

    public JewelryState state() {
        return stack.getOrDefault(MagicTreasuresDataComponents.JEWELRY_STATE.get(), JewelryState.EMPTY);
    }

    public JewelryStats stats() {
        return JewelryStats.of(identity());
    }

    public MagicTreasuresRarity getRarity() {
        return stats().rarity();
    }

    private void updateState(UnaryOperator<JewelryState> operator) {
        stack.set(MagicTreasuresDataComponents.JEWELRY_STATE.get(), operator.apply(state()));
    }

    private Optional<Holder.Reference<GemstoneDef>> gemstone() {
        return identity().gem().flatMap(JewelryDataIndex::gemstone);
    }

    private Optional<Holder.Reference<JewelryPresetDef>> preset() {
        return identity().preset().flatMap(JewelryDataIndex::preset);
    }

    @Override
    protected ResourceLocation materialId() {
        return identity().material();
    }

    /*
     * 1.x types. Built-in materials and styles map to their 1.x constants; a pack's map to NONE / UNKNOWN.
     */
    @Override
    public JewelryMaterial getMaterial() {
        return MagicTreasuresApi.getJewelryMaterial(identity().material()).orElse(JewelryMaterials.NONE);
    }

    @Override
    public IJewelrySizeTier getJewelrySizeTier() {
        ResourceLocation style = identity().style();
        return style.getNamespace().equals(MagicTreasures.MOD_ID)
                ? MagicTreasuresApi.getJewelrySize(style.getPath()).orElse(JewelrySizeTier.UNKNOWN)
                : JewelrySizeTier.UNKNOWN;
    }

    @Override
    public IJewelryType getJewelryType() {
        return MagicTreasuresApi.getJewelryType(identity().type()).orElse(JewelryType.UNKNOWN);
    }

    @Override
    public JewelryStoneTier getStoneTier() {
        return gemstone().flatMap(gem -> gem.value().tier().unwrapKey())
                .flatMap(key -> JewelryStoneTierRegistry.get(key.location()))
                .orElse(JewelryStoneTiers.NONE);
    }

    /*
     * computed values
     */
    @Override
    public int getMaxUses() {
        return stats().maxUses();
    }

    @Override
    public double getMaxMana() {
        return stats().maxMana();
    }

    @Override
    public int getMaxRepairs() {
        return stats().maxRepairs();
    }

    @Override
    public int getMaxRecharges() {
        return stats().maxRecharges();
    }

    @Override
    public int getMaxLevel() {
        return stats().maxLevel();
    }

    private SpellFactors factors() {
        return stats().factors();
    }

    @Override
    public double getSpellCostFactor() {
        return factors().cost();
    }

    @Override
    public double getSpellEffectAmountFactor() {
        return factors().effect();
    }

    @Override
    public double getSpellDurationFactor() {
        return factors().duration();
    }

    @Override
    public double getSpellCooldownFactor() {
        return factors().cooldown();
    }

    @Override
    public double getSpellRangeFactor() {
        return factors().range();
    }

    // computed from the identity; only the 1.x anvil calls these, and M5 replaces it
    @Override public void setMaxUses(int maxUses) {}
    @Override public void setMaxMana(double maxMana) {}
    @Override public void setMaxRepairs(int repairs) {}
    @Override public void setMaxLevel(int maxLevel) {}
    @Override public void setMaxRecharges(int maxRecharges) {}
    @Override public void setSpellCostFactor(double spellCostFactor) {}
    @Override public void setSpellEffectAmountFactor(double spellEffectAmountFactor) {}
    @Override public void setSpellDurationFactor(double spellDurationFactor) {}
    @Override public void setSpellCooldownFactor(double spellCooldownFactor) {}
    @Override public void setSpellRangeFactor(double spellRangeFactor) {}

    /*
     * state. Reads are capped at the current max, so a datapack that lowers a material's mana takes effect on
     * existing stacks; infinite uses stay infinite.
     */
    @Override
    public int getUses() {
        int uses = state().uses();
        return uses == Integer.MAX_VALUE ? uses : Math.min(uses, getMaxUses());
    }

    @Override
    public void setUses(int uses) {
        updateState(s -> s.withUses(uses));
    }

    @Override
    public double getMana() {
        return Math.min(state().mana(), getMaxMana());
    }

    @Override
    public void setMana(double mana) {
        updateState(s -> s.withMana(mana));
    }

    @Override
    public int getRepairs() {
        return Math.min(state().repairs(), getMaxRepairs());
    }

    @Override
    public void setRepairs(int repairs) {
        updateState(s -> s.withRepairs(repairs));
    }

    @Override
    public int getRecharges() {
        return Math.min(state().recharges(), getMaxRecharges());
    }

    @Override
    public void setRecharges(int recharges) {
        updateState(s -> s.withRecharges(recharges));
    }

    @Override
    public List<SpellEntity> getSpells() {
        List<SpellData> spells = state().spells();
        List<SpellEntity> entities = new ArrayList<>(spells.size());
        for (int i = 0; i < spells.size(); i++) {
            final int index = i;
            SpellEntity.fromData(spells.get(i)).ifPresent(entity -> entities.add(entity.bind(stack, index)));
        }
        return Collections.unmodifiableList(entities);
    }

    @Override
    public void setSpells(List<SpellEntity> spells) {
        updateState(s -> s.withSpells(spells.stream().map(SpellEntity::toData).toList()));
    }

    @Override
    public void addSpell(SpellEntity spell) {
        updateState(s -> s.addSpell(spell.toData()));
    }

    /*
     * the gem. 1.x callers pass and expect the gem's *item* id; the identity stores the gemstone id. They are equal
     * for built-in gems, not for a pack's (mypack:amber is minecraft:honeycomb in the test pack).
     */
    @Override
    public ResourceLocation getStone() {
        return identity().gem().map(id -> gemstone().map(gem -> gem.value().item()).orElse(id)).orElse(null);
    }

    @Override
    public void setStone(ResourceLocation item) {
        Optional<ResourceLocation> gem = Optional.empty();
        // null or minecraft:air (what 1.x writes for "no gem") removes the gem
        if (item != null && !item.equals(BuiltInRegistries.ITEM.getKey(Items.AIR))) {
            gem = BuiltInRegistries.ITEM.getOptional(item).flatMap(JewelryDataIndex::gemstone).map(h -> h.key().location());
            if (gem.isEmpty()) {
                MagicTreasures.LOGGER.warn("setStone: {} is not a gemstone; the gem is unchanged", item);
                return;
            }
        }
        JewelryIdentity identity = identity();
        stack.set(MagicTreasuresDataComponents.JEWELRY_IDENTITY.get(),
                new JewelryIdentity(identity.type(), identity.material(), gem, identity.style(), identity.preset()));
    }

    @Override
    public boolean hasStone() {
        return gemstone().isPresent();
    }

    /** a gem may be set if the rules allow it and a preset doesn't forbid gems; anything else is accepted */
    @Override
    public boolean acceptsAffixer(ItemStack affixer) {
        Optional<Holder.Reference<GemstoneDef>> gem = JewelryDataIndex.gemstone(affixer.getItem());
        if (gem.isEmpty()) {
            return true;
        }
        Optional<Holder.Reference<JewelryMaterialDef>> material = JewelryDataIndex.material(identity().material());
        return material.isPresent()
                && JewelryRules.allowsGem(material.get(), gem.get())
                && preset().map(p -> p.value().acceptsGems()).orElse(true);
    }

    /** the 1.x base name (castle_ring etc.) is the preset's path */
    @Override
    public String getBaseName() {
        return identity().preset().map(ResourceLocation::getPath).orElse(null);
    }

    @Override
    public void setBaseName(String baseName) {
        // the preset is part of the identity; it isn't renamed through the handler
    }
}
