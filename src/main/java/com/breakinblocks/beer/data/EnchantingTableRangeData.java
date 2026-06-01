package com.breakinblocks.beer.data;

import com.breakinblocks.beer.Config;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class EnchantingTableRangeData implements ValueIOSerializable {
    private int itemModifiersX;
    private int itemModifiersY;
    private int itemModifiersZ;

    public EnchantingTableRangeData() {
        this.itemModifiersX = 0;
        this.itemModifiersY = 0;
        this.itemModifiersZ = 0;
    }

    public EnchantingTableRangeData(int rangeX, int rangeY, int rangeZ) {
        this.itemModifiersX = rangeX;
        this.itemModifiersY = rangeY;
        this.itemModifiersZ = rangeZ;
    }

    public int getItemModifiersX() {
        return itemModifiersX;
    }

    public int getItemModifiersY() {
        return itemModifiersY;
    }

    public int getItemModifiersZ() {
        return itemModifiersZ;
    }

    public int getEffectiveRangeX() {
        return Math.max(0, 2 + itemModifiersX);
    }

    public int getEffectiveRangeY() {
        return Math.max(0, 1 + itemModifiersY);
    }

    public int getEffectiveRangeZ() {
        return Math.max(0, 2 + itemModifiersZ);
    }

    public void addItemModifierX(int amount) {
        this.itemModifiersX = clampItemModifierX(this.itemModifiersX + amount);
    }

    public void addItemModifierY(int amount) {
        this.itemModifiersY = clampItemModifierY(this.itemModifiersY + amount);
    }

    public void addItemModifierZ(int amount) {
        this.itemModifiersZ = clampItemModifierZ(this.itemModifiersZ + amount);
    }

    public void setItemModifierX(int modifier) {
        this.itemModifiersX = clampItemModifierX(modifier);
    }

    public void setItemModifierY(int modifier) {
        this.itemModifiersY = clampItemModifierY(modifier);
    }

    public void setItemModifierZ(int modifier) {
        this.itemModifiersZ = clampItemModifierZ(modifier);
    }

    private int clampItemModifierX(int modifier) {
        return Math.max(-2, Math.min(modifier, Config.maxItemModifiersPerAxis));
    }

    private int clampItemModifierY(int modifier) {
        return Math.max(-1, Math.min(modifier, Config.maxItemModifiersPerAxis));
    }

    private int clampItemModifierZ(int modifier) {
        return Math.max(-2, Math.min(modifier, Config.maxItemModifiersPerAxis));
    }

    @Override
    public void serialize(ValueOutput out) {
        out.putInt("itemModifiersX", itemModifiersX);
        out.putInt("itemModifiersY", itemModifiersY);
        out.putInt("itemModifiersZ", itemModifiersZ);
    }

    @Override
    public void deserialize(ValueInput in) {
        this.itemModifiersX = clampItemModifierX(in.getIntOr("itemModifiersX", 0));
        this.itemModifiersY = clampItemModifierY(in.getIntOr("itemModifiersY", 0));
        this.itemModifiersZ = clampItemModifierZ(in.getIntOr("itemModifiersZ", 0));
    }

    public boolean hasItemModifications() {
        return itemModifiersX != 0 || itemModifiersY != 0 || itemModifiersZ != 0;
    }

    public void resetToDefaults() {
        this.itemModifiersX = 0;
        this.itemModifiersY = 0;
        this.itemModifiersZ = 0;
    }
}
