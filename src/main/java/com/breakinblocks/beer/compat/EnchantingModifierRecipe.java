package com.breakinblocks.beer.compat;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public class EnchantingModifierRecipe {
    private final Ingredient mainhandInput;
    private final Optional<Ingredient> offhandInput;
    private final boolean consumesOffhand;
    private final String effectKey;
    private final String descriptionKey;
    private final ModifierType type;

    public EnchantingModifierRecipe(Ingredient mainhandInput, Optional<Ingredient> offhandInput, boolean consumesOffhand,
                                    String effectKey, String descriptionKey, ModifierType type) {
        this.mainhandInput = mainhandInput;
        this.offhandInput = offhandInput;
        this.consumesOffhand = consumesOffhand;
        this.effectKey = effectKey;
        this.descriptionKey = descriptionKey;
        this.type = type;
    }

    public Ingredient getMainhandInput() {
        return mainhandInput;
    }

    public Optional<Ingredient> getOffhandInput() {
        return offhandInput;
    }

    public boolean consumesOffhand() {
        return consumesOffhand;
    }

    public String getEffectKey() {
        return effectKey;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public ModifierType getType() {
        return type;
    }

    public boolean hasOffhandInput() {
        return offhandInput.isPresent();
    }

    public enum ModifierType {
        X_WIDTH("+X Width", 0xFF5555),
        Y_HEIGHT("+Y Height", 0x55FF55),
        Z_WIDTH("+Z Width", 0x5555FF),
        DECREASE("Decrease Mode", 0xFFFF55);

        private final String displayName;
        private final int color;

        ModifierType(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getColor() {
            return color;
        }
    }
}
