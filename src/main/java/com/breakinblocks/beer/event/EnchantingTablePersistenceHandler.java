package com.breakinblocks.beer.event;

import com.breakinblocks.beer.Beer;
import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.data.EnchantingTableRangeData;
import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import com.breakinblocks.beer.network.NetworkHandler;
import com.breakinblocks.beer.network.SyncEnchantingDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class EnchantingTablePersistenceHandler {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!Config.enableItemModifiers) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        BlockPos pos = event.getPos();
        
        if (!event.getPlacedBlock().is(Beer.ENCHANTING_TABLES_BLOCK_TAG)) {
            return;
        }

        if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(Beer.ENCHANTING_TABLES_ITEM_TAG)) {
            stack = player.getOffhandItem();
            if (!stack.is(Beer.ENCHANTING_TABLES_ITEM_TAG)) {
                return;
            }
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        
        if (customData != null) {
            CompoundTag beerData = customData.copyTag();

            if (beerData.contains("ItemModifiersX") || beerData.contains("ItemModifiersY") || beerData.contains("ItemModifiersZ")) {
                int modX = beerData.getInt("ItemModifiersX");
                int modY = beerData.getInt("ItemModifiersY");
                int modZ = beerData.getInt("ItemModifiersZ");
                

                level.getServer().execute(() -> {
                    EnchantingTableDataUtil.setRanges(level, pos, modX, modY, modZ);
                    
                    EnchantingTableRangeData restoredData = EnchantingTableDataUtil.getRangeData(level, pos);
                    
                    SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(pos, restoredData);
                    NetworkHandler.sendToAllPlayers(syncPacket);
                    
                });
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!Config.enableItemModifiers) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        
        BlockPos pos = event.getPos();
        
        if (!event.getState().is(Beer.ENCHANTING_TABLES_BLOCK_TAG)) {
            return;
        }

        if (event.getPlayer().getAbilities().instabuild) {
            return;
        }
        EnchantingTableRangeData data = EnchantingTableDataUtil.getRangeData(level, pos);

        if (!data.hasItemModifications()) {
            return;
        }


        event.setCanceled(true);

        ItemStack enchantingTableStack = new ItemStack(event.getState().getBlock());

        if (enchantingTableStack.isEmpty()) {
            return;
        }

        level.removeBlock(pos, false);

        CompoundTag beerData = new CompoundTag();
        beerData.putInt("ItemModifiersX", data.getItemModifiersX());
        beerData.putInt("ItemModifiersY", data.getItemModifiersY());
        beerData.putInt("ItemModifiersZ", data.getItemModifiersZ());
        
        CustomData customData = CustomData.of(beerData);
        enchantingTableStack.set(DataComponents.CUSTOM_DATA, customData);
        
        java.util.List<net.minecraft.network.chat.Component> lore = new java.util.ArrayList<>();
        
        lore.add(net.minecraft.network.chat.Component.literal("Bookshelf Range Modifiers:")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        var modifierLine = net.minecraft.network.chat.Component.empty();
        boolean hasModifiers = false;
        
        if (data.getItemModifiersX() != 0) {
            if (hasModifiers) modifierLine = modifierLine.append(" ");
            modifierLine = modifierLine.append(net.minecraft.network.chat.Component.literal("X: " + (data.getItemModifiersX() > 0 ? "+" : "") + data.getItemModifiersX())
                    .withStyle(net.minecraft.ChatFormatting.RED));
            hasModifiers = true;
        }
        if (data.getItemModifiersY() != 0) {
            if (hasModifiers) modifierLine = modifierLine.append(" ");
            modifierLine = modifierLine.append(net.minecraft.network.chat.Component.literal("Y: " + (data.getItemModifiersY() > 0 ? "+" : "") + data.getItemModifiersY())
                    .withStyle(net.minecraft.ChatFormatting.GREEN));
            hasModifiers = true;
        }
        if (data.getItemModifiersZ() != 0) {
            if (hasModifiers) modifierLine = modifierLine.append(" ");
            modifierLine = modifierLine.append(net.minecraft.network.chat.Component.literal("Z: " + (data.getItemModifiersZ() > 0 ? "+" : "") + data.getItemModifiersZ())
                    .withStyle(net.minecraft.ChatFormatting.BLUE));
        }
        
        lore.add(modifierLine);
        
        enchantingTableStack.set(DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(lore));


        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5; 
        double z = pos.getZ() + 0.5;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, enchantingTableStack);
        level.addFreshEntity(itemEntity);


        event.getState().getBlock().popExperience(level, pos, 0);
    }
}