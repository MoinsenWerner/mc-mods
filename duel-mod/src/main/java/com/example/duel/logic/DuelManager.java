package com.example.duel.logic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class DuelManager {
    private static final Map<ServerPlayer, ServerPlayer> pending = new HashMap<>();
    private static final Map<ServerPlayer, ServerPlayer> active = new HashMap<>();

    public static void requestDuel(ServerPlayer requester, String targetName) {
        ServerPlayer target = requester.server.getPlayerList().getPlayerByName(targetName);
        if (target == null || !target.position().closerThan(requester.position(), 10)) {
            requester.sendSystemMessage(Component.literal("Spieler nicht in Reichweite."));
            return;
        }
        pending.put(target, requester);
        target.sendSystemMessage(Component.literal(requester.getName().getString() + " möchte sich mit dir duellieren. /accept um anzunehmen, /deny um abzulehnen."));
    }

    public static void acceptDuel(ServerPlayer target) {
        ServerPlayer requester = pending.remove(target);
        if (requester == null) {
            target.sendSystemMessage(Component.literal("Keine offene Duellanfrage."));
            return;
        }
        active.put(requester, target);
        active.put(target, requester);
        // TODO disable extra hearts here
    }

    public static void denyDuel(ServerPlayer target) {
        ServerPlayer requester = pending.remove(target);
        if (requester != null) {
            requester.sendSystemMessage(Component.literal("Die Duellanfrage wurde abgelehnt."));
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerPlayer opponent = active.remove(player);
        if (opponent != null) {
            active.remove(opponent);
            // TODO reset hearts here
            opponent.sendSystemMessage(Component.literal("Duell beendet."));
        }
    }
}
