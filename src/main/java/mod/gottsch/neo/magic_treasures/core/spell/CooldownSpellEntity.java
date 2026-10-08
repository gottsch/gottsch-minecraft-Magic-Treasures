package mod.gottsch.neo.magic_treasures.core.spell;

import mod.gottsch.neo.magic_treasures.core.component.SpellData;

/**
 *
 * @author Mark Gottschling May 10, 2024
 *
 */
public class CooldownSpellEntity extends SpellEntity {

    private double cooldownExpireTime;

    public CooldownSpellEntity() {}

    public CooldownSpellEntity(ISpell spell) {
        super(spell);
    }

    public double getCooldownExpireTime() {
        return cooldownExpireTime;
    }

    /**
     * also writes the new expire time back to the jewelry stack this entity is bound to
     */
    public void setCooldownExpireTime(double cooldownExpireTime) {
        this.cooldownExpireTime = cooldownExpireTime;
        writeBack();
    }

    @Override
    public SpellData toData() {
        return super.toData().withCooldownExpireTime(cooldownExpireTime);
    }

    @Override
    public void load(SpellData data) {
        super.load(data);
        this.cooldownExpireTime = data.cooldownExpireTime();
    }
}
