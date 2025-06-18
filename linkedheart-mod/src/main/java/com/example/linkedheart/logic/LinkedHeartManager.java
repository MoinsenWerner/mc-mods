package com.example.linkedheart.logic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber
public class LinkedHeartManager {
    private static final Map<ServerPlayer, ServerPlayer> links = new HashMap<>();
    private static final Random random = new Random();

    public static void onLoseExtraHearts(ServerPlayer player) {
        if (links.containsKey(player)) return;
        ServerPlayer[] players = player.server.getPlayerList().getPlayers().toArray(new ServerPlayer[0]);
        if (players.length == 0) return;
        ServerPlayer partner = players[random.nextInt(players.length)];
        links.put(player, partner);
        player.sendSystemMessage(Component.literal("Linked Heart mit " + partner.getName().getString()));
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerPlayer partner = links.remove(player);
        if (partner != null) {
            links.remove(partner);
            partner.sendSystemMessage(Component.literal("Dein Linked Heart wurde durch den Tod von " + player.getName().getString() + " zerstört."));
        }
    }
}
