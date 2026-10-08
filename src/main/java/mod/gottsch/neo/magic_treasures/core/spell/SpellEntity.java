package mod.gottsch.neo.magic_treasures.core.spell;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.neo.magic_treasures.core.component.SpellData;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Still need the entity but its purpose is to store additional data that needs to be
 * maintained to proper function.
 * <p>
 * The persisted form is SpellData (in the jewelry's JewelryData component). An entity read from a
 * stack is bound to that stack and its index in the spell list, so a state change (ex. a cooldown)
 * is written straight back to the stack's component.
 */
public class SpellEntity {
    public ISpell spell;

    // the jewelry stack and spell index this entity was read from. null/-1 when free-standing (ex. in a builder)
    private ItemStack stack;
    private int index = -1;

    /**
     *
     */
    public SpellEntity() {}

    public SpellEntity(ISpell spell) {
        this.spell = spell;
    }

    /**
     * create an entity from its persisted form. empty if the spell is not registered.
     */
    public static Optional<SpellEntity> fromData(SpellData data) {
        Optional<ISpell> spell = SpellRegistry.get(data.name());
        if (spell.isEmpty()) {
            MagicTreasures.LOGGER.warn("unable to locate spell {} in registry.", data.name());
            return Optional.empty();
        }
        SpellEntity entity = spell.get().entity();
        entity.load(data);
        return Optional.of(entity);
    }

    /**
     * the persisted form of this entity
     */
    public SpellData toData() {
        return new SpellData(spell.getName());
    }

    /**
     * load the entity-specific state from the persisted form
     */
    public void load(SpellData data) {
    }

    /**
     * bind this entity to the jewelry stack and spell index it was read from
     */
    public SpellEntity bind(ItemStack stack, int index) {
        this.stack = stack;
        this.index = index;
        return this;
    }

    /**
     * write this entity's state back to the stack it is bound to, if any
     */
    protected void writeBack() {
        if (stack != null && index >= 0) {
            JewelryHandler.updateSpell(stack, index, toData());
        }
    }

    public ISpell getSpell() {
        return spell;
    }

    public void setSpell(ISpell spell) {
        this.spell = spell;
    }

    @Override
    public String toString() {
        return "SpellEntity{" +
                "spell=" + spell +
                '}';
    }
}
