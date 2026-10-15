package mod.gottsch.forge.magic_treasures.core.jewelry.data;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.capability.IJewelryHandler;
import mod.gottsch.forge.magic_treasures.core.capability.JewelryHandler;
import mod.gottsch.forge.magic_treasures.core.component.MagicTreasuresDataComponents;
import mod.gottsch.forge.magic_treasures.core.component.StackData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * {@code /magictreasures data}: writes every enabled jewelry data entry to the log, as JSON. (Until M9 it also compared
 * them with the 1.x constants and items; that check went with the 1.x code.)
 * {@code /magictreasures jewelry [mana <amount>]}: shows (and sets the mana of) the held jewelry through its handler.
 * Operators only.
 */
@Mod.EventBusSubscriber(modid = MagicTreasures.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JewelryDataCommand {

    private JewelryDataCommand() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(MagicTreasures.MOD_ID)
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("data").executes(JewelryDataCommand::run))
                .then(Commands.literal("jewelry").executes(JewelryDataCommand::showHeld)
                        .then(Commands.literal("mana")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                                        .executes(context -> setHeldMana(context, DoubleArgumentType.getDouble(context, "amount")))))));
    }

    /** {@code /magictreasures jewelry}: the held jewelry as its handler sees it (M2: computed stats + state) */
    private static int showHeld(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ItemStack stack = context.getSource().getPlayerOrException().getMainHandItem();
        Optional<IJewelryHandler> found = JewelryHandler.get(stack);
        if (found.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Not jewelry: " + stack.getHoverName().getString()));
            return 0;
        }
        IJewelryHandler handler = found.get();
        List<String> lines = new ArrayList<>();
        JewelryStats stats = handler.getStats();
        lines.add("identity: " + handler.getIdentity());
        lines.add("known: " + stats.known() + ", gem known: " + stats.gemKnown() + ", rarity: " + stats.rarity().getValue()
                + ", state stored: " + StackData.has(stack, MagicTreasuresDataComponents.JEWELRY_STATE));
        lines.add(String.format(Locale.ROOT, "uses %s/%d, mana %.1f/%.1f, level %d", handler.isInfinite() ? "inf" : handler.getUses(),
                handler.getMaxUses(), handler.getMana(), handler.getMaxMana(), handler.getMaxLevel()));
        lines.add(String.format(Locale.ROOT, "repairs %d/%d, recharges %d/%d, stone %s, spells %d", handler.getRepairs(),
                handler.getMaxRepairs(), handler.getRecharges(), handler.getMaxRecharges(), handler.getStone(), handler.getSpells().size()));
        lines.add(String.format(Locale.ROOT, "factors: cost %.4f, effect %.4f, duration %.4f, cooldown %.4f, range %.4f",
                handler.getSpellCostFactor(), handler.getSpellEffectAmountFactor(), handler.getSpellDurationFactor(),
                handler.getSpellCooldownFactor(), handler.getSpellRangeFactor()));
        lines.forEach(line -> context.getSource().sendSuccess(() -> Component.literal(line), false));
        return 1;
    }

    /** {@code /magictreasures jewelry mana <amount>}: writes mana through the handler, to check state round-trips */
    private static int setHeldMana(CommandContext<CommandSourceStack> context, double amount) throws CommandSyntaxException {
        ItemStack stack = context.getSource().getPlayerOrException().getMainHandItem();
        Optional<IJewelryHandler> found = JewelryHandler.get(stack);
        if (found.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Not jewelry"));
            return 0;
        }
        found.get().setMana(amount);
        return showHeld(context);
    }

    private static int run(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        RegistryOps<com.google.gson.JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, source.registryAccess());

        dump(ops, "jewelry_material", JewelryDataIndex.materials(), JewelryMaterialDef.CODEC);
        dump(ops, "jewelry_style", JewelryDataIndex.styles(), JewelryStyleDef.CODEC);
        dump(ops, "stone_tier", JewelryDataIndex.stoneTiers(), StoneTierDef.CODEC);
        dump(ops, "gemstone", JewelryDataIndex.gemstones(), GemstoneDef.CODEC);
        dump(ops, "jewelry_preset", JewelryDataIndex.presets(), JewelryPresetDef.CODEC);

        source.sendSuccess(() -> Component.literal(String.format("%d materials, %d styles, %d stone tiers, %d gemstones, %d presets (written to the log)",
                JewelryDataIndex.materials().size(), JewelryDataIndex.styles().size(), JewelryDataIndex.stoneTiers().size(),
                JewelryDataIndex.gemstones().size(), JewelryDataIndex.presets().size())), false);
        return 1;
    }

    private static <T> void dump(RegistryOps<com.google.gson.JsonElement> ops, String registry,
            Map<ResourceLocation, Holder.Reference<T>> entries, Codec<T> codec) {
        entries.forEach((id, holder) -> MagicTreasures.LOGGER.info("[data] {} {} = {}", registry, id,
                codec.encodeStart(ops, holder.value()).get().map(Object::toString, error -> "<" + error.message() + ">")));
    }
}
