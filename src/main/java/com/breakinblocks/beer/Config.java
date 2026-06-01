package com.breakinblocks.beer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Beer.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_ITEM_MODIFIERS = BUILDER
            .comment("Enable item-based enchanting table modifications. Default: true")
            .define("enableItemModifiers", true);

    public static final ModConfigSpec.IntValue MODIFIER_AMOUNT_PER_USE = BUILDER
            .comment("Amount each item use modifies the range. Default: 1")
            .defineInRange("modifierAmountPerUse", 1, 1, 10);

    public static final ModConfigSpec.IntValue MAX_ITEM_MODIFIERS_PER_AXIS = BUILDER
            .comment("Maximum item modifiers allowed per axis (positive or negative). Default: 5")
            .defineInRange("maxItemModifiersPerAxis", 5, 1, 32);

    public static final ModConfigSpec.BooleanValue CONSUME_ITEMS = BUILDER
            .comment("Whether items are consumed when modifying enchanting tables. Default: true")
            .define("consumeItems", true);

    public static final ModConfigSpec.BooleanValue ENABLE_XP_COSTS = BUILDER
            .comment("Enable XP costs for item modifiers. Increasing costs XP, decreasing gives XP back. Default: false")
            .define("enableXpCosts", false);

    public static final ModConfigSpec.IntValue XP_COST_PER_MODIFIER = BUILDER
            .comment("XP cost per modifier level. Increasing costs this much XP, decreasing gives back this much XP. Default: 50")
            .defineInRange("xpCostPerModifier", 50, 1, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableItemModifiers;
    public static int modifierAmountPerUse;
    public static int maxItemModifiersPerAxis;
    public static boolean consumeItems;
    public static boolean enableXpCosts;
    public static int xpCostPerModifier;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableItemModifiers = ENABLE_ITEM_MODIFIERS.get();
        modifierAmountPerUse = MODIFIER_AMOUNT_PER_USE.get();
        maxItemModifiersPerAxis = MAX_ITEM_MODIFIERS_PER_AXIS.get();
        consumeItems = CONSUME_ITEMS.get();
        enableXpCosts = ENABLE_XP_COSTS.get();
        xpCostPerModifier = XP_COST_PER_MODIFIER.get();
    }
}
