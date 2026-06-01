package com.breakinblocks.beer.recipe;

import com.breakinblocks.beer.Beer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BeerRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(Registries.RECIPE_TYPE, Beer.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, Beer.MODID);

    public static final Supplier<RecipeType<EnchantingModifierRecipeType>> ENCHANTING_MODIFIER_TYPE =
        RECIPE_TYPES.register("enchanting_modifier", () -> new RecipeType<EnchantingModifierRecipeType>() {
        });

    public static final Supplier<RecipeSerializer<EnchantingModifierRecipeType>> ENCHANTING_MODIFIER_SERIALIZER =
        RECIPE_SERIALIZERS.register("enchanting_modifier", () -> EnchantingModifierRecipeType.SERIALIZER);

    public static void register(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
