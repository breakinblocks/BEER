package com.breakinblocks.beer;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BeerConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue rangeX;
        public final ForgeConfigSpec.IntValue rangeY;
        public final ForgeConfigSpec.IntValue rangeZ;

        // Item modifier configuration
        public final ForgeConfigSpec.BooleanValue enableItemModifiers;
        public final ForgeConfigSpec.IntValue modifierAmountPerUse;
        public final ForgeConfigSpec.IntValue maxItemModifiersPerAxis;
        public final ForgeConfigSpec.BooleanValue consumeItems;

        // XP cost configuration
        public final ForgeConfigSpec.BooleanValue enableXpCosts;
        public final ForgeConfigSpec.IntValue xpCostPerModifier;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("range");
            rangeX = builder
                .comment("Default Range for X axis (min 2, max 16)")
                .defineInRange("x", 2, 2, 16);
            rangeY = builder
                .comment("Default Range for Y axis (min 2, max 16)")
                .defineInRange("y", 2, 2, 16);
            rangeZ = builder
                .comment("Default Range for Z axis (min 2, max 16)")
                .defineInRange("z", 2, 2, 16);
            builder.pop();

            builder.push("item_modifiers");
            enableItemModifiers = builder
                    .comment("Enable item-based enchanting table modifications. Default: true")
                    .define("enableItemModifiers", true);
            modifierAmountPerUse = builder
                    .comment("Amount each item use modifies the range. Default: 1")
                    .defineInRange("modifierAmountPerUse", 1, 1, 10);
            maxItemModifiersPerAxis = builder
                    .comment("Maximum item modifiers allowed per axis (positive or negative). Default: 5")
                    .defineInRange("maxItemModifiersPerAxis", 5, 1, 32);
            consumeItems = builder
                    .comment("Whether items are consumed when modifying enchanting tables. Default: true")
                    .define("consumeItems", true);
            builder.pop();

            builder.push("xp_costs");
            enableXpCosts = builder
                    .comment("Enable XP costs for item modifiers. Increasing costs XP, decreasing gives XP back. Default: false")
                    .define("enableXpCosts", false);
            xpCostPerModifier = builder
                    .comment("XP cost per modifier level. Increasing costs this much XP, decreasing gives back this much XP. Default: 50")
                    .defineInRange("xpCostPerModifier", 50, 1, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static int getRangeX() {
        return COMMON.rangeX.get();
    }
    public static int getRangeY() {
        return COMMON.rangeY.get();
    }
    public static int getRangeZ() {
        return COMMON.rangeZ.get();
    }

    public static boolean isEnableItemModifiers() {
        return COMMON.enableItemModifiers.get();
    }
    public static int getModifierAmountPerUse() {
        return COMMON.modifierAmountPerUse.get();
    }
    public static int getMaxItemModifiersPerAxis() {
        return COMMON.maxItemModifiersPerAxis.get();
    }
    public static boolean isConsumeItems() {
        return COMMON.consumeItems.get();
    }
    public static boolean isEnableXpCosts() {
        return COMMON.enableXpCosts.get();
    }
    public static int getXpCostPerModifier() {
        return COMMON.xpCostPerModifier.get();
    }
}