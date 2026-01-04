package com.breakinblocks.beer.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IEnchantingTableRangeData extends INBTSerializable<CompoundTag> {
    int getItemModifiersX();
    int getItemModifiersY();
    int getItemModifiersZ();

    void setItemModifiersX(int value);
    void setItemModifiersY(int value);
    void setItemModifiersZ(int value);

    void addItemModifierX(int amount);
    void addItemModifierY(int amount);
    void addItemModifierZ(int amount);

    void copyFrom(IEnchantingTableRangeData other);
    
    int getEffectiveRangeX(int base);
    int getEffectiveRangeY(int base);
    int getEffectiveRangeZ(int base);
}
