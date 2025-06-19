package com.example.duel.logic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class DuelManager {
    private static final Map<ServerPlayer, ServerPlayer> pending = new HashMap<>();
    private static final Map<ServerPlayer, ServerPlayer> active = new HashMap<>();
    private static final Map<ServerPlayer, Double> oldMaxHealth = new HashMap<>();

    public static void requestDuel(ServerPlayer requester, String targetName) {
        ServerPlayer target = requester.server.getPlayerList().getPlayerByName(targetName);
        if (target == null || !target.position().closerThan(requester.position(), 10)) {
            requester.sendSystemMessage(Component.literal("Spieler nicht in Reichweite."));
            return;
        }
        pending.put(target, requester);
        target.sendSystemMessage(Component.literal(requester.getName().getString() +
                " möchte sich mit dir duellieren. /accept um anzunehmen, /deny um abzulehnen."));
    }

    public static void acceptDuel(ServerPlayer target) {
        ServerPlayer requester = pending.remove(target);
        if (requester == null) {
            target.sendSystemMessage(Component.literal("Keine offene Duellanfrage."));
            return;
        }
        active.put(requester, target);
        active.put(target, requester);
        disableExtraHearts(requester);
        disableExtraHearts(target);
    }

    public static void denyDuel(ServerPlayer target) {
        ServerPlayer requester = pending.remove(target);
        if (requester != null) {
            requester.sendSystemMessage(Component.literal("Die Duellanfrage wurde abgelehnt."));
        }
    }

    private static void disableExtraHearts(ServerPlayer player) {
        double currentMax = player.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
        if (!oldMaxHealth.containsKey(player)) {
            oldMaxHealth.put(player, currentMax);
        }
        if (currentMax > 20.0D) {
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
        }
        if (player.getHealth() > 20.0F) {
            player.setHealth(20.0F);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerPlayer opponent = active.remove(player);
        if (opponent != null) {
            active.remove(opponent);
            resetHearts(player);
            resetHearts(opponent);
            opponent.sendSystemMessage(Component.literal("Duell beendet."));
        }
    }

    private static void resetHearts(ServerPlayer player) {
        Double prev = oldMaxHealth.remove(player);
        if (prev != null) {
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(prev);
            if (player.getHealth() > prev.floatValue()) {
                player.setHealth(prev.floatValue());
            }
        }
    }
}

