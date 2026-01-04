package com.breakinblocks.beer.capability;

import net.minecraft.nbt.CompoundTag;

public class EnchantingTableRangeCapability implements IEnchantingTableRangeData {
    private int modX;
    private int modY;
    private int modZ;

    @Override
    public int getItemModifiersX() { return modX; }
    @Override
    public int getItemModifiersY() { return modY; }
    @Override
    public int getItemModifiersZ() { return modZ; }

    @Override
    public void setItemModifiersX(int value) { this.modX = value; }
    @Override
    public void setItemModifiersY(int value) { this.modY = value; }
    @Override
    public void setItemModifiersZ(int value) { this.modZ = value; }

    @Override
    public void addItemModifierX(int amount) { this.modX += amount; }
    @Override
    public void addItemModifierY(int amount) { this.modY += amount; }
    @Override
    public void addItemModifierZ(int amount) { this.modZ += amount; }

    @Override
    public void copyFrom(IEnchantingTableRangeData other) {
        this.modX = other.getItemModifiersX();
        this.modY = other.getItemModifiersY();
        this.modZ = other.getItemModifiersZ();
    }

    @Override
    public int getEffectiveRangeX(int base) { return base + modX; }
    @Override
    public int getEffectiveRangeY(int base) { return base + modY; }
    @Override
    public int getEffectiveRangeZ(int base) { return base + modZ; }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("modX", modX);
        tag.putInt("modY", modY);
        tag.putInt("modZ", modZ);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        modX = nbt.getInt("modX");
        modY = nbt.getInt("modY");
        modZ = nbt.getInt("modZ");
    }
}
