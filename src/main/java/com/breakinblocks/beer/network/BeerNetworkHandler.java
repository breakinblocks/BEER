package com.breakinblocks.beer.network;

import com.breakinblocks.beer.Beer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class BeerNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Beer.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SyncEnchantingDataPacket.class, SyncEnchantingDataPacket::encode, SyncEnchantingDataPacket::decode, SyncEnchantingDataPacket::handle);
        CHANNEL.registerMessage(id++, ApplyItemModifierPacket.class, ApplyItemModifierPacket::encode, ApplyItemModifierPacket::decode, ApplyItemModifierPacket::handle);
        CHANNEL.registerMessage(id++, RequestEnchantingDataPacket.class, RequestEnchantingDataPacket::encode, RequestEnchantingDataPacket::decode, RequestEnchantingDataPacket::handle);
    }
    
    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }
    
    public static void sendToPlayersNear(Object msg, net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos, double radius) {
        CHANNEL.send(net.minecraftforge.network.PacketDistributor.NEAR.with(() -> new net.minecraftforge.network.PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), radius, level.dimension())), msg);
    }

    public static void sendToPlayer(Object msg, net.minecraft.server.level.ServerPlayer player) {
        CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
