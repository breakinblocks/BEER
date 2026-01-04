package com.breakinblocks.beer.network;

import com.breakinblocks.beer.capability.EnchantingTableRangeCapabilityProvider;
import com.breakinblocks.beer.capability.IEnchantingTableRangeData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

public class SyncEnchantingDataPacket {
    private final BlockPos pos;
    private final int modX, modY, modZ;

    public SyncEnchantingDataPacket(BlockPos pos, int modX, int modY, int modZ) {
        this.pos = pos;
        this.modX = modX;
        this.modY = modY;
        this.modZ = modZ;
    }
    
    public static SyncEnchantingDataPacket create(BlockPos pos, IEnchantingTableRangeData data) {
        return new SyncEnchantingDataPacket(pos, data.getItemModifiersX(), data.getItemModifiersY(), data.getItemModifiersZ());
    }

    public static void encode(SyncEnchantingDataPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.modX);
        buf.writeInt(msg.modY);
        buf.writeInt(msg.modZ);
    }

    public static SyncEnchantingDataPacket decode(FriendlyByteBuf buf) {
        return new SyncEnchantingDataPacket(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(SyncEnchantingDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
             @SuppressWarnings("resource")
             var level = Minecraft.getInstance().level;
             if (level != null && level.isLoaded(msg.pos)) {
                 BlockEntity be = level.getBlockEntity(msg.pos);
                 if (be != null) {
                     be.getCapability(EnchantingTableRangeCapabilityProvider.CAPABILITY).ifPresent(cap -> {
                         cap.setItemModifiersX(msg.modX);
                         cap.setItemModifiersY(msg.modY);
                         cap.setItemModifiersZ(msg.modZ);
                     });
                 }
             }
        });
        ctx.get().setPacketHandled(true);
    }
}
