
package mod.gottsch.neo.magic_treasures.core.spell.cost;

import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neo.magic_treasures.core.spell.ICastSpellContext;
import mod.gottsch.neo.magic_treasures.core.spell.SpellEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Calculates and applies the cost to execute the jewelry.
 * @author Mark Gottschling on Jan 12, 2022
 *
 */
public interface ICostEvaluator {
	public double apply(Level level, Random random, ICoords coords, ICastSpellContext context, double amount);

	@Deprecated
	default public CompoundTag save(CompoundTag tag) {
		return tag;
	}

	@Deprecated
	default public void load(CompoundTag tag) {}
}
