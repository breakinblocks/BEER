package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.util.BookshelfOffsetUtil;
import dev.shadowsoffire.apothic_enchanting.table.EnchantmentTableStats;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EnchantingTableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(
        method = "gatherStats(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;I)Ldev/shadowsoffire/apothic_enchanting/table/EnchantmentTableStats;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void beer$useCustomOffsets(LevelReader level, BlockPos pos, int itemEnch, CallbackInfoReturnable<EnchantmentTableStats> cir) {
        EnchantmentTableStats.Builder builder = new EnchantmentTableStats.Builder(itemEnch);
        Iterable<BlockPos> offsets = level instanceof Level worldLevel
            ? BookshelfOffsetUtil.getOffsetsForTable(worldLevel, pos)
            : EnchantingTableBlock.BOOKSHELF_OFFSETS;

        for (BlockPos offset : offsets) {
            if (canReadStatsFrom(level, pos, offset)) {
                gatherStats(builder, level, pos.offset(offset));
            }
        }
        cir.setReturnValue(builder.build());
    }
}
