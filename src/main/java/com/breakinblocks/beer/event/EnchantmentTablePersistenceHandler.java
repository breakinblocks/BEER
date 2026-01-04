package com.breakinblocks.beer.event;

import com.breakinblocks.beer.BeerConfig;
import com.breakinblocks.beer.capability.EnchantingTableRangeCapabilityProvider;
import com.breakinblocks.beer.network.BeerNetworkHandler;
import com.breakinblocks.beer.network.SyncEnchantingDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EnchantmentTablePersistenceHandler {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;

        BlockPos pos = event.getPos();
        if (!event.getPlacedBlock().is(Blocks.ENCHANTING_TABLE)) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(Blocks.ENCHANTING_TABLE.asItem())) {
            stack = player.getOffhandItem();
            if (!stack.is(Blocks.ENCHANTING_TABLE.asItem())) {
                return;
            }
        }

        if (stack.hasTag() && stack.getTag().contains("beer_range_data")) {
            CompoundTag data = stack.getTag().getCompound("beer_range_data");
            BlockEntity be = event.getLevel().getBlockEntity(pos);
            if (be != null) {
                be.getCapability(EnchantingTableRangeCapabilityProvider.CAPABILITY, null).ifPresent(cap -> {
                    cap.deserializeNBT(data);
                    be.setChanged();
                    
                    if (event.getLevel() instanceof ServerLevel serverLevel) {
                         BeerNetworkHandler.sendToPlayersNear(
                             SyncEnchantingDataPacket.create(pos, cap),
                             serverLevel,
                             pos,
                             64.0
                         );
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getState().is(Blocks.ENCHANTING_TABLE)) {
            BlockEntity be = event.getLevel().getBlockEntity(event.getPos());
            if (be != null) {
                be.getCapability(EnchantingTableRangeCapabilityProvider.CAPABILITY, null).ifPresent(cap -> {
                    if (cap.getItemModifiersX() != 0 || cap.getItemModifiersY() != 0 || cap.getItemModifiersZ() != 0) {
                        if (event.getLevel() instanceof Level level) {
                            ItemStack stack = new ItemStack(Blocks.ENCHANTING_TABLE);
                            CompoundTag tag = stack.getOrCreateTag();
                            tag.put("beer_range_data", cap.serializeNBT());

                            int effX = cap.getEffectiveRangeX(BeerConfig.getRangeX());
                            int effY = cap.getEffectiveRangeY(BeerConfig.getRangeY());
                            int effZ = cap.getEffectiveRangeZ(BeerConfig.getRangeZ());

                            int displayX = Math.max(1, effX * 2 + 1);
                            int displayY = Math.max(1, effY + 1);
                            int displayZ = Math.max(1, effZ * 2 + 1);

                            Component loreComponent = Component.translatable("tooltip.beer.enchanting_table.range", displayX, displayY, displayZ)
                                    .withStyle(net.minecraft.ChatFormatting.GRAY);
                            
                            ListTag loreList = new ListTag();
                            loreList.add(StringTag.valueOf(Component.Serializer.toJson(loreComponent)));
                            
                            stack.getOrCreateTagElement("display").put("Lore", loreList);
                            
                            Block.popResource(level, event.getPos(), stack);
                            
                            event.setCanceled(true);
                            level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                });
            }
        }
    }
}
