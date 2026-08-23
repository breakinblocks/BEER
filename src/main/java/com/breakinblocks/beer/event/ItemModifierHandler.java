package com.breakinblocks.beer.event;

import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.data.BeerDataAttachments;
import com.breakinblocks.beer.data.EnchantingTableRangeData;
import com.breakinblocks.beer.network.ApplyItemModifierPacket;
import com.breakinblocks.beer.network.NetworkHandler;
import com.breakinblocks.beer.network.SyncEnchantingDataPacket;
import com.breakinblocks.beer.recipe.BeerRecipes;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipeType;
import com.breakinblocks.beer.compat.EnchantingModifierRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = "beer")
public class ItemModifierHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!Config.enableItemModifiers) {
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

        
        ModifierType modifierType = getModifierType(level, heldItem, offhandItem);
        if (modifierType == null) {
            return;
        }
        
        boolean decreaseMode = isDecreaseMode(level, heldItem, offhandItem);
        
        if (!level.isClientSide()) {
            applyModification(level, pos, player, modifierType, decreaseMode, heldItem);
        }
        
        event.setCanceled(true);
    }
    
    private static ModifierType getModifierType(Level level, ItemStack mainhandStack, ItemStack offhandStack) {
        try {
            var recipeManager = level.getRecipeManager();
            var recipeType = BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();
            
            if (recipeManager == null || recipeType == null) {
                return null;
            }
            
            var allRecipes = recipeManager.getAllRecipesFor(recipeType);
            
            for (var recipeHolder : allRecipes) {
                var recipe = recipeHolder.value();
                
                if (!recipe.getMainhandInput().test(mainhandStack)) {
                    continue;
                }
                
                if (recipe.getOffhandInput() != null && !recipe.getOffhandInput().isEmpty()) {
                    if (offhandStack.isEmpty() || !recipe.getOffhandInput().test(offhandStack)) {
                        continue;
                    }
                }
                
                return convertToModifierType(recipe.getModifierType());
            }
        } catch (Exception e) {
            System.err.println("[BEER] Error loading modifier type from recipes: " + e.getMessage());
        }
        
        return null;
    }
    
    private static boolean isDecreaseMode(Level level, ItemStack mainhandStack, ItemStack offhandStack) {
        try {
            var recipeManager = level.getRecipeManager();
            var recipeType = BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();
            
            if (recipeManager == null || recipeType == null) {
                return false;
            }
            
            var allRecipes = recipeManager.getAllRecipesFor(recipeType);
            
            for (var recipeHolder : allRecipes) {
                var recipe = recipeHolder.value();
                
                if (!recipe.getMainhandInput().test(mainhandStack)) {
                    continue;
                }
                
                if (recipe.getOffhandInput() != null && !recipe.getOffhandInput().isEmpty()) {
                    if (offhandStack.isEmpty() || !recipe.getOffhandInput().test(offhandStack)) {
                        continue;
                    }
                    return recipe.getEffectKey().startsWith("-");
                }
            }
        } catch (Exception e) {
            System.err.println("[BEER] Error checking decrease mode from recipes: " + e.getMessage());
        }
        
        return false;
    }
    
    private static ModifierType convertToModifierType(EnchantingModifierRecipe.ModifierType recipeType) {
        switch (recipeType) {
            case X_WIDTH:
                return ModifierType.X_WIDTH;
            case Y_HEIGHT:
                return ModifierType.Y_HEIGHT;
            case Z_WIDTH:
                return ModifierType.Z_WIDTH;
            default:
                return null;
        }
    }
    
    private static void applyModification(Level level, BlockPos pos, Player player, ModifierType modifierType, boolean decreaseMode, ItemStack heldItem) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof EnchantingTableBlockEntity)) {
            return;
        }
        
        EnchantingTableRangeData data = blockEntity.getData(BeerDataAttachments.ENCHANTING_TABLE_RANGE.get());

        int modifierAmount = Config.modifierAmountPerUse;
        if (decreaseMode) {
            modifierAmount = -modifierAmount;
        }
        
        boolean success = false;
        
        switch (modifierType) {
            case X_WIDTH:
                int newX = data.getItemModifiersX() + modifierAmount;
                if (isValidModifierX(newX)) {
                    data.addItemModifierX(modifierAmount);
                    success = true;
                }
                break;
            case Z_WIDTH:
                int newZ = data.getItemModifiersZ() + modifierAmount;
                if (isValidModifierZ(newZ)) {
                    data.addItemModifierZ(modifierAmount);
                    success = true;
                }
                break;
            case Y_HEIGHT:
                int newY = data.getItemModifiersY() + modifierAmount;
                if (isValidModifierY(newY)) {
                    data.addItemModifierY(modifierAmount);
                    success = true;
                }
                break;
        }
        
        if (success) {
            if (Config.enableXpCosts) {
                int xpCost = Config.xpCostPerModifier;
                if (decreaseMode) {
                    player.giveExperiencePoints(xpCost);
                    
                    if (player instanceof ServerPlayer serverPlayer) {
                        Component xpGainMessage = Component.translatable("beer.message.xp_gained", xpCost)
                                .withStyle(net.minecraft.ChatFormatting.GREEN);
                        serverPlayer.sendSystemMessage(xpGainMessage, true);
                    }
                } else {
                    int totalXp = getTotalExperience(player);
                    if (totalXp < xpCost && !player.getAbilities().instabuild) {
                        level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
                        
                        if (player instanceof ServerPlayer serverPlayer) {
                            Component xpMessage = Component.translatable("beer.message.insufficient_xp", xpCost, totalXp)
                                    .withStyle(net.minecraft.ChatFormatting.RED);
                            serverPlayer.sendSystemMessage(xpMessage, true); // true = action bar
                            NetworkHandler.sendToPlayer(new ApplyItemModifierPacket(pos, modifierType, true, false, 
                                0, 0, 0), serverPlayer);
                        }
                        return;
                    }
                    if (!player.getAbilities().instabuild) {
                        addExperience(player, -xpCost);
                    }
                    
                    if (player instanceof ServerPlayer serverPlayer) {
                        Component xpConsumedMessage = Component.translatable("beer.message.xp_consumed", xpCost)
                                .withStyle(net.minecraft.ChatFormatting.YELLOW);
                        serverPlayer.sendSystemMessage(xpConsumedMessage, true);
                    }
                }
            }
            
            blockEntity.setChanged();
            
            if (Config.consumeItems && !player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }
            
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.2F);
            
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new ApplyItemModifierPacket(pos, modifierType, !decreaseMode, true, 
                    data.getEffectiveRangeX(), data.getEffectiveRangeY(), data.getEffectiveRangeZ()), serverPlayer);
            }
            
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(pos, data);
                NetworkHandler.sendToPlayersNear(syncPacket, serverLevel, pos, 64.0);
            }
        } else {
            level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
            
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new ApplyItemModifierPacket(pos, modifierType, !decreaseMode, false, 
                    0, 0, 0), serverPlayer);
            }
        }
    }
    
    private static boolean isValidModifier(int modifier) {
        return Math.abs(modifier) <= Config.maxItemModifiersPerAxis;
    }
    
    private static boolean isValidModifierX(int modifier) {
        return modifier >= -2 && modifier <= Config.maxItemModifiersPerAxis;
    }
    
    private static boolean isValidModifierY(int modifier) {
        return modifier >= -1 && modifier <= Config.maxItemModifiersPerAxis;
    }
    
    private static boolean isValidModifierZ(int modifier) {
        return modifier >= -2 && modifier <= Config.maxItemModifiersPerAxis;
    }
    
    private static boolean isValidEffectiveRange(int effectiveRange, int maxAllowed) {
        return effectiveRange >= 1 && effectiveRange <= maxAllowed;
    }
    
    private static boolean isValidEffectiveRangeY(int effectiveRange, int maxAllowed) {
        return effectiveRange >= 0 && effectiveRange <= maxAllowed;
    }
    
    private static int getTotalExperience(Player player) {
        return (int)(getExperienceForLevel(player.experienceLevel) + (player.experienceProgress * player.getXpNeededForNextLevel()));
    }
    
    private static int getExperienceForLevel(int level) {
        if (level >= 30) {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        } else if (level >= 16) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return level * level + 6 * level;
        }
    }
    
    private static void addExperience(Player player, int xp) {
        int totalXp = getTotalExperience(player) + xp;
        player.totalExperience = totalXp;
        
        if (totalXp <= 0) {
            player.experienceLevel = 0;
            player.experienceProgress = 0.0f;
            return;
        }
        
        int level = getLevelForExperience(totalXp);
        player.experienceLevel = level;
        
        int xpForCurrentLevel = getExperienceForLevel(level);
        int xpForNextLevel = getExperienceForLevel(level + 1);
        int currentLevelXp = totalXp - xpForCurrentLevel;
        int neededForNext = xpForNextLevel - xpForCurrentLevel;
        
        player.experienceProgress = (float) currentLevelXp / neededForNext;
    }
    
    private static int getLevelForExperience(int xp) {
        if (xp >= 1395) { // Level 30+
            return (int) (8.1 + Math.sqrt(0.4 * (xp - 1395)));
        } else if (xp >= 315) { // Level 16-29  
            return (int) (8.1 + Math.sqrt(0.4 * (xp - 315)));
        } else { // Level 0-15
            return (int) (-3 + Math.sqrt(9 + 4 * xp)) / 2;
        }
    }
    
    public enum ModifierType {
        X_WIDTH,
        Z_WIDTH,
        Y_HEIGHT
    }
}
