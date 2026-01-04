package com.breakinblocks.beer.network;

import com.breakinblocks.beer.recipe.EnchantingModifierRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ApplyItemModifierPacket {
    private final BlockPos pos;
    private final EnchantingModifierRecipe.ModifierType type;
    private final boolean increase;
    private final boolean success;
    private final int rangeX, rangeY, rangeZ;

    public ApplyItemModifierPacket(BlockPos pos, EnchantingModifierRecipe.ModifierType type, boolean increase, boolean success, int x, int y, int z) {
        this.pos = pos;
        this.type = type;
        this.increase = increase;
        this.success = success;
        this.rangeX = x;
        this.rangeY = y;
        this.rangeZ = z;
    }

    public static void encode(ApplyItemModifierPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeEnum(msg.type);
        buf.writeBoolean(msg.increase);
        buf.writeBoolean(msg.success);
        buf.writeInt(msg.rangeX);
        buf.writeInt(msg.rangeY);
        buf.writeInt(msg.rangeZ);
    }

    public static ApplyItemModifierPacket decode(FriendlyByteBuf buf) {
        return new ApplyItemModifierPacket(buf.readBlockPos(), buf.readEnum(EnchantingModifierRecipe.ModifierType.class), buf.readBoolean(), buf.readBoolean(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(ApplyItemModifierPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Client side feedback could be added here
        });
        ctx.get().setPacketHandled(true);
    }
}
