package com.breakinblocks.beer.util;

import com.breakinblocks.beer.BeerConfig;
import com.breakinblocks.beer.capability.EnchantingTableRangeCapabilityProvider;
import com.breakinblocks.beer.capability.IEnchantingTableRangeData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;

public class EnchantingTableDataUtil {
    
    public static IEnchantingTableRangeData getRangeData(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EnchantmentTableBlockEntity) {
            return blockEntity.getCapability(EnchantingTableRangeCapabilityProvider.CAPABILITY).orElse(null);
        }
        return null;
    }

    public static int[] getEffectiveBoundingBoxSizes(Level level, BlockPos pos) {
        IEnchantingTableRangeData data = getRangeData(level, pos);
        int rangeX = BeerConfig.getRangeX();
        int rangeY = BeerConfig.getRangeY();
        int rangeZ = BeerConfig.getRangeZ();
        
        if (data != null) {
            rangeX = data.getEffectiveRangeX(rangeX);
            rangeY = data.getEffectiveRangeY(rangeY);
            rangeZ = data.getEffectiveRangeZ(rangeZ);
        }
        
        return new int[]{
            Math.max(1, rangeX * 2 + 1), 
            Math.max(1, rangeY + 1), 
            Math.max(1, rangeZ * 2 + 1)
        };
    }
}
