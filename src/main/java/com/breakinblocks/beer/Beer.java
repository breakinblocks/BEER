package com.breakinblocks.beer;

import com.breakinblocks.beer.network.BeerNetworkHandler;
import com.breakinblocks.beer.recipe.BeerRecipes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Beer.MODID)
public class Beer {
    public static final String MODID = "beer";
    public static final Logger LOGGER = LogManager.getLogger();

    public Beer() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(
            net.minecraftforge.fml.config.ModConfig.Type.COMMON,
            BeerConfig.COMMON_SPEC
        );

        BeerRecipes.register(modEventBus);
        BeerNetworkHandler.register();
    }
}
