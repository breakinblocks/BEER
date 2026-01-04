package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.util.BookshelfOffsetUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Pseudo
@Mixin(targets = "shadows.apotheosis.ench.table.EnchantingStatManager")
public class TableStatsMixin {

    @Redirect(method = "scan(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lshadows/apotheosis/ench/table/TableStats;", 
              at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/EnchantmentTableBlock;BOOKSHELF_OFFSETS:Ljava/util/List;"), 
              remap = false)
    private static List<BlockPos> beer$useCustomBookshelfOffsets(Level level, BlockPos pos) {
        return BookshelfOffsetUtil.getOffsetsForTable(level, pos);
    }
}
