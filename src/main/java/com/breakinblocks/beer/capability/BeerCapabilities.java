package com.breakinblocks.beer.capability;

import com.breakinblocks.beer.Beer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class BeerCapabilities {

    @Mod.EventBusSubscriber(modid = Beer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.register(IEnchantingTableRangeData.class);
        }
    }

    @Mod.EventBusSubscriber(modid = Beer.MODID)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
            if (event.getObject() instanceof EnchantmentTableBlockEntity) {
                event.addCapability(new ResourceLocation(Beer.MODID, "range_data"), new EnchantingTableRangeCapabilityProvider());
            }
        }
    }
}
