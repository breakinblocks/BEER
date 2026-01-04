package com.breakinblocks.beer.compat;

import com.breakinblocks.beer.BeerConfig;
import com.breakinblocks.beer.recipe.BeerRecipes;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@JeiPlugin
public class BeerJEIPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("beer", "enchanting_modifiers");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EnchantingTableCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!BeerConfig.isEnableItemModifiers()) {
            return;
        }

        List<EnchantingModifierRecipe> jeiRecipes = new ArrayList<>();
        
        try {
            if (Minecraft.getInstance().level != null) {
                var recipeManager = Minecraft.getInstance().level.getRecipeManager();
                var recipeType = BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();
                
                if (recipeManager != null && recipeType != null) {
                    var allRecipes = recipeManager.getAllRecipesFor(recipeType);
                    
                    jeiRecipes = allRecipes
                        .stream()
                        .sorted(Comparator.comparing(EnchantingModifierRecipe::getId))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            LOGGER.error("[BEER JEI] Failed to load datapack recipes", e);
        }
        
        registration.addRecipes(EnchantingTableCategory.TYPE, jeiRecipes);

        registration.addItemStackInfo(
            new ItemStack(Blocks.ENCHANTING_TABLE),
            Component.translatable("jei.beer.enchanting_table.info.title").withStyle(ChatFormatting.GOLD),
            Component.translatable("jei.beer.enchanting_table.info.bookshelf_range").withStyle(ChatFormatting.BLACK),
            Component.translatable("jei.beer.enchanting_table.info.configuration").withStyle(ChatFormatting.BLACK),
            Component.translatable("jei.beer.enchanting_table.info.range_modification").withStyle(ChatFormatting.BLACK)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.ENCHANTING_TABLE), EnchantingTableCategory.TYPE);
    }
}
