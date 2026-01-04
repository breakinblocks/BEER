package com.breakinblocks.beer.recipe;

import com.breakinblocks.beer.Beer;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnchantingModifierRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient mainhandInput;
    private final Ingredient offhandInput;
    private final ModifierType modifierType;
    private final String effectKey;
    private final String descriptionKey;

    public enum ModifierType {
        X_WIDTH, Y_HEIGHT, Z_WIDTH
    }

    public EnchantingModifierRecipe(ResourceLocation id, Ingredient mainhand, Ingredient offhand, ModifierType type, String key, String descriptionKey) {
        this.id = id;
        this.mainhandInput = mainhand;
        this.offhandInput = offhand;
        this.modifierType = type;
        this.effectKey = key;
        this.descriptionKey = descriptionKey;
    }
    
    public Ingredient getMainhandInput() { return mainhandInput; }
    public Ingredient getOffhandInput() { return offhandInput; }
    public ModifierType getModifierType() { return modifierType; }
    public String getEffectKey() { return effectKey; }
    public String getDescriptionKey() { return descriptionKey; }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BeerRecipes.ENCHANTING_MODIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();
    }
    
    public static class Serializer implements RecipeSerializer<EnchantingModifierRecipe> {
        @Override
        public EnchantingModifierRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient main = Ingredient.fromJson(pSerializedRecipe.get("mainhand_input"));
            Ingredient off = pSerializedRecipe.has("offhand_input") ? Ingredient.fromJson(pSerializedRecipe.get("offhand_input")) : Ingredient.EMPTY;
            String typeStr = GsonHelper.getAsString(pSerializedRecipe, "modifier_type");
            ModifierType type = ModifierType.valueOf(typeStr.toUpperCase());
            String key = GsonHelper.getAsString(pSerializedRecipe, "effect_key", ""); 
            String descriptionKey = GsonHelper.getAsString(pSerializedRecipe, "description_key", "");
            
            return new EnchantingModifierRecipe(pRecipeId, main, off, type, key, descriptionKey);
        }

        @Override
        public @Nullable EnchantingModifierRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient main = Ingredient.fromNetwork(pBuffer);
            Ingredient off = Ingredient.fromNetwork(pBuffer);
            ModifierType type = pBuffer.readEnum(ModifierType.class);
            String key = pBuffer.readUtf();
            String descriptionKey = pBuffer.readUtf();
            return new EnchantingModifierRecipe(pRecipeId, main, off, type, key, descriptionKey);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, EnchantingModifierRecipe pRecipe) {
            pRecipe.mainhandInput.toNetwork(pBuffer);
            pRecipe.offhandInput.toNetwork(pBuffer);
            pBuffer.writeEnum(pRecipe.modifierType);
            pBuffer.writeUtf(pRecipe.effectKey);
            pBuffer.writeUtf(pRecipe.descriptionKey);
        }
    }
}
