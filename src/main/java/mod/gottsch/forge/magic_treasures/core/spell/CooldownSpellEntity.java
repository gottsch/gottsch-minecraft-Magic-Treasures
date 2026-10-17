package mod.gottsch.forge.magic_treasures.core.spell;

import net.minecraft.nbt.CompoundTag;

/**
 *
 * @author Mark Gottschling May 10, 2024
 *
 */
public class CooldownSpellEntity extends SpellEntity {
    public static final String COOLDOWN_EXPIRE_TIME = "cooldownExpireTime";

    private double cooldownExpireTime;

    public CooldownSpellEntity() {}

    public CooldownSpellEntity(ISpell spell) {
        super(spell);
    }

    public double getCooldownExpireTime() {
        return cooldownExpireTime;
    }

    public void setCooldownExpireTime(double cooldownExpireTime) {
        this.cooldownExpireTime = cooldownExpireTime;
    }

    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putDouble(COOLDOWN_EXPIRE_TIME, cooldownExpireTime);
        return tag;
    }

    public boolean load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(COOLDOWN_EXPIRE_TIME)) {
            setCooldownExpireTime(tag.getDouble(COOLDOWN_EXPIRE_TIME));
        }
        return true;
    }
}
