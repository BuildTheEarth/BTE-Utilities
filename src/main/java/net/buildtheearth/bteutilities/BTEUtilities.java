/*
 *  MIT License
 *
 *  Copyright (c) 2021-2022 BuildTheEarth.net
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 *  modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 *  is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.buildtheearth.bteutilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.buildtheearth.bteutilities.discord.DiscordHandler;
import net.buildtheearth.bteutilities.gui.GuiHandler;
import net.buildtheearth.bteutilities.gui.util.IconLoader;
import net.buildtheearth.bteutilities.network.BTENetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Mod(modid = BTEUtilities.MOD_ID, useMetadata = true)
public class BTEUtilities {

    public static final String MOD_ID = "bteutilities";
    // GSON is outdated in forge, must use old parser
    public static final JsonParser parser = new JsonParser();
    public static final Gson gson = new GsonBuilder().create();

    @Mod.Instance(BTEUtilities.MOD_ID)
    @Getter
    private static BTEUtilities instance;

    @Getter
    private static Logger logger;

    public File defaultImageFolder;
    public File dynamicImageFolder;

    @Getter
    private String majorVersion;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Display.setTitle("Minecraft 1.12.2 - BuildTheEarth");
        Display.setIcon(IconLoader.load("assets/bteutilities/icons/bte.png"));
        String[] splitVersion = event.getModMetadata().version.split("\\.");
        majorVersion = splitVersion[0] + "." + splitVersion[1];

        defaultImageFolder = new File(new File(Minecraft.getMinecraft().gameDir, "buildtheearth"), "default");
        dynamicImageFolder = new File(new File(Minecraft.getMinecraft().gameDir, "buildtheearth"), "dynamic");
        defaultImageFolder.mkdir();
        dynamicImageFolder.mkdir();
        MinecraftForge.EVENT_BUS.register(EventHandler.getInstance());
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(Config.class);
        MinecraftForge.EVENT_BUS.register(getInstance());
        DiscordHandler.getInstance().configChanged();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModpackAPI.load();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new ModpackAPI(), 0, 10, TimeUnit.MINUTES);
        BTENetworkHandler.registerHandlers();
    }

    @SubscribeEvent
    public void onPlayerQuit(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        DiscordHandler.getInstance().setState(null);
    }

    public void reload() {
        try {
            GuiHandler.getInstance().loadTitleScreen();
            EventHandler.getInstance().displayMs = -1;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.getInstance().displayMs = System.currentTimeMillis();
            logger.log(Level.ERROR, "Error while loading new config file, trying to keep the old one loaded.");
        }
    }
}
