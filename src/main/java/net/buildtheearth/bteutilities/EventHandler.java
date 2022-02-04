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

import com.google.common.collect.Maps;
import net.buildtheearth.bteutilities.gui.CustomGui;
import net.buildtheearth.bteutilities.gui.GuiCustomWrappedButton;
import net.buildtheearth.bteutilities.gui.GuiFakeMain;
import net.buildtheearth.bteutilities.gui.GuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

public class EventHandler {
    private static EventHandler instance = null;
    private final Map<Integer, GuiButton> loadedButtons = Maps.newHashMap();
    public long displayMs = -1;
    Field guiField;
    CustomGui actualGui;
    private boolean loadedMainMenuAssets = false;

    private EventHandler() {
        try {
            guiField = GuiScreenEvent.class.getDeclaredField("gui");
            guiField.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static EventHandler getInstance() {
        return instance == null ? instance = new EventHandler() : instance;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void openGui(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu) {
            if (loadedMainMenuAssets) {
                CustomGui customMainMenu = GuiHandler.getInstance().getGUI("mainmenu");
                if (customMainMenu != null) {
                    event.setGui(customMainMenu);
                }
            }
        } else if (event.getGui() instanceof CustomGui) {
            CustomGui custom = (CustomGui) event.getGui();

            CustomGui target = GuiHandler.getInstance().getGUI(custom.name);
            if (target != custom) {
                event.setGui(target);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void initGuiPostEarly(InitGuiEvent.Post event) {
        if (event.getGui() instanceof CustomGui) {
            CustomGui custom = (CustomGui) event.getGui();
            if (custom.name.equals("mainmenu")) {
                event.setButtonList(new ArrayList<>());
                actualGui = custom;
                try {
                    guiField.set(event, new GuiFakeMain());
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void initGuiPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu && !loadedMainMenuAssets) {
            for (GuiButton b : event.getButtonList()) {
                loadedButtons.put(b.id, b);
            }
            loadedMainMenuAssets = true;
            Minecraft.getMinecraft().displayGuiScreen(GuiHandler.getInstance().getGUI("mainmenu"));
            return;
        }
        if (event.getGui() instanceof GuiFakeMain) {
            for (GuiButton o : actualGui.getButtonList()) {
                if (o instanceof GuiCustomWrappedButton) {
                    GuiCustomWrappedButton b = (GuiCustomWrappedButton) o;
                    b.init(loadedButtons.get(b.wrappedButtonID));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderScreenPost(DrawScreenEvent.Post event) {
        if (displayMs != -1) {
            if (System.currentTimeMillis() - displayMs < 5000) {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Error loading config file, see console for more information", 0, 80, 16711680);
            } else {
                displayMs = -1;
            }
        }
    }
}
