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

import net.buildtheearth.bteutilities.gui.Alignment;
import net.buildtheearth.bteutilities.gui.CustomGui;
import net.buildtheearth.bteutilities.gui.actions.ActionOpenGUI;
import net.buildtheearth.bteutilities.gui.actions.ActionOpenLink;
import net.buildtheearth.bteutilities.gui.actions.ActionQuit;
import net.buildtheearth.bteutilities.gui.elements.Background;
import net.buildtheearth.bteutilities.gui.elements.Button;
import net.buildtheearth.bteutilities.gui.elements.Image;
import net.buildtheearth.bteutilities.gui.elements.Label;
import net.buildtheearth.bteutilities.gui.elements.Slideshow;
import net.buildtheearth.bteutilities.gui.textures.ITexture;
import net.buildtheearth.bteutilities.gui.textures.TextureResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

/**
 * @author Noah Husby
 */
public final class TitleScreen extends CustomGui {

    private Slideshow slideshow;
    private Label credit;

    public TitleScreen() {
        super("mainmenu");
    }

    @Override
    protected void setup() {
        ITexture[] backgrounds = new ITexture[5];
        String[] credits = new String[5];
        // Load credits and images
        ModpackPicture[] modpackPictures = ModpackAPI.getModpackPictures();
        for (int i = 0; i < modpackPictures.length; i++) {
            ModpackPicture picture = modpackPictures[i];
            backgrounds[i] = picture.getImage();
            //Sometimes credit shows up, weird bug
            credits[i] = picture.getCredit();
        }
        Image title = new Image(-137, 40, 512, 512, Alignment.top_center);
        ITexture titleTexture = ModpackAPI.getLogo().getImage();
        title.setImage(titleTexture);
        title.setHoverImage(titleTexture);
        images.add(title);

        Button singleplayer = new Button("menu.singleplayer", -100, 48, 200, 20);
        singleplayer.setTexture(new TextureResourceLocation("bteutilities:buttons/transparent.png"));
        singleplayer.setImageWidth(1000);
        singleplayer.setImageHeight(100);
        singleplayer.setAction(new ActionOpenGUI(ActionOpenGUI.GUI.SINGLEPLAYER));
        buttons.add(singleplayer);

        Button multiplayer = new Button("menu.multiplayer", -100, 72, 200, 20);
        multiplayer.setTexture(new TextureResourceLocation("bteutilities:buttons/transparent.png"));
        multiplayer.setImageWidth(1000);
        multiplayer.setImageHeight(100);
        multiplayer.setAction(new ActionOpenGUI(ActionOpenGUI.GUI.MULTIPLAYER));
        buttons.add(multiplayer);

        Button mods = new Button("fml.menu.mods", -100, 96, 98, 20);
        mods.setTexture(new TextureResourceLocation("bteutilities:buttons/transparent.png"));
        mods.setImageWidth(1000);
        mods.setImageHeight(100);
        mods.setAction(new ActionOpenGUI(ActionOpenGUI.GUI.MODS));
        buttons.add(mods);

        Button map = new Button("Build Teams", 2, 96, 98, 20);
        map.setTexture(new TextureResourceLocation("bteutilities:buttons/transparent.png"));
        map.setImageWidth(1000);
        map.setImageHeight(100);
        map.setAction(new ActionOpenLink("https://buildtheearth.net/buildteams"));
        buttons.add(map);

        Button options = new Button("menu.options", -100, 132, 98, 20);
        options.setTexture(new TextureResourceLocation("bteutilities:buttons/transparent.png"));
        options.setImageWidth(1000);
        options.setImageHeight(100);
        options.setAction(new ActionOpenGUI(ActionOpenGUI.GUI.OPTIONS));
        buttons.add(options);

        Button quit = new Button("menu.quit", 2, 132, 98, 20);
        quit.setTexture(new TextureResourceLocation("bteutilities:buttons/transparent.png"));
        quit.setImageWidth(1000);
        quit.setImageHeight(100);
        quit.setAction(new ActionQuit());
        buttons.add(quit);

        if (Loader.isModLoaded("replaymod")) {
            Button replay = new Button("", 105, 132, 20, 20);
            replay.setTexture(new TextureResourceLocation("bteutilities:buttons/replay.png"));
            replay.setImageWidth(300);
            replay.setImageHeight(300);
            replay.setWrappedButtonID(17890234);
            buttons.add(replay);
        }

        Label mojang = new Label("Copyright Mojang AB. Do not distribute!", -200, -10, Alignment.bottom_right, -1);
        mojang.setHoverText(">Copyright Mojang AB. Do not distribute!");
        mojang.setAction(new ActionOpenGUI(ActionOpenGUI.GUI.CREDITS));
        labels.add(mojang);

        Label bte = new Label("BuildTheEarth.net", 2, -30, Alignment.bottom_left, -1);
        bte.setHoverText(">BuildTheEarth.net");
        bte.setAction(new ActionOpenLink("https://buildtheearth.net/"));
        labels.add(bte);

        Label version = new Label("BTE Modpack v" + BTEUtilities.getInstance().getMajorVersion(), 2, -20, Alignment.bottom_left, -1);
        labels.add(version);

        credit = new Label(credits[0], 2, -10, Alignment.bottom_left, -1);
        labels.add(credit);

        background = new Background(null);
        slideshow = new Slideshow(backgrounds);
        slideshow.setDisplayDuration(100);
        slideshow.setFadeDuration(15);
        background.setSlideshow(slideshow);
    }

    @Override
    protected void onSlideshowFade() {
        String currentCredit = ModpackAPI.getModpackPictures()[slideshow.getIndex()].getCredit();
        credit.setText(currentCredit);
        credit.setHoverText(currentCredit);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(Minecraft.getMinecraft().gameDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
