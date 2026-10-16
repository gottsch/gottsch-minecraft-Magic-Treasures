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
package mod.gottsch.neo.magic_treasures.core.client;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import mod.gottsch.neo.magic_treasures.core.client.hud.JewelryHud;
import mod.gottsch.neo.magic_treasures.core.client.model.JewelryColors;
import mod.gottsch.neo.magic_treasures.core.client.model.JewelryModel;
import mod.gottsch.neo.magic_treasures.core.client.model.LichsMaskModel;
import mod.gottsch.neo.magic_treasures.core.client.model.LoreBookModel;
import mod.gottsch.neo.magic_treasures.core.client.model.SmithsHelmModel;
import mod.gottsch.neo.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.neo.magic_treasures.core.item.set.SetShield;
import mod.gottsch.neo.magic_treasures.core.setup.Registration;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import mod.gottsch.neo.magic_treasures.core.client.particle.ArcaneSparkParticle;
import mod.gottsch.neo.magic_treasures.core.client.tooltip.ClientIconTitleTooltip;
import mod.gottsch.neo.magic_treasures.core.client.tooltip.RichTooltips;
import mod.gottsch.neo.magic_treasures.core.client.tooltip.IconTitleTooltip;
import mod.gottsch.neo.magic_treasures.core.particle.MagicTreasuresParticles;
import mod.gottsch.neo.magic_treasures.core.set.SetEquipment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import mod.gottsch.neo.magic_treasures.core.client.renderer.OrbitingSwordRenderer;
import mod.gottsch.neo.magic_treasures.core.entity.MagicTreasuresEntities;

/**
 * Client-only mod-bus registrations.
 *
 * @author Mark Gottschling on 10/9/2026
 */
@EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MagicTreasuresClientSetup {

	/**
	 * Vanilla registers the {@code blocking} item property for its own shield only, so a set shield's
	 * {@code blocking} model override would never fire without this (Dungeons2's Mirror Shield hit the same).
	 */
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> Registration.ITEMS.getEntries().stream()
				.filter(item -> item.get() instanceof SetShield)
				.forEach(item -> ItemProperties.register(item.get(), ResourceLocation.withDefaultNamespace("blocking"),
						(stack, level, entity, seed) ->
								entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F)));
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MagicTreasuresEntities.ORBITING_SWORD.get(), OrbitingSwordRenderer::new);
	}

	/** models of set armor that isn't the plain armor box */
	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(SmithsHelmModel.LAYER, SmithsHelmModel::createBodyLayer);
		event.registerLayerDefinition(LichsMaskModel.LAYER, LichsMaskModel::createBodyLayer);
	}

	/** set helmets drawn with their own model instead of vanilla's helmet box: the Smith's Helm (horns, raised top), the Lich's Mask (a closer fit) */
	@SubscribeEvent
	public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new IClientItemExtensions() {
			private SmithsHelmModel model;

			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
					HumanoidModel<?> original) {
				if (model == null) {
					model = new SmithsHelmModel(Minecraft.getInstance().getEntityModels().bakeLayer(SmithsHelmModel.LAYER));
				}
				return model;
			}
		}, MagicTreasuresItems.SMITHS_HELM.get());
		event.registerItem(new IClientItemExtensions() {
			private LichsMaskModel model;

			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
					HumanoidModel<?> original) {
				if (model == null) {
					model = new LichsMaskModel(Minecraft.getInstance().getEntityModels().bakeLayer(LichsMaskModel.LAYER));
				}
				return model;
			}
		}, MagicTreasuresItems.LICHS_MASK.get());
	}

	@SubscribeEvent
	public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
		MagicTreasuresParticles.sparks().forEach(spark -> event.registerSpriteSet(spark.get(), ArcaneSparkParticle.Provider::new));
		event.registerSpriteSet(MagicTreasuresParticles.MOTE_CYAN.get(), sprites -> new ArcaneSparkParticle.Provider(sprites, 0.35F));
	}

	@SubscribeEvent
	public static void onRegisterTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(IconTitleTooltip.class, ClientIconTitleTooltip::new);
		RichTooltips.registerFactories(event);
		// spell chips show the set bonuses of the player wearing the jewelry
		SetEquipment.setTooltipPlayer(() -> Minecraft.getInstance().player);
	}

	@SubscribeEvent
	public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(JewelryHud.ID, new JewelryHud());
	}

	/** the composed model of 2.0 generic jewelry */
	@SubscribeEvent
	public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
		event.register(JewelryModel.LOADER_ID, JewelryModel.Loader.INSTANCE);
		event.register(LoreBookModel.LOADER_ID, LoreBookModel.Loader.INSTANCE);
	}

	/** tints the composed model's fallback layers */
	@SubscribeEvent
	public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
		MagicTreasuresItems.GENERIC_JEWELRY.forEach(item -> event.register(new JewelryColors(), item.get()));
	}
}
