package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.util.BookshelfOffsetUtil;
import dev.shadowsoffire.apothic_enchanting.table.EnchantmentTableStats;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EnchantingTableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(EnchantmentTableStats.class)
public abstract class TableStatsMixin {

    @Shadow
    public static boolean canReadStatsFrom(LevelReader level, BlockPos pos, BlockPos offset) {
        throw new AssertionError();
    }

    @Shadow
    public static void gatherStats(EnchantmentTableStats.Builder builder, LevelReader level, BlockPos pos) {
        throw new AssertionError();
    }

    /**
     * @author TheonlyTazz
     * @reason Use custom bookshelf offsets from BookshelfOffsetUtil instead of default EnchantingTableBlock.BOOKSHELF_OFFSETS
     */
    @Overwrite
    public static EnchantmentTableStats gatherStats(LevelReader level, BlockPos pos) {
        EnchantmentTableStats.Builder builder = new EnchantmentTableStats.Builder();

        if (level instanceof Level worldLevel) {
            for (BlockPos offset : BookshelfOffsetUtil.getOffsetsForTable(worldLevel, pos)) {
                if (canReadStatsFrom(level, pos, offset)) {
                    gatherStats(builder, level, pos.offset(offset));
                }
            }
        } else {
            for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                if (canReadStatsFrom(level, pos, offset)) {
                    gatherStats(builder, level, pos.offset(offset));
                }
            }
        }

        return builder.build();
    }

}
