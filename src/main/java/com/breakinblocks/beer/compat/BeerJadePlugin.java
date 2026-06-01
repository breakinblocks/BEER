package com.breakinblocks.beer.compat;

import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import com.breakinblocks.beer.network.NetworkHandler;
import com.breakinblocks.beer.network.RequestEnchantingDataPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WailaPlugin
public class BeerJadePlugin implements IWailaPlugin, IBlockComponentProvider {

    private static final Set<BlockPos> pendingRequests = ConcurrentHashMap.newKeySet();

    @Override
    public void registerClient(IWailaClientRegistration reg) {
        reg.registerBlockComponent(this, Block.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.ENCHANTING_TABLE) {
            try {
                var data = EnchantingTableDataUtil.getRangeData(accessor.getLevel(), accessor.getPosition());
                
                if (accessor.getLevel().isClientSide()) {
                    var blockEntity = accessor.getLevel().getBlockEntity(accessor.getPosition());
                    if (blockEntity != null && !data.hasItemModifications() && !pendingRequests.contains(accessor.getPosition())) {
                        pendingRequests.add(accessor.getPosition());
                        RequestEnchantingDataPacket requestPacket = RequestEnchantingDataPacket.create(accessor.getPosition());
                        NetworkHandler.sendToServer(requestPacket);
                    }
                }
                
                int[] boundingBoxSizes = EnchantingTableDataUtil.getEffectiveBoundingBoxSizes(accessor.getLevel(), accessor.getPosition());
                tooltip.add(Component.translatable("tooltip.beer.enchanting_table.range",
                        boundingBoxSizes[0], boundingBoxSizes[1], boundingBoxSizes[2]).withStyle(ChatFormatting.GRAY));
                
                if (data.hasItemModifications()) {
                    tooltip.add(Component.literal("§8[Item mods: " + data.getItemModifiersX() + "," + 
                        data.getItemModifiersY() + "," + data.getItemModifiersZ() + "]"));
                }
            } catch (Exception e) {
                tooltip.add(Component.literal("§cError reading enchanting table data").withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath("beer", "beer");
    }
    
    public static void clearPendingRequest(BlockPos pos) {
        pendingRequests.remove(pos);
    }
}
