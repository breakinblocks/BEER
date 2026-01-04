package com.breakinblocks.beer.util;

import com.breakinblocks.beer.BeerConfig;
import com.breakinblocks.beer.capability.EnchantingTableRangeCapabilityProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;

import java.util.List;
import java.util.stream.Collectors;

public class BookshelfOffsetUtil {
    
    public static List<BlockPos> getOffsetsForTable(Level level, BlockPos pos) {
        int rangeX = BeerConfig.getRangeX();
        int rangeY = BeerConfig.getRangeY();
        int rangeZ = BeerConfig.getRangeZ();
        
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof EnchantmentTableBlockEntity) {
             var cap = be.getCapability(EnchantingTableRangeCapabilityProvider.CAPABILITY).orElse(null);
             if (cap != null) {
                 rangeX = cap.getEffectiveRangeX(rangeX);
                 rangeY = cap.getEffectiveRangeY(rangeY);
                 rangeZ = cap.getEffectiveRangeZ(rangeZ);
             }
        }
        
        // Fallback or verify minimums
        rangeX = Math.max(2, rangeX);
        rangeY = Math.max(1, rangeY);
        rangeZ = Math.max(2, rangeZ);
        
        return BlockPos.betweenClosedStream(-rangeX, -rangeY, -rangeZ, rangeX, rangeY, rangeZ)
                .filter(p -> Math.abs(p.getX()) > 1 || Math.abs(p.getZ()) > 1)
                .map(BlockPos::immutable)
                .collect(Collectors.toList());
    }
}
