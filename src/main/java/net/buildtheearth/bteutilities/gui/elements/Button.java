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

package net.buildtheearth.bteutilities.gui.elements;

import lombok.Data;
import lombok.NonNull;
import net.buildtheearth.bteutilities.gui.Alignment;
import net.buildtheearth.bteutilities.gui.actions.IAction;
import net.buildtheearth.bteutilities.gui.textures.ITexture;

@Data
public class Button {
    private String text;
    private String hoverText;
    private IAction action = null;
    private String tooltip = null;

    private Alignment alignment;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private int imageWidth;
    private int imageHeight;

    private ITexture texture = null;

    private int normalTextColor = 14737632;
    private int hoverTextColor = 16777120;
    private boolean shadow = true;

    private String pressSound;
    private String hoverSound;

    private int textOffsetX = 0;
    private int textOffsetY = 0;

    private int wrappedButtonID = -1;

    public Button(@NonNull String text, int posX, int posY, int width, int height, @NonNull Alignment alignment) {
        this.text = text;
        this.hoverText = text;

        this.posX = posX;
        this.posY = posY;
        this.width = this.imageWidth = width;
        this.height = this.imageHeight = height;
        this.alignment = alignment;
    }

    public Button(@NonNull String text, int posX, int posY, int width, int height) {
        this(text, posX, posY, width, height, Alignment.button);
    }
}
