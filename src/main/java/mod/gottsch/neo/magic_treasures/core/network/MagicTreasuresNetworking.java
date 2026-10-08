/**
 * 
 */
package mod.gottsch.neo.magic_treasures.core.network;

import mod.gottsch.neo.magic_treasures.MagicTreasures;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


import java.util.Optional;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;


/**
 * @author Mark Gottschling on May 2, 2024
 *
 */
public class MagicTreasuresNetworking {
	public static final String PROTOCOL_VERSION = "1.1";
	public static final int SPELL_MESSAGE_ID = 14;

	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MagicTreasures.MOD_ID, "magic_treasures_channel");

	public static SimpleChannel channel;    // used to transmit your network messages

	/**
	 * 
	 * @param event
	 */
	public static void common(final FMLCommonSetupEvent event) {
		// register the channel
		channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
				.networkProtocolVersion(() -> PROTOCOL_VERSION).clientAcceptedVersions(PROTOCOL_VERSION::equals)
				.serverAcceptedVersions(PROTOCOL_VERSION::equals).simpleChannel();

		// register the messages
		channel.registerMessage(SPELL_MESSAGE_ID, SpellUpdateS2C.class,
		           SpellUpdateS2C::encode, SpellUpdateS2C::decode,
		            SpellUpdateS2C::handle, Optional.of(PLAY_TO_CLIENT));

	}

}
