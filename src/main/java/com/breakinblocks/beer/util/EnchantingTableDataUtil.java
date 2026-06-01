package com.breakinblocks.beer.util;

import com.breakinblocks.beer.data.BeerDataAttachments;
import com.breakinblocks.beer.data.EnchantingTableRangeData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;

public class EnchantingTableDataUtil {

    public static EnchantingTableRangeData getRangeData(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EnchantingTableBlockEntity enchantingTable) {
            EnchantingTableRangeData data = enchantingTable.getData(BeerDataAttachments.ENCHANTING_TABLE_RANGE.get());
            if (data != null) {
                return data;
            }
        }
        return new EnchantingTableRangeData();
    }

    public static void setRangeData(Level level, BlockPos pos, EnchantingTableRangeData data) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EnchantingTableBlockEntity enchantingTable) {
            enchantingTable.setData(BeerDataAttachments.ENCHANTING_TABLE_RANGE.get(), data);
            enchantingTable.setChanged();
        }
    }

    public static void setRanges(Level level, BlockPos pos, int rangeX, int rangeY, int rangeZ) {
        EnchantingTableRangeData data = getRangeData(level, pos);
        data.setItemModifierZ(rangeZ);
        data.setItemModifierY(rangeY);
        data.setItemModifierX(rangeX);
        setRangeData(level, pos, data);
    }

    public static boolean hasEnchantingTable(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof EnchantingTableBlockEntity;
    }

    public static int[] getEffectiveBoundingBoxSizes(Level level, BlockPos pos) {
        EnchantingTableRangeData data = getRangeData(level, pos);
        return new int[]{
            Math.max(1, data.getEffectiveRangeX() * 2 + 1),
            Math.max(1, data.getEffectiveRangeY() + 1),
            Math.max(1, data.getEffectiveRangeZ() * 2 + 1)
        };
    }

    public static void resetToDefaults(Level level, BlockPos pos) {
        EnchantingTableRangeData data = getRangeData(level, pos);
        data.resetToDefaults();
        setRangeData(level, pos, data);
    }

    public static void forceResetAll(Level level, BlockPos pos) {
        setRangeData(level, pos, new EnchantingTableRangeData());
    }
}
