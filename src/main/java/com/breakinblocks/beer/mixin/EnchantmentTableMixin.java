package com.breakinblocks.beer.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.breakinblocks.beer.util.BookshelfOffsetUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.List;

@Mixin(EnchantmentTableBlock.class)
public class EnchantmentTableMixin {

    @Redirect(method = "animateTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/EnchantmentTableBlock;BOOKSHELF_OFFSETS:Ljava/util/List;"))
    public List<BlockPos> beer$animateTickOffsets(BlockState state, Level level, BlockPos pos, RandomSource random) {
        return BookshelfOffsetUtil.getOffsetsForTable(level, pos);
    }

    /**
     * Skip the intermediate block check for extended range bookshelves.
     * We simply check if the target position has enchant power bonus.
     */
    @Inject(method = "isValidBookShelf", at = @At("HEAD"), cancellable = true)
    private static void onIsValidBookShelf(Level level, BlockPos tablePos, BlockPos offset, CallbackInfoReturnable<Boolean> cir) {
        BlockPos targetPos = tablePos.offset(offset);
        boolean hasEnchantPower = level.getBlockState(targetPos).getEnchantPowerBonus(level, targetPos) > 0;
        cir.setReturnValue(hasEnchantPower);
    }
}
