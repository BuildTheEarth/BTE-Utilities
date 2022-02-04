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

package net.buildtheearth.bteutilities.gui;

import net.buildtheearth.bteutilities.gui.elements.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiCustomWrappedButton extends GuiCustomButton {
    public final int wrappedButtonID;
    GuiButton wrappedButton;

    public GuiCustomWrappedButton(int buttonId, int wrappedButtonID, Button b) {
        super(buttonId, b);
        this.wrappedButtonID = wrappedButtonID;
    }

    @SuppressWarnings("unused")
    @Override
    public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (wrappedButton != null) {
            this.visible = this.enabled = true;
        } else {
            this.visible = this.enabled = false;
        }
        super.func_191745_a(mc, mouseX, mouseY, partialTicks);
    }

    public void init(GuiButton wrappedButton) {
        this.wrappedButton = wrappedButton;
        if (wrappedButton == null) {
            this.visible = this.enabled = false;
        }
    }
}
