package com.breakinblocks.beer.datagen;

import com.breakinblocks.beer.Beer;
import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BeerItemTagsProvider extends ItemTagsProvider {
    public BeerItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Beer.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(Beer.ENCHANTING_TABLES_ITEM_TAG)
                .add(Blocks.ENCHANTING_TABLE.asItem())
                .addOptional(ApothicEnchanting.loc("apothic_enchanting_table"))
                .addOptional(ApothicEnchanting.loc("raven_enchanting_table"));
    }
}
