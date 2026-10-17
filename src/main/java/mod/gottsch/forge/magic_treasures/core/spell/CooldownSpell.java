package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.gottschcore.spatial.ICoords;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

import java.util.Random;

/**
 * @author Mark Gottschling on May 16, 2024
 *
 */
public abstract class CooldownSpell extends Spell {

    public CooldownSpell(Builder builder) {
        super(builder);
    }

    @Override
    public SpellEntity entity() {
        return new CooldownSpellEntity(this);
    }

    @Override
    public boolean serverUpdate(Level world, Random random, ICoords coords, Event event, ICastSpellContext context) {
        boolean result = false;

        if (!(context.getEntity() instanceof CooldownSpellEntity spellEntity)) {
            return result;
        }

        double cooldown = modifyCooldown(context.getJewelry());
        // check if supports cooldown or if world time has exceeded the entity cooldown end time
        if (cooldown <= 0.0 || (world.getGameTime() > spellEntity.getCooldownExpireTime())) {
            result = execute(world, random, coords, event, context);
            if(result) {
                // update cooldown expire time
                updateExpireTime(world, random, context.getJewelry(), context.getEntity(), cooldown);
//                context.getPlayer().getCooldowns().addCooldown(context.getJewelry().getItem(), (int)cooldown);
            }
        }
        return result;
    }

    abstract public boolean execute(Level world, Random random, ICoords coords, Event event, ICastSpellContext context);

    public void updateExpireTime(Level level, Random random, ItemStack jewelry, SpellEntity entity, double cooldown) {
        // set cooldown expire time if cooldown is activated
        if (modifyCooldown(jewelry) > 0.0) {
            ((CooldownSpellEntity) entity).setCooldownExpireTime(Long.valueOf(level.getGameTime()).doubleValue() + cooldown);
        }
    }
}
