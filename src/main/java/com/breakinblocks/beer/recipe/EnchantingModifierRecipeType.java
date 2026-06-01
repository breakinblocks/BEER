package com.breakinblocks.beer.recipe;

import com.breakinblocks.beer.compat.EnchantingModifierRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class EnchantingModifierRecipeType implements Recipe<RecipeInput> {
    public static final MapCodec<EnchantingModifierRecipeType> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Ingredient.CODEC.fieldOf("mainhand_input").forGetter(EnchantingModifierRecipeType::getMainhandInput),
            Ingredient.CODEC.optionalFieldOf("offhand_input").forGetter(EnchantingModifierRecipeType::getOffhandInput),
            Codec.BOOL.optionalFieldOf("consumes_offhand", false).forGetter(EnchantingModifierRecipeType::consumesOffhand),
            Codec.STRING.fieldOf("effect_key").forGetter(EnchantingModifierRecipeType::getEffectKey),
            Codec.STRING.fieldOf("description_key").forGetter(EnchantingModifierRecipeType::getDescriptionKey),
            Codec.stringResolver(EnchantingModifierRecipe.ModifierType::name, EnchantingModifierRecipe.ModifierType::valueOf)
                .fieldOf("modifier_type").forGetter(EnchantingModifierRecipeType::getModifierType)
        ).apply(instance, EnchantingModifierRecipeType::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantingModifierRecipeType> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, EnchantingModifierRecipeType::getMainhandInput,
        Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, EnchantingModifierRecipeType::getOffhandInput,
        ByteBufCodecs.BOOL, EnchantingModifierRecipeType::consumesOffhand,
        ByteBufCodecs.STRING_UTF8, EnchantingModifierRecipeType::getEffectKey,
        ByteBufCodecs.STRING_UTF8, EnchantingModifierRecipeType::getDescriptionKey,
        ByteBufCodecs.STRING_UTF8.map(EnchantingModifierRecipe.ModifierType::valueOf, EnchantingModifierRecipe.ModifierType::name), EnchantingModifierRecipeType::getModifierType,
        EnchantingModifierRecipeType::new
    );

    public static final RecipeSerializer<EnchantingModifierRecipeType> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private final Ingredient mainhandInput;
    private final Optional<Ingredient> offhandInput;
    private final boolean consumesOffhand;
    private final String effectKey;
    private final String descriptionKey;
    private final EnchantingModifierRecipe.ModifierType modifierType;

    public EnchantingModifierRecipeType(Ingredient mainhandInput, Optional<Ingredient> offhandInput, boolean consumesOffhand,
                                        String effectKey, String descriptionKey, EnchantingModifierRecipe.ModifierType modifierType) {
        this.mainhandInput = mainhandInput;
        this.offhandInput = offhandInput;
        this.consumesOffhand = consumesOffhand;
        this.effectKey = effectKey;
        this.descriptionKey = descriptionKey;
        this.modifierType = modifierType;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();
    }

    public EnchantingModifierRecipe toJEIRecipe() {
        return new EnchantingModifierRecipe(mainhandInput, offhandInput, consumesOffhand, effectKey, descriptionKey, modifierType);
    }

    public Ingredient getMainhandInput() { return mainhandInput; }
    public Optional<Ingredient> getOffhandInput() { return offhandInput; }
    public boolean consumesOffhand() { return consumesOffhand; }
    public String getEffectKey() { return effectKey; }
    public String getDescriptionKey() { return descriptionKey; }
    public EnchantingModifierRecipe.ModifierType getModifierType() { return modifierType; }
}
