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
package mod.gottsch.neo.magic_treasures.core.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * A short-lived arcane spark: a bright, flickering star that fades out fast. Rendered
 * <em>additively</em> and at full brightness, so it glows like light and overlapping sparks (as
 * along a spell arc) read as a jagged bolt rather than a row of dim dots. Animates through its
 * {@code magictreasures:spark_<color>} frames over its brief life for the flicker.
 * <p>
 * Copied from gottsch's Monster Manual's {@code ElectricSparkParticle}. One class serves every
 * {@code spark_<color>} type in {@code MagicTreasuresParticles}; only the textures differ.
 *
 * @author Mark Gottschling on 7/4/2026
 */
@OnlyIn(Dist.CLIENT)
public class ArcaneSparkParticle extends TextureSheetParticle {
	private final SpriteSet sprites;

	protected ArcaneSparkParticle(ClientLevel level, double x, double y, double z,
									double dx, double dy, double dz, SpriteSet sprites) {
		super(level, x, y, z, 0.0D, 0.0D, 0.0D);
		this.sprites = sprites;
		this.gravity = 0.0F;
		this.friction = 0.6F;
		this.hasPhysics = false;                 // sparks arc through the air, they don't collide
		// long enough to actually register the bolt (a too-short flash was invisible mid-swing),
		// still snappy enough to read as lightning rather than a lingering mote
		this.lifetime = 10 + this.random.nextInt(8);  // 10–17 ticks (~0.5–0.85s)
		this.quadSize = 0.22F + this.random.nextFloat() * 0.10F;  // ~70% of GMM's spark: spell sparks sit close to the player
		// a tiny crackle of motion around the spawn point (plus whatever tiny seed velocity was passed)
		this.xd = dx + (this.random.nextDouble() - 0.5D) * 0.02D;
		this.yd = dy + (this.random.nextDouble() - 0.5D) * 0.02D;
		this.zd = dz + (this.random.nextDouble() - 0.5D) * 0.02D;
		this.setSpriteFromAge(sprites);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.removed) {
			this.setSpriteFromAge(this.sprites);         // advance the flicker frame
			// hold near full brightness through most of its life, then snap-fade in the last
			// 30% — a linear fade from spawn made the spark look dim/washed-out immediately,
			// which was part of why the additive-blended bolt wasn't reading as electricity
			float lifeFraction = (float) this.age / this.lifetime;
			this.alpha = lifeFraction < 0.7F ? 1.0F : 1.0F - ((lifeFraction - 0.7F) / 0.3F);
		}
	}

	/** Full-bright: a spark is its own light source. */
	@Override
	public int getLightColor(float partialTick) {
		return 0xF000F0;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ADDITIVE_GLOW;
	}

	/** Additive, full-bright particle pass (like the translucent sheet but glow-blended, no depth write). */
	private static final ParticleRenderType ADDITIVE_GLOW = new ParticleRenderType() {
		@Override
		public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			RenderSystem.setShader(GameRenderer::getParticleShader);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
			return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}

		// 1.21 has no end() hook: ParticleEngine draws the batch, then restores depthMask/blend itself.

		@Override
		public String toString() {
			return "MAGICTREASURES_SPARK_GLOW";
		}
	};

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level,
									   double x, double y, double z, double dx, double dy, double dz) {
			return new ArcaneSparkParticle(level, x, y, z, dx, dy, dz, this.sprites);
		}
	}
}
