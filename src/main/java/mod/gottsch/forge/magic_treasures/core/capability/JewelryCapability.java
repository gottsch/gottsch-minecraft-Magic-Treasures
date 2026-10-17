/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.magic_treasures.core.capability;

import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelrySizeTier;
import mod.gottsch.forge.magic_treasures.core.item.JewelryType;
import mod.gottsch.forge.magic_treasures.core.jewelry.JewelryMaterials;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Mark Gottschling on 6/1/2023
 */
public class JewelryCapability implements ICapabilitySerializable<CompoundTag> {
        public static final ResourceLocation ID = new ResourceLocation(MagicTreasures.MOD_ID, "jewelry");

    // reference of handler/data for easy access
    private final IJewelryHandler handler;// = new JewelryHandler();
    // holder of the handler/data
    private final LazyOptional<IJewelryHandler> optional;// = LazyOptional.of(() -> handler);

    public JewelryCapability() {
        this(new JewelryHandler.Builder(JewelryType.UNKNOWN, JewelryMaterials.NONE, JewelrySizeTier.UNKNOWN).build());
    }

    public JewelryCapability(IJewelryHandler handler) {
        this.handler = handler;
        optional = LazyOptional.of(() -> handler);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == MagicTreasuresCapabilities.JEWELRY_CAPABILITY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return (CompoundTag)handler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        handler.deserializeNBT(tag);
    }

    /**
     *
     * @param event
     */
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IJewelryHandler.class);
    }
}
