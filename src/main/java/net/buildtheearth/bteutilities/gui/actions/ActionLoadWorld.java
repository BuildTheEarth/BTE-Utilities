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
import net.buildtheearth.bteutilities.BTEUtilities;
import net.buildtheearth.bteutilities.gui.CustomGui;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiOldSaveLoadConfirm;
import net.minecraftforge.fml.common.StartupQuery;

import java.io.File;
import java.io.FileInputStream;

@RequiredArgsConstructor
public class ActionLoadWorld implements IAction {
    private final String dirName;
    private final String saveName;

    @Override
    public void perform(Object source, CustomGui menu) {
        File dir = new File(FMLClientHandler.instance().getSavesDir(), dirName);
        NBTTagCompound level;
        try {
            level = CompressedStreamTools.readCompressed(new FileInputStream(new File(dir, "level.dat")));
        } catch (Exception e) {
            try {
                level = CompressedStreamTools.readCompressed(new FileInputStream(new File(dir, "level.dat_old")));
            } catch (Exception e1) {
                BTEUtilities.getLogger().warn("There appears to be a problem loading the save {}, both level files are unreadable.", dirName);
                return;
            }
        }
        NBTTagCompound fmlData = level.getCompoundTag("FML");
        if (fmlData.hasKey("ModItemData")) {
            FMLClientHandler.instance().showGuiScreen(new GuiOldSaveLoadConfirm(dirName, saveName, menu));
        } else {
            try {
                Minecraft.getMinecraft().launchIntegratedServer(dirName, saveName, null);
            } catch (StartupQuery.AbortedException ignored) {
            }
        }
    }

}
