package com.breakinblocks.beer.network;

import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.breakinblocks.beer.Beer.rl;

public record UpdateRangePacket(BlockPos pos, int rangeX, int rangeY, int rangeZ) implements CustomPacketPayload {
    
    private static final double MAX_INTERACTION_DISTANCE_SQ = 64.0;
    private static final double BLOCK_CENTER_OFFSET = 0.5;
    
    public static final CustomPacketPayload.Type<UpdateRangePacket> TYPE = 
        new CustomPacketPayload.Type<>(rl("update_range"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRangePacket> STREAM_CODEC = 
        StreamCodec.composite(
            BlockPos.STREAM_CODEC, UpdateRangePacket::pos,
            ByteBufCodecs.INT, UpdateRangePacket::rangeX,
            ByteBufCodecs.INT, UpdateRangePacket::rangeY,
            ByteBufCodecs.INT, UpdateRangePacket::rangeZ,
            UpdateRangePacket::new
        );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateRangePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!context.flow().isServerbound()) {
                return;
            }
            
            var player = context.player();
            if (!isValidContext(player)) {
                return;
            }
            
            if (!EnchantingTableDataUtil.hasEnchantingTable(player.level(), packet.pos())) {
                return;
            }
            
            if (!isPlayerInRange(player, packet.pos())) {
                return;
            }
            
            updateRangeData(packet, player);
        });
    }
    
    private static boolean isValidContext(Player player) {
        return player != null;
    }
    
    private static boolean isPlayerInRange(Player player, BlockPos pos) {
        double distance = player.distanceToSqr(
            pos.getX() + BLOCK_CENTER_OFFSET,
            pos.getY() + BLOCK_CENTER_OFFSET,
            pos.getZ() + BLOCK_CENTER_OFFSET
        );
        return distance <= MAX_INTERACTION_DISTANCE_SQ;
    }
    
    private static void updateRangeData(UpdateRangePacket packet, Player player) {
        EnchantingTableDataUtil.setRanges(player.level(), packet.pos(), packet.rangeX(), packet.rangeY(), packet.rangeZ());
        
        if (player.level() instanceof ServerLevel serverLevel) {
            var updatedData = EnchantingTableDataUtil.getRangeData(player.level(), packet.pos());
            SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(packet.pos(), updatedData);
            NetworkHandler.sendToPlayersNear(syncPacket, serverLevel, packet.pos(), MAX_INTERACTION_DISTANCE_SQ);
        }
    }
}