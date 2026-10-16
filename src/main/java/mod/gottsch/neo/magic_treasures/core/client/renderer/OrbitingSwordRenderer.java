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
package mod.gottsch.neo.magic_treasures.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.gottsch.neo.magic_treasures.core.entity.OrbitingSword;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Draws a Ring of Swords sword: the spectral sword item, its tip pointing the way it circles, the blade tilted
 * (rolled about its length, tip dipped) so it reads from any angle instead of lying flat.
 * <p>
 * The position comes from the owner's interpolated position and the partial tick, not from the entity's own
 * last-tick lerp, so the ring stays smooth and centred while the owner moves. Drawn full bright: it's a spell.
 *
 * @author Mark Gottschling on 10/16/2026
 */
public class OrbitingSwordRenderer extends EntityRenderer<OrbitingSword> {
    private static final float SCALE = 0.9F;
    /** degrees the blade is turned about its length (0 = flat, 90 = on edge) */
    private static final float ROLL = 45F;
    /** degrees the tip dips below level */
    private static final float PITCH = 15F;

    private final ItemRenderer itemRenderer;
    private ItemStack stack;

    public OrbitingSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0F;
    }

    @Override
    public void render(OrbitingSword sword, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Entity owner = sword.getOwner();
        if (owner == null) {
            return;
        }
        if (stack == null) {
            stack = new ItemStack(MagicTreasuresItems.SPECTRAL_SWORD.get());
        }
        double time = sword.level().getGameTime() + partialTick;
        Vec3 target = sword.orbitPosition(owner.getPosition(partialTick), time);
        // the pose stack starts at the entity's own lerped position: move from there to where the orbit says
        Vec3 here = sword.getPosition(partialTick);

        poseStack.pushPose();
        poseStack.translate(target.x - here.x, target.y - here.y, target.z - here.z);
        // the orbit runs counter-clockwise seen from above (angle grows from +X toward +Z), so the way it moves is
        // (-sin a, 0, cos a); turning +Z by -a about Y points it there
        poseStack.mulPose(Axis.YP.rotation((float) -sword.angle(time)));
        // tip dipped a little, as if cutting (a positive turn about X takes +Z down)
        poseStack.mulPose(Axis.XP.rotationDegrees(PITCH));
        // blade rolled about its length, so it never lies flat: its face shows from above and from the side
        poseStack.mulPose(Axis.ZP.rotationDegrees(ROLL));
        // lay the sprite flat: its up (+Y) becomes +Z, the way it moves
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        // the sprite's tip is at its top right: turn it to point straight up first
        poseStack.mulPose(Axis.ZP.rotationDegrees(45));
        poseStack.scale(SCALE, SCALE, SCALE);
        itemRenderer.renderStatic(stack, ItemDisplayContext.NONE, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, sword.level(), sword.getId());
        poseStack.popPose();
        super.render(sword, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(OrbitingSword sword) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
