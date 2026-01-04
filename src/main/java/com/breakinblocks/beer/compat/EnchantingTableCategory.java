package com.breakinblocks.beer.compat;

import com.breakinblocks.beer.BeerConfig;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchantingTableCategory implements IRecipeCategory<EnchantingModifierRecipe> {
    public static final RecipeType<EnchantingModifierRecipe> TYPE = RecipeType.create("beer", "enchanting_modifiers", EnchantingModifierRecipe.class);
    
    public static final ResourceLocation TEXTURES = new ResourceLocation("beer", "textures/gui/enchanting_jei.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public EnchantingTableCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(169, 75);
        this.icon = helper.createDrawableItemStack(new ItemStack(Blocks.ENCHANTING_TABLE));
        this.title = Component.translatable("beer.jei.category.enchanting_modifiers");
    }

    @Override
    public @NotNull RecipeType<EnchantingModifierRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnchantingModifierRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 11)
               .addIngredients(recipe.getMainhandInput());

        if (recipe.getOffhandInput() != Ingredient.EMPTY) {
            builder.addSlot(RecipeIngredientRole.INPUT, 11, 48)
                   .addIngredients(recipe.getOffhandInput());
        }

        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST)
               .addItemStack(new ItemStack(Blocks.ENCHANTING_TABLE));
               
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
               .addItemStack(new ItemStack(Blocks.ENCHANTING_TABLE));
    }

    @Override
    public void draw(@NotNull EnchantingModifierRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics gfx, double mouseX, double mouseY) {
        Screen scn = Minecraft.getInstance().screen;
        if(scn == null) return;

        Font font = Minecraft.getInstance().font;

        // Draw background
        gfx.blit(TEXTURES, 0, 0, 0, 0, getWidth(), getHeight(), 256, 256);
        if (recipe.getOffhandInput() == Ingredient.EMPTY) {
            gfx.blit(TEXTURES, 1, 31, 0, 0, 88, 28, 34, 256, 256);
        }
        gfx.renderFakeItem(new ItemStack(Blocks.ENCHANTING_TABLE), 31, 29);

        
        String effectText = recipe.getEffectKey();
        int textColor = 0x000000;
        int textWidth = font.width(effectText);
        int left = 168;
        int top = 34;

        // Draw main effect text
        gfx.drawString(font, effectText, left - textWidth, top, textColor, false);
        
        // Draw XP cost if enabled
        if (BeerConfig.isEnableXpCosts()) {
            drawXPCost(gfx, recipe, font);
        }
        drawTooltipsIfHovered(scn, gfx, recipe, font, mouseX, mouseY);
    }

    private void drawXPCost(GuiGraphics gfx, EnchantingModifierRecipe recipe, Font font) {
        boolean isDecrease = recipe.getOffhandInput() != Ingredient.EMPTY; // Has quartz in offhand = decrease mode

        gfx.renderFakeItem(new ItemStack(Items.EXPERIENCE_BOTTLE), 75, 50);

        if (isDecrease) {
            Component xpText = Component.translatable("beer.jei.modifier.xp_gain", BeerConfig.getXpCostPerModifier());
            gfx.drawString(font, xpText, 95, 54, 0x55FF55, true); // Green for gain
        } else {
            Component xpText = Component.translatable("beer.jei.modifier.xp_cost", BeerConfig.getXpCostPerModifier());
            gfx.drawString(font, xpText, 95, 54, 0xFF5555, true); // Red for cost
        }
    }

    private void drawTooltipsIfHovered(Screen scn, GuiGraphics gfx, EnchantingModifierRecipe recipe, Font font, double mouseX, double mouseY) {
        List<Component> tooltips = new ArrayList<>();
        int left; int top; int width; int height;

        // MainHand ? Tooltip
        left = -1; top = 13; width = 9; height = 12;
        if (isHover(mouseX, mouseY, left, top, width, height)) {
            // gfx.blit(TEXTURES, -1, 13, 0, 0, 75, 10, 12, 256, 256); // Removed as it might be buggy with negative coords
            tooltips.add(Component.translatable("beer.jei.category.main_hand"));
        }
        // OffHand ? Tooltip
        left = 0; top = 50;
        if (recipe.getOffhandInput() != Ingredient.EMPTY && isHover(mouseX, mouseY, left, top, width, height)) {
            // gfx.blit(TEXTURES, -1, 50, 0, 0, 75, 10, 12, 256, 256);
            tooltips.add(Component.translatable("beer.jei.category.off_hand"));
            tooltips.add(Component.translatable("beer.jei.category.not_consumed").withStyle(ChatFormatting.GRAY));
        }

        // Enchanting table tooltip (center icon)
        left = 36; top = 30; width = 16; height = 16;
        if (isHover(mouseX, mouseY, left, top, width, height)) {
            tooltips.add(Component.translatable("beer.jei.category.right_click_table"));
        }
        
        // Main effect text tooltip
        else {
            String effectText = recipe.getEffectKey();
            left = 168; top = 34; width = font.width(effectText);
            height = font.lineHeight + 1;
            if (isHover(mouseX, mouseY, left - width, top, width, height)) {
                if (recipe.getDescriptionKey() != null && !recipe.getDescriptionKey().isEmpty()) {
                    tooltips.add(Component.translatable(recipe.getDescriptionKey()));
                }
            }
        }
        
        // Render tooltips if any
        if (!tooltips.isEmpty()) {
            gfx.renderComponentTooltip(font, tooltips, (int)mouseX, (int)mouseY);
        }
    }

    private boolean isHover(double mouseX, double mouseY, int left, int top, int width, int height) {
        return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY < top + height;
    }
}
