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
 * Since 2.0 only the 1.x legacy items carry this: it keeps a 1.x stack's saved jewelry state (uses, mana, stone,
 * spells...) as it was, so {@code LegacyConversion} can read it. 1.x kept that state in this item capability, saved
 * under the stack's {@code ForgeCaps.Parent}, which is only loaded while an item provides a capability. Nothing here
 * interprets the data; it is handed back unchanged on save, so an unconverted stack loses nothing.
 * <p>
 * (2.0 jewelry keeps its state on the stack's NBT, see {@code StackData}.)
 */
public class JewelryCapability implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation ID = new ResourceLocation(MagicTreasures.MOD_ID, "jewelry");

    private CompoundTag data = new CompoundTag();
    private final LazyOptional<JewelryCapability> optional = LazyOptional.of(() -> this);

    /** the 1.x handler's saved state; empty for a stack that never had one */
    public CompoundTag data() {
        return data;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == MagicTreasuresCapabilities.LEGACY_JEWELRY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return data.copy();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        data = tag.copy();
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(JewelryCapability.class);
    }
}
