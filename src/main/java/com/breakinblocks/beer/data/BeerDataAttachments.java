package com.breakinblocks.beer.data;

import com.breakinblocks.beer.Beer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BeerDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Beer.MODID);

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantingTableRangeData> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EnchantingTableRangeData::getItemModifiersX,
            ByteBufCodecs.VAR_INT, EnchantingTableRangeData::getItemModifiersY,
            ByteBufCodecs.VAR_INT, EnchantingTableRangeData::getItemModifiersZ,
            EnchantingTableRangeData::new
        );

    public static final Supplier<AttachmentType<EnchantingTableRangeData>> ENCHANTING_TABLE_RANGE =
        ATTACHMENT_TYPES.register("enchanting_table_range", () ->
            AttachmentType.serializable(EnchantingTableRangeData::new)
                .sync(STREAM_CODEC)
                .copyOnDeath()
                .build()
        );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
