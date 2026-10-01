package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.util.BookshelfOffsetUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.shadowsoffire.apothic_enchanting.table.EnchantmentTableStats;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EnchantingTableBlock;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Pseudo
@Mixin(EnchantmentTableStats.class)
public abstract class TableStatsMixin {

    @WrapOperation(method = "gatherStats(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Ldev/shadowsoffire/apothic_enchanting/table/EnchantmentTableStats;",
            at = @At(opcode = Opcodes.GETSTATIC, target = "net/minecraft/world/level/block/EnchantingTableBlock.BOOKSHELF_OFFSETS : Ljava/util/List;", value = "FIELD"))
    private static List<BlockPos> getBookshelfOffsets(Operation<List<BlockPos>> original, LevelReader level, BlockPos pos) {
        if (level instanceof Level worldLevel) {
            return BookshelfOffsetUtil.getOffsetsForTable(worldLevel, pos);
        }
        return original.call();
    }

}
