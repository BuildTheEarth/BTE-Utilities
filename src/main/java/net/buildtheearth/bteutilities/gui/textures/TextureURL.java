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

package net.buildtheearth.bteutilities.gui.textures;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.bteutilities.BTEUtilities;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TextureURL implements ITexture {
    @Getter
    private URL url;
    @Setter
    private int textureID;
    @Setter
    private BufferedImage bi;

    public TextureURL(String url) {
        this.textureID = -1;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            BTEUtilities.getLogger().log(Level.ERROR, "Invalid URL: " + url);
            e.printStackTrace();
        }
        new LoadTextureURL(this).start();
    }

    @Override
    public void bind() {
        if (this.textureID != -1) {
            GlStateManager.bindTexture(this.textureID);
        } else {
            if (bi != null) {
                setTextureID(TextureUtil.uploadTextureImageAllocate(GL11.glGenTextures(), bi, false, false));
                bind();
                return;
            }
            BTEUtilities.bindTransparent();
        }
    }

    public static class LoadTextureURL extends Thread {
        private final TextureURL texture;

        public LoadTextureURL(TextureURL texture) {
            this.texture = texture;
            this.setDaemon(true);
        }

        @Override
        public void run() {
            BufferedImage bi = null;
            try {
                bi = ImageIO.read(texture.getUrl());
            } catch (IOException ignored) {
            }

            if (bi != null) {
                texture.setBi(bi);
            }
        }
    }
}
