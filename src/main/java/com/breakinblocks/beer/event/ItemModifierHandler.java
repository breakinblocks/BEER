package com.breakinblocks.beer.event;

import com.breakinblocks.beer.Beer;
import com.breakinblocks.beer.BeerConfig;
import com.breakinblocks.beer.capability.EnchantingTableRangeCapabilityProvider;
import com.breakinblocks.beer.network.ApplyItemModifierPacket;
import com.breakinblocks.beer.network.BeerNetworkHandler;
import com.breakinblocks.beer.network.SyncEnchantingDataPacket;
import com.breakinblocks.beer.recipe.BeerRecipes;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Beer.MODID)
public class ItemModifierHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!BeerConfig.isEnableItemModifiers()) {
            return;
        }

        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        InteractionHand hand = event.getHand();
        
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }
        
        if (!player.isShiftKeyDown()) {
            return;
        }
        
        if (!level.getBlockState(pos).is(Blocks.ENCHANTING_TABLE)) {
            return;
        }
        
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack offhandItem = player.getOffhandItem();

        EnchantingModifierRecipe recipe = findRecipe(level, heldItem, offhandItem);
        if (recipe == null) {
            return;
        }

        EnchantingModifierRecipe.ModifierType modifierType = recipe.getModifierType();
        boolean decreaseMode = recipe.getEffectKey() != null && recipe.getEffectKey().startsWith("-");
        
        if (!level.isClientSide()) {
            applyModification(level, pos, player, modifierType, decreaseMode, heldItem);
        }
        
        event.setCanceled(true);
    }
    
    private static EnchantingModifierRecipe findRecipe(Level level, ItemStack mainhandStack, ItemStack offhandStack) {
        if (level.isClientSide()) {
            return null;
        }
        
        try {
            var recipeManager = level.getRecipeManager();
            var recipeType = BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();
            
            if (recipeManager == null || recipeType == null) {
                return null;
            }
            
            var allRecipes = recipeManager.getAllRecipesFor(recipeType);
            
            for (var recipe : allRecipes) {
                if (!recipe.getMainhandInput().test(mainhandStack)) {
                    continue;
                }
                
                if (recipe.getOffhandInput() != null && !recipe.getOffhandInput().isEmpty()) {
                    if (offhandStack.isEmpty() || !recipe.getOffhandInput().test(offhandStack)) {
                        continue;
                    }
                }
                
                return recipe;
            }
        } catch (Exception e) {
            Beer.LOGGER.error("Error finding enchanting modifier recipe", e);
        }
        
        return null;
    }
    
    private static void applyModification(Level level, BlockPos pos, Player player, EnchantingModifierRecipe.ModifierType modifierType, boolean decreaseMode, ItemStack heldItem) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof EnchantmentTableBlockEntity)) {
            return;
        }
        
        blockEntity.getCapability(EnchantingTableRangeCapabilityProvider.CAPABILITY).ifPresent(data -> {
            int modifierAmount = BeerConfig.getModifierAmountPerUse();
            if (decreaseMode) {
                modifierAmount = -modifierAmount;
            }
            
            boolean success = false;
            int currentVal = 0;
            int minVal = -2;

            switch (modifierType) {
                case X_WIDTH:
                    currentVal = data.getItemModifiersX();
                    minVal = -2;
                    break;
                case Z_WIDTH:
                    currentVal = data.getItemModifiersZ();
                    minVal = -2;
                    break;
                case Y_HEIGHT:
                    currentVal = data.getItemModifiersY();
                    minVal = -1;
                    break;
            }

            int newVal = currentVal + modifierAmount;
            if (isValidModifier(newVal, minVal)) {
                 switch (modifierType) {
                    case X_WIDTH: data.setItemModifiersX(newVal); break;
                    case Z_WIDTH: data.setItemModifiersZ(newVal); break;
                    case Y_HEIGHT: data.setItemModifiersY(newVal); break;
                }
                success = true;
            }
            
            if (success) {
                if (BeerConfig.isEnableXpCosts()) {
                    int xpCost = BeerConfig.getXpCostPerModifier();
                    if (decreaseMode) {
                        player.giveExperiencePoints(xpCost);
                        
                        if (player instanceof ServerPlayer serverPlayer) {
                            Component xpGainMessage = Component.translatable("beer.message.xp_gained", xpCost)
                                    .withStyle(net.minecraft.ChatFormatting.GREEN);
                            serverPlayer.sendSystemMessage(xpGainMessage, true);
                        }
                    } else {
                        if (player.totalExperience < xpCost && !player.getAbilities().instabuild) {
                            level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
                            
                            if (player instanceof ServerPlayer serverPlayer) {
                                Component xpMessage = Component.translatable("beer.message.insufficient_xp", xpCost, player.totalExperience)
                                        .withStyle(net.minecraft.ChatFormatting.RED);
                                serverPlayer.sendSystemMessage(xpMessage, true);
                                BeerNetworkHandler.sendToPlayer(new ApplyItemModifierPacket(pos, modifierType, true, false, 
                                    0, 0, 0), serverPlayer);
                            }
                            return;
                        }
                        if (!player.getAbilities().instabuild) {
                            player.giveExperiencePoints(-xpCost);
                        }
                        
                        if (player instanceof ServerPlayer serverPlayer) {
                            Component xpConsumedMessage = Component.translatable("beer.message.xp_consumed", xpCost)
                                    .withStyle(net.minecraft.ChatFormatting.YELLOW);
                            serverPlayer.sendSystemMessage(xpConsumedMessage, true);
                        }
                    }
                }
                
                blockEntity.setChanged();
                
                if (BeerConfig.isConsumeItems() && !player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.2F);
                
                if (player instanceof ServerPlayer serverPlayer) {
                    BeerNetworkHandler.sendToPlayer(new ApplyItemModifierPacket(pos, modifierType, !decreaseMode, true, 
                        data.getEffectiveRangeX(BeerConfig.getRangeX()), 
                        data.getEffectiveRangeY(BeerConfig.getRangeY()), 
                        data.getEffectiveRangeZ(BeerConfig.getRangeZ())), serverPlayer);
                }
                
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(pos, data);
                    BeerNetworkHandler.sendToPlayersNear(syncPacket, serverLevel, pos, 64.0);
                }
            } else {
                level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
                
                if (player instanceof ServerPlayer serverPlayer) {
                    BeerNetworkHandler.sendToPlayer(new ApplyItemModifierPacket(pos, modifierType, !decreaseMode, false, 
                        0, 0, 0), serverPlayer);
                }
            }
        });
    }
    
    private static boolean isValidModifier(int modifier, int min) {
        return modifier >= min && modifier <= BeerConfig.getMaxItemModifiersPerAxis();
    }
}
