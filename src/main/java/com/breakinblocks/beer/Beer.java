package com.breakinblocks.beer;

import com.breakinblocks.beer.data.BeerDataAttachments;
import com.breakinblocks.beer.recipe.BeerRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.List;

@Mod(Beer.MODID)
public class Beer {
    public static final String MODID = "beer";

    public static final TagKey<Block> ENCHANTING_TABLES_BLOCK_TAG = TagKey.create(Registries.BLOCK, rl("enchanting_tables"));
    public static final TagKey<Item> ENCHANTING_TABLES_ITEM_TAG = TagKey.create(Registries.ITEM, rl("enchanting_tables"));

    public static List<ItemStack> getEnchantingTablesBlockTag() {
        return BuiltInRegistries.BLOCK.getTag(Beer.ENCHANTING_TABLES_BLOCK_TAG)
                .map(tag -> tag.stream().map(holder -> new ItemStack(holder.value())).toList())
                .orElse(List.of(new ItemStack(Blocks.ENCHANTING_TABLE)));
    }

    public static final ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Beer(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
        BeerDataAttachments.register(modEventBus);
        
        BeerRecipes.register(modEventBus);
        
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        var recipeManager = event.getServer().getRecipeManager();
        var recipeType = BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();

        if (recipeType != null) {
            var recipes = recipeManager.getAllRecipesFor(recipeType);
            recipes.forEach(recipeHolder -> {
            });
        }
    }

}
