package com.breakinblocks.beer.datagen;

import com.breakinblocks.beer.Beer;
import net.minecraft.core.HolderLookup;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Beer.MODID)
public class BeerDatagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        event.createProvider((output) -> new BeerRecipeProvider.Runner(output, lookupProvider));
    }
}
