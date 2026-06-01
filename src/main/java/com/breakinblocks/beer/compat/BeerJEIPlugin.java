package com.breakinblocks.beer.compat;

import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipeType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;

@JeiPlugin
public class BeerJEIPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath("beer", "enchanting_modifiers");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EnchantingTableCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!Config.enableItemModifiers) {
            return;
        }

        List<EnchantingModifierRecipeType> jeiRecipes = List.of(
            new EnchantingModifierRecipeType(
                Ingredient.of(Items.REDSTONE_BLOCK), Optional.empty(), false,
                "+X Width", "beer.jei.modifier.x_width.description",
                EnchantingModifierRecipe.ModifierType.X_WIDTH),
            new EnchantingModifierRecipeType(
                Ingredient.of(Items.REDSTONE_BLOCK), Optional.of(Ingredient.of(Items.QUARTZ)), false,
                "-X Width", "beer.jei.modifier.x_width.description",
                EnchantingModifierRecipe.ModifierType.X_WIDTH),
            new EnchantingModifierRecipeType(
                Ingredient.of(Items.GLOWSTONE), Optional.empty(), false,
                "+Y Height", "beer.jei.modifier.y_height.description",
                EnchantingModifierRecipe.ModifierType.Y_HEIGHT),
            new EnchantingModifierRecipeType(
                Ingredient.of(Items.GLOWSTONE), Optional.of(Ingredient.of(Items.QUARTZ)), false,
                "-Y Height", "beer.jei.modifier.y_height.description",
                EnchantingModifierRecipe.ModifierType.Y_HEIGHT),
            new EnchantingModifierRecipeType(
                Ingredient.of(Items.LAPIS_BLOCK), Optional.empty(), false,
                "+Z Width", "beer.jei.modifier.z_width.description",
                EnchantingModifierRecipe.ModifierType.Z_WIDTH),
            new EnchantingModifierRecipeType(
                Ingredient.of(Items.LAPIS_BLOCK), Optional.of(Ingredient.of(Items.QUARTZ)), false,
                "-Z Width", "beer.jei.modifier.z_width.description",
                EnchantingModifierRecipe.ModifierType.Z_WIDTH)
        );

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
        registration.addCraftingStation(EnchantingTableCategory.TYPE, new ItemStack(Blocks.ENCHANTING_TABLE));
    }
}
