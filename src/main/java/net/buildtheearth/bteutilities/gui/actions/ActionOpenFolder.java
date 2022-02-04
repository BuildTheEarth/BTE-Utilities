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

import java.awt.*;
import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public class ActionOpenFolder implements IAction {
    private final String folderName;

    @Override
    public void perform(Object source, CustomGui parent) {
        File toOpen = new File(Minecraft.getMinecraft().gameDir, folderName);

        boolean isInMinecraftFolder = false;
        try {
            File parentFile = toOpen.getCanonicalFile();
            while ((parentFile = parentFile.getParentFile()) != null) {
                if (parentFile.getCanonicalPath().equals(Minecraft.getMinecraft().gameDir.getCanonicalPath())) {
                    isInMinecraftFolder = true;
                }
            }

            if (isInMinecraftFolder) {
                if (toOpen.isDirectory() && Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(toOpen);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException ignored) {

        }

    }

}
