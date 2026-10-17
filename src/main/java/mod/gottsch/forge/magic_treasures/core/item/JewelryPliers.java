/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2024 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.magic_treasures.core.item;

import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author Mark Gottschling on May 6, 2024
 *
 */
public class JewelryPliers extends Item {

	public JewelryPliers(Properties properties) {
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);	
		tooltip.add(Component.translatable(LangUtil.tooltip("jewelry_pliers")));
	}

	/**
	 * Required to prevent item consumpution in recipe
	 */
	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	/**
	 * Required to prevent item consumpution in recipe
	 */
	@Override
	public boolean hasCraftingRemainingItem(ItemStack itemStack) {
		if (!hasCraftingRemainingItem()) {
			return false;
		}
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
		return itemStack.copy();
	}

}
