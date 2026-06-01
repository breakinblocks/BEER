package com.breakinblocks.beer.event;

import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.data.EnchantingTableRangeData;
import com.breakinblocks.beer.network.NetworkHandler;
import com.breakinblocks.beer.network.SyncEnchantingDataPacket;
import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }

        CompoundTag beerData = customData.copyTag();
        Optional<Integer> optX = beerData.getInt("ItemModifiersX");
        Optional<Integer> optY = beerData.getInt("ItemModifiersY");
        Optional<Integer> optZ = beerData.getInt("ItemModifiersZ");
        if (optX.isEmpty() && optY.isEmpty() && optZ.isEmpty()) {
            return;
        }

        int modX = optX.orElse(0);
        int modY = optY.orElse(0);
        int modZ = optZ.orElse(0);

        level.getServer().execute(() -> {
            EnchantingTableDataUtil.setRanges(level, pos, modX, modY, modZ);
            EnchantingTableRangeData restoredData = EnchantingTableDataUtil.getRangeData(level, pos);
            SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(pos, restoredData);
            NetworkHandler.sendToAllPlayers(syncPacket);
        });
    }

    @SubscribeEvent
    public static void onBlockBreak(BreakBlockEvent event) {
        if (!Config.enableItemModifiers) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        BlockPos pos = event.getPos();

        if (!event.getState().is(Blocks.ENCHANTING_TABLE)) {
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
        level.removeBlock(pos, false);

        ItemStack enchantingTableStack = new ItemStack(Blocks.ENCHANTING_TABLE);

        CompoundTag beerData = new CompoundTag();
        beerData.putInt("ItemModifiersX", data.getItemModifiersX());
        beerData.putInt("ItemModifiersY", data.getItemModifiersY());
        beerData.putInt("ItemModifiersZ", data.getItemModifiersZ());
        enchantingTableStack.set(DataComponents.CUSTOM_DATA, CustomData.of(beerData));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal("Bookshelf Range Modifiers:").withStyle(ChatFormatting.GRAY));

        MutableComponent modifierLine = Component.empty();
        boolean hasModifiers = false;
        if (data.getItemModifiersX() != 0) {
            modifierLine = modifierLine.append(Component.literal("X: " + (data.getItemModifiersX() > 0 ? "+" : "") + data.getItemModifiersX())
                    .withStyle(ChatFormatting.RED));
            hasModifiers = true;
        }
        if (data.getItemModifiersY() != 0) {
            if (hasModifiers) modifierLine = modifierLine.append(Component.literal(" "));
            modifierLine = modifierLine.append(Component.literal("Y: " + (data.getItemModifiersY() > 0 ? "+" : "") + data.getItemModifiersY())
                    .withStyle(ChatFormatting.GREEN));
            hasModifiers = true;
        }
        if (data.getItemModifiersZ() != 0) {
            if (hasModifiers) modifierLine = modifierLine.append(Component.literal(" "));
            modifierLine = modifierLine.append(Component.literal("Z: " + (data.getItemModifiersZ() > 0 ? "+" : "") + data.getItemModifiersZ())
                    .withStyle(ChatFormatting.BLUE));
        }
        lore.add(modifierLine);
        enchantingTableStack.set(DataComponents.LORE, new ItemLore(lore));

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        level.addFreshEntity(new ItemEntity(level, x, y, z, enchantingTableStack));

        event.getState().getBlock().popExperience(level, pos, 0);
    }
}
