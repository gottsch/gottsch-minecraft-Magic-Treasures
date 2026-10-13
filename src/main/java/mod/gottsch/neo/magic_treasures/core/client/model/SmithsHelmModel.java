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
package mod.gottsch.neo.magic_treasures.core.client.model;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * The Smith's Helm as worn: a helmet with a raised top and two horns, from Mark's Blockbench model
 * ({@code blockbench\magic treasures\salandaars_helm.bbmodel}, exported as a "Modded Entity" Java model).
 * <p>
 * Armor rendering expects a {@link HumanoidModel}: it copies the wearer's pose onto it and shows only the parts for
 * the slot (head and hat for a helmet). So the mesh has every humanoid part, all empty, and the helm's boxes hang
 * under {@code head}, which turns with the player's head. The texture is the material's worn layer,
 * {@code textures/models/armor/smiths_steel_layer_1.png}, 64x64 to match this model.
 */
public class SmithsHelmModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MagicTreasures.MOD_ID, "smiths_helm"), "main");

    public SmithsHelmModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        // from the Blockbench export; its "bipedHead" is vanilla's head
        PartDefinition armorHead = head.addOrReplaceChild("armorHead", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.ZERO);

        armorHead.addOrReplaceChild("helmTop", CubeListBuilder.create().texOffs(29, 9)
                .addBox(-4.0F, 0.35F, 0.15F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(0.0F, -8.25F, -4.25F, 0.1745F, 0.0F, 0.0F));

        PartDefinition leftHorn = armorHead.addOrReplaceChild("leftHorn", CubeListBuilder.create().texOffs(16, 52)
                .addBox(5.9F, -10.2F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.1309F, 0.0F));
        leftHorn.addOrReplaceChild("long_r1", CubeListBuilder.create().texOffs(17, 47)
                .addBox(0.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, -5.5F, 0.0F, 0.0F, 0.0F, -0.4363F));

        PartDefinition rightHorn = armorHead.addOrReplaceChild("rightHorn", CubeListBuilder.create().texOffs(34, 30)
                .addBox(5.9F, -10.2F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(-0.05F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -3.0107F, 0.0F));
        rightHorn.addOrReplaceChild("long_r2", CubeListBuilder.create().texOffs(0, 46)
                .addBox(0.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, -5.5F, 0.0F, 0.0F, 0.0F, -0.4363F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
