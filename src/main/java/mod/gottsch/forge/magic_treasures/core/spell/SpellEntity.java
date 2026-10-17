package mod.gottsch.forge.magic_treasures.core.spell;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.util.ModUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Optional;

/**
 * Still need the entity but its purpose is to store additional data that needs to be
 * maintained to proper function.
 *
 */
public class SpellEntity {
    public static final String SPELL = "spell";
    public static final String NAME = "name";

    public ISpell spell;

    /**
     * 
     */
    public SpellEntity() {}
    
    public SpellEntity(ISpell spell) {
        this.spell = spell;
    }

    // TODO this doesn't seem like it is used anywhere
    /**
     * Client-side. Only update those properties that need to be reflected on the client-side.
     */
    public void clientUpdate(ItemStack stack) {

    }

    /**
     * saves directly to the tag provided. ie does not make a new tag and append to tag param
     * @param tag
     * @return
     */
    public CompoundTag save(CompoundTag tag) {
        if (ObjectUtils.isNotEmpty(spell.getName())) {
            tag.putString(NAME, spell.getName().toString());
        }
        return tag;
    }

    public boolean load(CompoundTag tag) {
        if (tag.contains(SpellEntity.NAME)) {
            ResourceLocation location = ModUtil.asLocation(tag.getString(SpellEntity.NAME));
            Optional<ISpell> spell = SpellRegistry.get(location);
           spell.ifPresentOrElse(this::setSpell, () -> MagicTreasures.LOGGER.warn("unable to spell %s in registry.", location.toString()));
        }

        return true;
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
