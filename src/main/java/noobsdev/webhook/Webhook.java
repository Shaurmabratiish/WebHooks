package noobsdev.webhook;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("webhook")
@Mod.EventBusSubscriber(modid = "webhook", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Webhook {

    public static final String WEBHOOK = "";
    public static Boolean world = false;
    public Webhook() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(chat::onReceiveMessage);
        MinecraftForge.EVENT_BUS.addListener(chat::onClientChat);

    }
    private void commonSetup(final FMLCommonSetupEvent event) {

    }
}
