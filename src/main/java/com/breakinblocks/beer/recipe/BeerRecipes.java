package com.breakinblocks.beer.recipe;

import com.breakinblocks.beer.Beer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BeerRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Beer.MODID);
        
    public static final DeferredRegister<RecipeType<?>> TYPES = 
        DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Beer.MODID);

    public static final RegistryObject<RecipeSerializer<EnchantingModifierRecipe>> ENCHANTING_MODIFIER_SERIALIZER = 
        SERIALIZERS.register("enchanting_modifier", EnchantingModifierRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<EnchantingModifierRecipe>> ENCHANTING_MODIFIER_TYPE = 
        TYPES.register("enchanting_modifier", () -> new RecipeType<EnchantingModifierRecipe>() {
            @Override
            public String toString() {
                return "beer:enchanting_modifier";
            }
        });

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
