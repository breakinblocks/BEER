package com.breakinblocks.beer.network;

import com.breakinblocks.beer.Beer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Beer.MODID)
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Beer.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.playToServer(
            UpdateRangePacket.TYPE,
            UpdateRangePacket.STREAM_CODEC,
            UpdateRangePacket::handle
        );

        registrar.playToServer(
            RequestEnchantingDataPacket.TYPE,
            RequestEnchantingDataPacket.STREAM_CODEC,
            RequestEnchantingDataPacket::handle
        );

        registrar.playToClient(
            ApplyItemModifierPacket.TYPE,
            ApplyItemModifierPacket.STREAM_CODEC,
            ApplyItemModifierPacket::handle
        );

        registrar.playToClient(
            SyncEnchantingDataPacket.TYPE,
            SyncEnchantingDataPacket.STREAM_CODEC,
            SyncEnchantingDataPacket::handle
        );
    }

    public static void sendToServer(CustomPacketPayload packet) {
        ClientPacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayer(CustomPacketPayload packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToAllPlayers(CustomPacketPayload packet) {
        PacketDistributor.sendToAllPlayers(packet);
    }

    public static void sendToPlayersNear(CustomPacketPayload packet, ServerLevel level, BlockPos pos, double range) {
        PacketDistributor.sendToPlayersNear(level, null, pos.getX(), pos.getY(), pos.getZ(), range, packet);
    }
}
