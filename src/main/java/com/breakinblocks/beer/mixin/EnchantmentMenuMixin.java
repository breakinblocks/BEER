package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.util.BookshelfOffsetUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {
    
    @Shadow @Final private ContainerLevelAccess access;
    
    @Redirect(method = "*", 
              at = @At(value = "FIELD", 
                      target = "Lnet/minecraft/world/level/block/EnchantmentTableBlock;BOOKSHELF_OFFSETS:Ljava/util/List;"))
    private List<BlockPos> beer$useCustomBookshelfOffsets() {
        return access.evaluate(BookshelfOffsetUtil::getOffsetsForTable).orElse(EnchantmentTableBlock.BOOKSHELF_OFFSETS);
    }
}