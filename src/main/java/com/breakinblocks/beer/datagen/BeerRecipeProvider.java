package com.breakinblocks.beer.datagen;

import com.breakinblocks.beer.Beer;
import com.breakinblocks.beer.compat.EnchantingModifierRecipe;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipeType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BeerRecipeProvider extends RecipeProvider {

    protected BeerRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        enchantingModifierRecipe("increase_x",
            Ingredient.of(Items.REDSTONE_BLOCK),
            Optional.empty(),
            "+X Width",
            "beer.jei.modifier.x_width.description",
            EnchantingModifierRecipe.ModifierType.X_WIDTH);

        enchantingModifierRecipe("decrease_x",
            Ingredient.of(Items.REDSTONE_BLOCK),
            Optional.of(Ingredient.of(Items.QUARTZ)),
            "-X Width",
            "beer.jei.modifier.x_width.description",
            EnchantingModifierRecipe.ModifierType.X_WIDTH);

        enchantingModifierRecipe("increase_y",
            Ingredient.of(Items.GLOWSTONE),
            Optional.empty(),
            "+Y Height",
            "beer.jei.modifier.y_height.description",
            EnchantingModifierRecipe.ModifierType.Y_HEIGHT);

        enchantingModifierRecipe("decrease_y",
            Ingredient.of(Items.GLOWSTONE),
            Optional.of(Ingredient.of(Items.QUARTZ)),
            "-Y Height",
            "beer.jei.modifier.y_height.description",
            EnchantingModifierRecipe.ModifierType.Y_HEIGHT);

        enchantingModifierRecipe("increase_z",
            Ingredient.of(Items.LAPIS_BLOCK),
            Optional.empty(),
            "+Z Width",
            "beer.jei.modifier.z_width.description",
            EnchantingModifierRecipe.ModifierType.Z_WIDTH);

        enchantingModifierRecipe("decrease_z",
            Ingredient.of(Items.LAPIS_BLOCK),
            Optional.of(Ingredient.of(Items.QUARTZ)),
            "-Z Width",
            "beer.jei.modifier.z_width.description",
            EnchantingModifierRecipe.ModifierType.Z_WIDTH);
    }

    private void enchantingModifierRecipe(String name,
                                          Ingredient mainhandInput, Optional<Ingredient> offhandInput,
                                          String effectKey, String descriptionKey,
                                          EnchantingModifierRecipe.ModifierType modifierType) {
        EnchantingModifierRecipeType recipe = new EnchantingModifierRecipeType(
            mainhandInput, offhandInput, false, effectKey, descriptionKey, modifierType
        );
        ResourceKey<Recipe<?>> key = ResourceKey.create(
            Registries.RECIPE,
            Identifier.fromNamespaceAndPath(Beer.MODID, "enchanting_modifier/" + name)
        );
        this.output.accept(key, recipe, null);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new BeerRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "BEER Recipes";
        }
    }
}
