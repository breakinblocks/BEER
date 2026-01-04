package com.breakinblocks.beer.network;

import com.breakinblocks.beer.capability.IEnchantingTableRangeData;
import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestEnchantingDataPacket {
    private final BlockPos pos;

    public RequestEnchantingDataPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(RequestEnchantingDataPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static RequestEnchantingDataPacket decode(FriendlyByteBuf buf) {
        return new RequestEnchantingDataPacket(buf.readBlockPos());
    }

    public static void handle(RequestEnchantingDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                var level = player.serverLevel();
                var blockEntity = level.getBlockEntity(msg.pos);
                
                if (blockEntity instanceof EnchantmentTableBlockEntity) {
                    IEnchantingTableRangeData data = EnchantingTableDataUtil.getRangeData(level, msg.pos);
                    if (data != null) {
                        SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(msg.pos, data);
                        BeerNetworkHandler.sendToPlayer(syncPacket, player);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
    
    public static RequestEnchantingDataPacket create(BlockPos pos) {
        return new RequestEnchantingDataPacket(pos);
    }
}
