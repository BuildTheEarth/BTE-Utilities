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

package net.buildtheearth.bteutilities.gui.actions;

import lombok.RequiredArgsConstructor;
import net.buildtheearth.bteutilities.gui.CustomGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiCustomizeSkin;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiSnooper;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.realms.RealmsBridge;
import net.minecraftforge.fml.client.GuiModList;

@RequiredArgsConstructor
public class ActionOpenGUI implements IAction {
    private final GUI guiType;

    @Override
    public void perform(Object source, CustomGui menu) {
        GuiScreen gui = null;
        switch (guiType) {
            case MODS:
                gui = new GuiModList(menu);
                break;
            case SINGLEPLAYER:
                gui = new GuiWorldSelection(menu);
                break;
            case SINGEPLAYER_CREATE:
                gui = new GuiCreateWorld(menu);
                break;
            case MULTIPLAYER:
                gui = new GuiMultiplayer(menu);
                break;
            case OPTIONS:
                gui = new GuiOptions(menu, menu.mc.gameSettings);
                break;
            case LANGUAGES:
                gui = new GuiLanguage(menu, menu.mc.gameSettings, menu.mc.getLanguageManager());
                break;
            case RESOURCE_PACKS:
                gui = new GuiScreenResourcePacks(menu);
                break;
            case SNOOPER:
                gui = new GuiSnooper(menu, menu.mc.gameSettings);
                break;
            case SOUNDS:
                gui = new GuiScreenOptionsSounds(menu, menu.mc.gameSettings);
                break;
            case SKIN:
                gui = new GuiCustomizeSkin(menu);
                break;
            case VIDEO:
                gui = new GuiVideoSettings(menu, menu.mc.gameSettings);
                break;
            case CONTROLS:
                gui = new GuiControls(menu, menu.mc.gameSettings);
                break;
            case CHAT:
                gui = new ScreenChatOptions(menu, menu.mc.gameSettings);
                break;
            case REALMS:
                RealmsBridge realmsbridge = new RealmsBridge();
                GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                if (screen != null) {
                    realmsbridge.switchToRealms(screen);
                }
                break;
            case CREDITS:
                gui = new GuiWinGame(false, () -> {});
                break;
        }

        if (gui != null) {
            Minecraft.getMinecraft().displayGuiScreen(gui);
        }
    }

    public enum GUI {
        MODS, SINGLEPLAYER, SINGEPLAYER_CREATE, MULTIPLAYER, OPTIONS, LANGUAGES, RESOURCE_PACKS, SNOOPER, SOUNDS, SKIN, VIDEO, CONTROLS, CHAT, REALMS, CREDITS
    }
}
