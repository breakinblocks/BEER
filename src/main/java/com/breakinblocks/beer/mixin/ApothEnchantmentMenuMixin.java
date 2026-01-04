package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.util.BookshelfOffsetUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu.TableStats;
import org.spongepowered.asm.mixin.*;

@Pseudo
@Mixin(ApothEnchantmentMenu.class)
public class ApothEnchantmentMenuMixin {

    @Shadow(remap = false)
    private static boolean canReadStatsFrom(Level level, BlockPos tablePos, BlockPos offset) {
        throw new AssertionError();
    }

    @Shadow(remap = false)
    private static void gatherStats(TableStats.Builder builder, Level level, BlockPos pos) {
        throw new AssertionError();
    }

    /**
     * @author TheonlyTazz
     * @reason Use custom bookshelf offsets when gathering enchanting stats
     */
    @Overwrite(remap = false)
    public static TableStats gatherStats(Level level, BlockPos pos, int itemEnch) {
        TableStats.Builder builder = new TableStats.Builder(itemEnch);

        for (BlockPos offset : BookshelfOffsetUtil.getOffsetsForTable(level, pos)) {
            if (canReadStatsFrom(level, pos, offset)) {
                gatherStats(builder, level, pos.offset(offset));
            }
        }

        return builder.build();
    }


}
