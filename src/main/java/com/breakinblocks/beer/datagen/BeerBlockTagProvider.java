package com.breakinblocks.beer.datagen;

import com.breakinblocks.beer.Beer;
import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BeerBlockTagProvider extends BlockTagsProvider {
    public BeerBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Beer.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(Beer.ENCHANTING_TABLES_BLOCK_TAG)
                .add(Blocks.ENCHANTING_TABLE)
                .addOptional(ApothicEnchanting.loc("apothic_enchanting_table"))
                .addOptional(ApothicEnchanting.loc("raven_enchanting_table"));
    }
}
