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
package mod.gottsch.forge.magic_treasures.core.client.model;

import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

/**
 * The set helmets drawn with their own model instead of vanilla's helmet box: the Smith's Helm (horns, raised top)
 * and the Lich's Mask (a closer fit). {@code SetArmor.initializeClient} asks here; on 1.21.1 the same models are
 * registered with {@code RegisterClientExtensionsEvent}, which Forge 1.20.1 doesn't have. Models are baked on first
 * use, after the layers are registered.
 *
 * @author Mark Gottschling on 10/14/2026
 */
public final class SetArmorModels {
    private static SmithsHelmModel smithsHelm;
    private static LichsMaskModel lichsMask;

    private SetArmorModels() {}

    /** the worn model for this set armor item, or null for vanilla's */
    @Nullable
    public static HumanoidModel<?> get(Item item) {
        if (item == MagicTreasuresItems.SMITHS_HELM.get()) {
            if (smithsHelm == null) {
                smithsHelm = new SmithsHelmModel(Minecraft.getInstance().getEntityModels().bakeLayer(SmithsHelmModel.LAYER));
            }
            return smithsHelm;
        }
        if (item == MagicTreasuresItems.LICHS_MASK.get()) {
            if (lichsMask == null) {
                lichsMask = new LichsMaskModel(Minecraft.getInstance().getEntityModels().bakeLayer(LichsMaskModel.LAYER));
            }
            return lichsMask;
        }
        return null;
    }
}
