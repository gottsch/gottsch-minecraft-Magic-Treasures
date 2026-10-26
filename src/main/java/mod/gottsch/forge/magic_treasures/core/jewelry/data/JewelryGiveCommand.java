package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.component.JewelryIdentity;
import mod.gottsch.forge.magic_treasures.core.item.GenericJewelry;
import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresItems;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Gives generic jewelry, because the component form of /give is too long to type:
 * <pre>
 * /magictreasures give &lt;players&gt; &lt;type&gt; &lt;material&gt; [&lt;gem&gt;|none] [&lt;style&gt;]
 * /magictreasures give &lt;players&gt; preset &lt;preset&gt;
 * </pre>
 * Ids complete from the loaded registries. A combination the rules don't allow is still given (it is a command for
 * testing), with a warning. Operators only.
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JewelryGiveCommand {
    /** "none" as an id: no gem (needed to name a style for a gemless piece) */
    private static final ResourceLocation NO_GEM = new ResourceLocation("none");

    private JewelryGiveCommand() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(MagicTreasures.MOD_ID)
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("give")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.literal("preset")
                                        .then(Commands.argument("preset", ResourceLocationArgument.id())
                                                .suggests(ids(JewelryDataIndex::presets))
                                                .executes(JewelryGiveCommand::givePreset)))
                                .then(combination()))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> combination() {
        return Commands.argument("type", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(types(), builder))
                .then(Commands.argument("material", ResourceLocationArgument.id())
                        .suggests(ids(JewelryDataIndex::materials))
                        .executes(context -> give(context, false, false))
                        .then(Commands.argument("gem", ResourceLocationArgument.id())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(Stream.concat(
                                        Stream.of(NO_GEM), JewelryDataIndex.gemstones().keySet().stream()), builder))
                                .executes(context -> give(context, true, false))
                                .then(Commands.argument("style", ResourceLocationArgument.id())
                                        .suggests(ids(JewelryDataIndex::styles))
                                        .executes(context -> give(context, true, true)))));
    }

    private static int give(CommandContext<CommandSourceStack> context, boolean hasGem, boolean hasStyle) throws CommandSyntaxException {
        String type = StringArgumentType.getString(context, "type");
        Optional<GenericJewelry> item = item(type);
        if (item.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Unknown jewelry type: " + type));
            return 0;
        }
        ResourceLocation materialId = ResourceLocationArgument.getId(context, "material");
        Optional<Holder.Reference<JewelryMaterialDef>> material = JewelryDataIndex.material(materialId);
        if (material.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Unknown or disabled material: " + materialId));
            return 0;
        }
        Optional<Holder.Reference<GemstoneDef>> gem = Optional.empty();
        if (hasGem) {
            ResourceLocation gemId = ResourceLocationArgument.getId(context, "gem");
            if (!gemId.equals(NO_GEM)) {
                gem = JewelryDataIndex.gemstone(gemId);
                if (gem.isEmpty()) {
                    context.getSource().sendFailure(Component.literal("Unknown or disabled gemstone: " + gemId));
                    return 0;
                }
            }
        }
        ResourceLocation styleId = hasStyle ? ResourceLocationArgument.getId(context, "style") : JewelryRules.REGULAR;
        Optional<Holder.Reference<JewelryStyleDef>> style = JewelryDataIndex.style(styleId);
        if (style.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Unknown or disabled style: " + styleId));
            return 0;
        }
        if (!JewelryRules.allows(type, material.get(), style.get(), gem.map(g -> g))) {
            context.getSource().sendSuccess(() -> Component.literal("Note: the rules don't allow this combination; "
                    + "crafting, loot and the creative tab won't make it").withStyle(ChatFormatting.GOLD), false);
        }
        JewelryIdentity identity = new JewelryIdentity(type, materialId, gem.map(g -> g.key().location()), styleId, Optional.empty());
        return give(context, item.get().stack(identity));
    }

    private static int givePreset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocationArgument.getId(context, "preset");
        Optional<Holder.Reference<JewelryPresetDef>> preset = JewelryDataIndex.preset(id);
        if (preset.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Unknown or disabled preset: " + id));
            return 0;
        }
        JewelryPresetDef def = preset.get().value();
        Optional<GenericJewelry> item = item(def.type());
        if (item.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Preset " + id + " has an unknown type: " + def.type()));
            return 0;
        }
        return give(context, item.get().stack(new JewelryIdentity(def.type(), def.material(), def.gem(), def.style(), Optional.of(id))));
    }

    private static int give(CommandContext<CommandSourceStack> context, ItemStack template) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
        for (ServerPlayer player : players) {
            ItemStack stack = template.copy();
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
        Component name = template.getHoverName();
        context.getSource().sendSuccess(() -> Component.literal("Gave ").append(name)
                .append(" to " + players.size() + " player(s)"), true);
        return players.size();
    }

    private static Stream<String> types() {
        return MagicTreasuresItems.GENERIC_JEWELRY.stream().map(item -> ((GenericJewelry) item.get()).getJewelryType());
    }

    private static Optional<GenericJewelry> item(String type) {
        return GenericJewelry.forType(type);
    }

    private static SuggestionProvider<CommandSourceStack> ids(java.util.function.Supplier<? extends Map<ResourceLocation, ?>> entries) {
        return (context, builder) -> SharedSuggestionProvider.suggestResource(entries.get().keySet(), builder);
    }
}
