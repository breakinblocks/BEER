package com.breakinblocks.beer;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Beer.MODID)
public class Beer {
    public static final String MODID = "beer";
    public static final Logger LOGGER = LogManager.getLogger();

    public Beer() {
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(
            net.minecraftforge.fml.config.ModConfig.Type.COMMON,
            BeerConfig.COMMON_SPEC
        );
    }
}
