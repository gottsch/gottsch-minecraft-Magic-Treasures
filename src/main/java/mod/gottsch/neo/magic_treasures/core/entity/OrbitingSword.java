/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * Magic Treasures is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Magic Treasures is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Magic Treasures.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neo.magic_treasures.core.entity;

import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.particle.MagicTreasuresParticles;
import mod.gottsch.neo.magic_treasures.core.spell.SpellEffects;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * One sword of a Ring of Swords: it circles its owner until the spell's time runs out.
 * <p>
 * A sword that touches a hostile mob ({@link Monster} only: never players, pets or villagers, like Harm) deals its
 * damage as indirect magic with the owner as the attacker, so the mob turns on the owner. Each landed hit costs the
 * sword that much hp; at 0 it shatters. A sword waits {@link #HIT_COOLDOWN} ticks before hitting the same mob again.
 * <p>
 * The client is told only who the owner is, which sword of how many this is, and the orbit's radius; both sides
 * work the position out from the owner and the game time, so the server sends no position packets. The renderer
 * does the same with the frame's partial tick, so the ring stays smooth while the owner moves.
 * <p>
 * Never saved (the type is {@code noSave}): a logout or an unloaded chunk ends the spell. The server discards it when
 * its time is up or its owner is gone, dead or in another dimension.
 *
 * @author Mark Gottschling on 10/16/2026
 */
public class OrbitingSword extends Entity {
    /** ticks for one full turn */
    public static final int ORBIT_PERIOD = 40;
    /** height of the ring above the owner's feet: about the waist */
    public static final double ORBIT_HEIGHT = 1.0;
    /** ticks before a sword can hit the same mob again */
    public static final int HIT_COOLDOWN = 10;
    /** how far past its own box a sword reaches: the blade is longer than the entity is wide */
    private static final double HIT_REACH = 0.3;

    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(OrbitingSword.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INDEX = SynchedEntityData.defineId(OrbitingSword.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COUNT = SynchedEntityData.defineId(OrbitingSword.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(OrbitingSword.class, EntityDataSerializers.FLOAT);

    // server only
    private Player owner;
    private long expireTime;
    private float hp;
    private float damage;
    /** when each mob this sword hit can be hit again, by game time */
    private final Map<UUID, Long> nextHit = new HashMap<>();

    public OrbitingSword(EntityType<? extends OrbitingSword> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /**
     * Puts sword {@code index} of {@code count} into the world around {@code owner}.
     */
    public static OrbitingSword summon(ServerLevel level, Player owner, int index, int count, float radius, int duration, float damage, float hp) {
        OrbitingSword sword = new OrbitingSword(MagicTreasuresEntities.ORBITING_SWORD.get(), level);
        sword.owner = owner;
        sword.expireTime = level.getGameTime() + duration;
        sword.damage = damage;
        sword.hp = hp;
        sword.entityData.set(OWNER_ID, owner.getId());
        sword.entityData.set(INDEX, index);
        sword.entityData.set(COUNT, count);
        sword.entityData.set(RADIUS, radius);
        Vec3 pos = sword.orbitPosition(owner.position(), level.getGameTime());
        sword.moveTo(pos.x, pos.y, pos.z, 0F, 0F);
        level.addFreshEntity(sword);
        return sword;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ID, -1);
        builder.define(INDEX, 0);
        builder.define(COUNT, 1);
        builder.define(RADIUS, 1.6F);
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (!level().isClientSide()) {
            if (owner == null || !owner.isAlive() || owner.isRemoved() || owner.level() != level()) {
                discard();
                return;
            }
            if (level().getGameTime() >= expireTime) {
                SpellEffects.burst(level(), position(), MagicTreasuresParticles.SPARK_CYAN.get(), 4, 0.15);
                discard();
                return;
            }
        }
        if (owner != null) {
            Vec3 pos = orbitPosition(owner.position(), level().getGameTime());
            setPos(pos.x, pos.y, pos.z);
        }
        if (level() instanceof ServerLevel serverLevel && owner instanceof Player player) {
            hitMobs(serverLevel, player);
        } else if (level().isClientSide() && tickCount % 3 == 0) {
            // a faint trail
            level().addParticle(MagicTreasuresParticles.MOTE_CYAN.get(), getX(), getY(), getZ(), 0.0, 0.0, 0.0);
        }
    }

    private void hitMobs(ServerLevel level, Player player) {
        long now = level.getGameTime();
        for (Monster mob : level.getEntitiesOfClass(Monster.class, getBoundingBox().inflate(HIT_REACH), Monster::isAlive)) {
            if (nextHit.getOrDefault(mob.getUUID(), 0L) > now) {
                continue;
            }
            nextHit.put(mob.getUUID(), now + HIT_COOLDOWN);
            // false while the mob is in its post-hit invulnerability (another sword just hit it): no hp spent
            if (!mob.hurt(level.damageSources().indirectMagic(this, player), damage)) {
                continue;
            }
            SpellEffects.burst(level, mob, MagicTreasuresParticles.SPARK_CYAN.get(), 4);
            level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.6F, 1.4F);
            hp -= damage;
            if (hp <= 0) {
                shatter(level);
                return;
            }
        }
    }

    /** spent: breaks apart like glass */
    private void shatter(ServerLevel level) {
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(MagicTreasuresItems.SPECTRAL_SWORD.get())),
                getX(), getY(), getZ(), 12, 0.2, 0.2, 0.2, 0.05);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.8F, 1.3F);
        discard();
    }

    /** the orbit's angle, in radians, at {@code time} (game ticks, partial ticks allowed) */
    public double angle(double time) {
        int count = Math.max(1, entityData.get(COUNT));
        return Math.PI * 2 * (entityData.get(INDEX) / (double) count + (time % ORBIT_PERIOD) / ORBIT_PERIOD);
    }

    /** where this sword is at {@code time} when its owner stands at {@code ownerPos} */
    public Vec3 orbitPosition(Vec3 ownerPos, double time) {
        double angle = angle(time);
        double radius = entityData.get(RADIUS);
        return ownerPos.add(Math.cos(angle) * radius, ORBIT_HEIGHT, Math.sin(angle) * radius);
    }

    /** the owner: the cached player on the server, looked up by the synced id on the client */
    public Entity getOwner() {
        if (owner != null) {
            return owner;
        }
        int id = entityData.get(OWNER_ID);
        return id < 0 ? null : level().getEntity(id);
    }

    public float getDamage() {
        return damage;
    }

    public float getHp() {
        return hp;
    }

    /** position packets are ignored: the client keeps the orbit itself */
    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // never saved
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // never saved
    }
}
