package com.example.duel.command;

import com.example.duel.DuelMod;
import com.example.duel.logic.DuelManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DuelCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("duell")
            .then(Commands.argument("target", StringArgumentType.word())
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    String targetName = StringArgumentType.getString(ctx, "target");
                    DuelManager.requestDuel(player, targetName);
                    return 1;
                })));

        dispatcher.register(Commands.literal("accept").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            DuelManager.acceptDuel(player);
            return 1;
        }));

        dispatcher.register(Commands.literal("deny").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            DuelManager.denyDuel(player);
            return 1;
        }));
    }
}
