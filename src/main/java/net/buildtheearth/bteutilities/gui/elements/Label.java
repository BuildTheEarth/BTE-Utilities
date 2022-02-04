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
import net.buildtheearth.bteutilities.gui.Anchor;
import net.buildtheearth.bteutilities.gui.actions.IAction;

@Data
public class Label {
    private final int posX;
    private final int posY;
    private final int color;
    private final int hoverColor;
    private final Alignment alignment;
    private String text;
    private String hoverText;
    private IAction action;
    private float fontSize = 1;
    private Anchor anchor = Anchor.START;

    public Label(@NonNull String text, int posX, int posY, @NonNull Alignment alignment, int color) {
        this.text = this.hoverText = text;
        this.posX = posX;
        this.posY = posY;
        this.color = this.hoverColor = color;
        this.alignment = alignment;
    }
}
