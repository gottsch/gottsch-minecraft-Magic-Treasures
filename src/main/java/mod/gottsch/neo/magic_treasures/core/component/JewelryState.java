package mod.gottsch.neo.magic_treasures.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

/**
 * The play state of a generic piece of jewelry: what changes while it is used. Max values and factors are never
 * stored; they are computed from the identity ({@link mod.gottsch.neo.magic_treasures.core.jewelry.data.JewelryStats}),
 * so a datapack change reaches every existing stack.
 *
 * @param uses      remaining uses; {@link Integer#MAX_VALUE} = infinite
 * @param mana      current mana
 * @param repairs   remaining repairs
 * @param recharges remaining recharges
 * @param spells    imbued spells, with their cooldown expiry
 */
public record JewelryState(int uses, double mana, int repairs, int recharges, List<SpellData> spells) {

    public static final Codec<JewelryState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("uses").forGetter(JewelryState::uses),
            Codec.DOUBLE.fieldOf("mana").forGetter(JewelryState::mana),
            Codec.INT.fieldOf("repairs").forGetter(JewelryState::repairs),
            Codec.INT.fieldOf("recharges").forGetter(JewelryState::recharges),
            SpellData.CODEC.listOf().optionalFieldOf("spells", List.of()).forGetter(JewelryState::spells)
    ).apply(instance, JewelryState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, JewelryState> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    /** read when a stack has no state and its stats can't be computed (unknown material, or no data loaded yet) */
    public static final JewelryState EMPTY = new JewelryState(0, 0, 0, 0, List.of());

    public JewelryState {
        spells = List.copyOf(spells);
    }

    public JewelryState withUses(int uses) {
        return new JewelryState(uses, mana, repairs, recharges, spells);
    }

    public JewelryState withMana(double mana) {
        return new JewelryState(uses, mana, repairs, recharges, spells);
    }

    public JewelryState withRepairs(int repairs) {
        return new JewelryState(uses, mana, repairs, recharges, spells);
    }

    public JewelryState withRecharges(int recharges) {
        return new JewelryState(uses, mana, repairs, recharges, spells);
    }

    public JewelryState withSpells(List<SpellData> spells) {
        return new JewelryState(uses, mana, repairs, recharges, spells);
    }

    public JewelryState withSpell(int index, SpellData spell) {
        List<SpellData> list = new ArrayList<>(spells);
        list.set(index, spell);
        return withSpells(list);
    }

    public JewelryState addSpell(SpellData spell) {
        List<SpellData> list = new ArrayList<>(spells);
        list.add(spell);
        return withSpells(list);
    }
}
