package com.breakinblocks.beer.compat;

import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipeType;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnchantingTableCategory implements IRecipeCategory<EnchantingModifierRecipeType> {
    public static final IRecipeType<EnchantingModifierRecipeType> TYPE =
        IRecipeType.create("beer", "enchanting_modifiers", EnchantingModifierRecipeType.class);

    private static final int WIDTH = 168;
    private static final int HEIGHT = 80;

    private static final int MAIN_SLOT_X = 8;
    private static final int MAIN_SLOT_Y = 8;
    private static final int OFF_SLOT_X = 8;
    private static final int OFF_SLOT_Y = 56;
    private static final int TABLE_X = 40;
    private static final int TABLE_Y = 32;
    private static final int LABEL_X = 30;
    private static final int ARROW_X = 62;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public EnchantingTableCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableItemStack(new ItemStack(Blocks.ENCHANTING_TABLE));
        this.title = Component.translatable("beer.jei.category.enchanting_modifiers");
    }

    @Override
    public @NotNull IRecipeType<EnchantingModifierRecipeType> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
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
    public void setRecipe(IRecipeLayoutBuilder builder, EnchantingModifierRecipeType recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, MAIN_SLOT_X, MAIN_SLOT_Y)
               .addItemStacks(stacksOf(recipe.getMainhandInput()));

        recipe.getOffhandInput().ifPresent(offhand ->
            builder.addSlot(RecipeIngredientRole.INPUT, OFF_SLOT_X, OFF_SLOT_Y)
                   .addItemStacks(stacksOf(offhand))
        );

        builder.addInvisibleIngredients(RecipeIngredientRole.CRAFTING_STATION)
               .add(new ItemStack(Blocks.ENCHANTING_TABLE));

        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
               .add(new ItemStack(Blocks.ENCHANTING_TABLE));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, EnchantingModifierRecipeType recipe, IFocusGroup focuses) {
        Component mainHandLabel = Component.translatable("beer.jei.category.main_hand").withStyle(ChatFormatting.DARK_GRAY);
        builder.addText(mainHandLabel, WIDTH - LABEL_X, 10)
               .setPosition(LABEL_X, MAIN_SLOT_Y + 4);

        if (recipe.getOffhandInput().isPresent()) {
            Component offHandLabel = Component.translatable("beer.jei.category.off_hand").withStyle(ChatFormatting.DARK_GRAY);
            builder.addText(offHandLabel, WIDTH - LABEL_X, 10)
                   .setPosition(LABEL_X, OFF_SLOT_Y + 4);

            Component notConsumed = Component.translatable("beer.jei.category.not_consumed")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
            builder.addText(notConsumed, WIDTH - LABEL_X, 10)
                   .setPosition(LABEL_X, OFF_SLOT_Y + 14);
        }

        builder.addDrawable(makeItemDrawable(new ItemStack(Blocks.ENCHANTING_TABLE)), TABLE_X, TABLE_Y);

        Component instruction = Component.translatable("beer.jei.category.right_click_table")
            .withStyle(ChatFormatting.DARK_GREEN);
        builder.addText(instruction, WIDTH - ARROW_X, 10)
               .setPosition(ARROW_X, TABLE_Y + 4);

        Component effectText = Component.literal(recipe.getEffectKey())
            .withStyle(effectColor(recipe.getModifierType(), recipe.getOffhandInput().isPresent()));
        builder.addText(effectText, WIDTH - ARROW_X, 10)
               .setPosition(ARROW_X, TABLE_Y - 10);

        Component description = Component.translatable(recipe.getDescriptionKey()).withStyle(ChatFormatting.GRAY);
        builder.addText(description, WIDTH - 4, 20)
               .setPosition(4, HEIGHT - 18);

        if (Config.enableXpCosts) {
            boolean isDecrease = recipe.getOffhandInput().isPresent();
            Component xpText = isDecrease
                ? Component.translatable("beer.jei.modifier.xp_gain", Config.xpCostPerModifier)
                    .withStyle(ChatFormatting.GREEN)
                : Component.translatable("beer.jei.modifier.xp_cost", Config.xpCostPerModifier)
                    .withStyle(ChatFormatting.RED);
            builder.addText(xpText, WIDTH - ARROW_X, 10)
                   .setPosition(ARROW_X, TABLE_Y + 18);
        }
    }

    private static ChatFormatting effectColor(EnchantingModifierRecipe.ModifierType type, boolean decrease) {
        if (decrease) return ChatFormatting.YELLOW;
        return switch (type) {
            case X_WIDTH -> ChatFormatting.RED;
            case Y_HEIGHT -> ChatFormatting.GREEN;
            case Z_WIDTH -> ChatFormatting.BLUE;
            default -> ChatFormatting.WHITE;
        };
    }

    private static IDrawable makeItemDrawable(ItemStack stack) {
        return new IDrawable() {
            @Override public int getWidth() { return 16; }
            @Override public int getHeight() { return 16; }
            @Override public void draw(GuiGraphicsExtractor gfx, int x, int y) {
                gfx.fakeItem(stack, x, y);
            }
        };
    }

    private static List<ItemStack> stacksOf(Ingredient ingredient) {
        return ingredient.items().map(ItemStack::new).toList();
    }
}
