package com.breakinblocks.beer;

import com.breakinblocks.beer.data.BeerDataAttachments;
import com.breakinblocks.beer.recipe.BeerRecipes;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Beer.MODID)
public class Beer {
    public static final String MODID = "beer";

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    public Beer(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        BeerDataAttachments.register(modEventBus);
        BeerRecipes.register(modEventBus);
    }
}
