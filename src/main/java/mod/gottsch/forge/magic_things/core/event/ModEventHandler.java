
package mod.gottsch.forge.magic_things.core.event;

import mod.gottsch.forge.magic_things.MagicThings;
import mod.gottsch.forge.magic_things.core.loot.MagicThingsLootFunctions;
import mod.gottsch.forge.magic_things.core.loot.modifier.LootModifierByLootTable;
import mod.gottsch.forge.magic_things.core.loot.modifier.LootModifierByRarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

/**
 * 
 * @author Mark Gottschling Jun 12, 2023
 *
 */
@Mod.EventBusSubscriber(modid = MagicThings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler {
	
	@SubscribeEvent
	public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		event.getRegistry().register(
				new LootModifierByRarity.Serializer().setRegistryName(
						new ResourceLocation(MagicThings.MOD_ID, "default"))
				);
		event.getRegistry().register(
				new LootModifierByLootTable.Serializer().setRegistryName(
						new ResourceLocation(MagicThings.MOD_ID, "loot_modifier_by_loot_table"))
		);

		// register loot functions - is this handled in the ModEventHandler?
		MagicThingsLootFunctions.register();

//		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(
//				GlobalLootModifierSerializer.class,
//				(RegistryEvent.Register event) -> {
//					TestLootItemFunctions.init();
//				}
//		);
	}
}
