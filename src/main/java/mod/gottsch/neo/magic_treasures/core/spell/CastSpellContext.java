package mod.gottsch.neo.magic_treasures.core.spell;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Gottschling on 5/17/2024
 */
public class CastSpellContext implements ICastSpellContext {
    private ItemStack jewelry;
    private SpellEntity entity;
    private List<ItemStack> manaWells;
    private Player player;

    public CastSpellContext(ItemStack jewelry, List<ItemStack> manaWells, SpellEntity entity, Player player) {
        this.jewelry = jewelry;
        this.manaWells = manaWells;
        this.entity = entity;
        this.player = player;
    }

    @Override
    public ItemStack getJewelry() {
        return jewelry;
    }

    public void setJewelry(ItemStack jewelry) {
        this.jewelry = jewelry;
    }

    @Override
    public SpellEntity getEntity() {
        return entity;
    }

    public void setEntity(SpellEntity entity) {
        this.entity = entity;
    }

    @Override
    public List<ItemStack> getManaWells() {
        if (manaWells == null) {
            this.manaWells = new ArrayList<>();
        }
        return manaWells;
    }

    public void setManaWells(List<ItemStack> manaWells) {
        this.manaWells = manaWells;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
