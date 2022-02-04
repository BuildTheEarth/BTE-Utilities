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

import net.buildtheearth.bteutilities.gui.elements.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Field;
import java.util.List;

public class GuiCustomLabel extends Gui {
    static Field mcpversionField;

    static {
        try {
            mcpversionField = Loader.class.getDeclaredField("mcpversion");
            mcpversionField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Label text;
    int posX, posY;
    FontRenderer fontRendererObj;
    CustomGui parent;
    boolean hovered;

    public GuiCustomLabel(CustomGui customGUI, Label text, int posX, int posY) {
        this.text = text;
        this.posX = posX;
        this.posY = posY;
        this.parent = customGUI;
        fontRendererObj = Minecraft.getMinecraft().fontRenderer;

        hovered = false;

        if (text.getText().equals("fml")) {
            StringBuilder string = new StringBuilder();
            List<String> brandings = FMLCommonHandler.instance().getBrandings(true);
            for (int i = 0; i < brandings.size(); i++) {
                String brd = brandings.get(i);
                if (!com.google.common.base.Strings.isNullOrEmpty(brd)) {
                    string.append(brd).append((i < brandings.size() - 1) ? "\n" : "");
                }
            }

            this.text.setText(string.toString());
            this.text.setHoverText(string.toString());
        }
    }

    public static GuiCustomLabel from(CustomGui parent, Label label) {
        return new GuiCustomLabel(parent, label, parent.modX(label.getPosX(), label.getAlignment()), parent.modY(label.getPosY(), label.getAlignment()));
    }

    public void drawLabel(int mouseX, int mouseY) {
        if (text.getFontSize() != 1F) {
            GlStateManager.translate(posX, posY, 0);
            GlStateManager.scale(text.getFontSize(), text.getFontSize(), 1);
            GlStateManager.translate(-posX, -posY, 0);
        }

        String toDraw = hovered ? text.getHoverText() : text.getText();

        hovered = isMouseAboveLabel(mouseX, mouseY);

        if (toDraw.contains("\n")) {
            int modY = 0;
            String[] lines = toDraw.split("\n");
            for (String line : lines) {

                int textWidth = fontRendererObj.getStringWidth(line);

                int offsetX = text.getAnchor() == Anchor.START ? 0 : (text.getAnchor() == Anchor.MIDDLE ? -(textWidth / 2) : (-textWidth));

                if (hovered) {
                    this.drawString(fontRendererObj, line, posX + offsetX, posY + modY, text.getHoverColor());
                } else {
                    this.drawString(fontRendererObj, line, posX + offsetX, posY + modY, text.getColor());
                }

                modY += fontRendererObj.FONT_HEIGHT;
            }
        } else {
            int textWidth = fontRendererObj.getStringWidth(toDraw);

            int offsetX = text.getAnchor() == Anchor.START ? 0 : (text.getAnchor() == Anchor.MIDDLE ? -(textWidth / 2) : (-textWidth));

            if (hovered) {
                this.drawString(fontRendererObj, toDraw, posX + offsetX, posY, text.getHoverColor());
            } else {
                this.drawString(fontRendererObj, toDraw, posX + offsetX, posY, text.getColor());
            }
        }

        if (text.getFontSize() != 1F) {
            GlStateManager.translate(posX, posY, 0);
            GlStateManager.scale(1 / text.getFontSize(), 1 / text.getFontSize(), 1);
            GlStateManager.translate(-posX, -posY, 0);
        }
    }

    private boolean isMouseAboveLabel(int mouseX, int mouseY) {
        String stringText = this.text.getText();

        if (stringText == null) {
            return false;
        }

        if (stringText.contains("\n")) {
            String[] lines = stringText.split("\n");

            for (int i = 0; i < lines.length; i++) {
                int width = this.fontRendererObj.getStringWidth(lines[i]);
                int height = this.fontRendererObj.FONT_HEIGHT;

                int modX = 0;

                switch (text.getAnchor()) {
                    case END:
                        modX = -width;
                        break;
                    case MIDDLE:
                        modX = -width / 2;
                        break;
                    default:
                        break;
                }

                if (mouseX >= posX + modX && mouseY >= posY + this.fontRendererObj.FONT_HEIGHT * i && mouseX < posX + width + modX && mouseY < posY + this.fontRendererObj.FONT_HEIGHT * i + height) {
                    return true;
                }
            }

            return false;
        } else {
            int width = this.fontRendererObj.getStringWidth(stringText);
            int height = this.fontRendererObj.FONT_HEIGHT;

            // Anchor Difference
            int modX = 0;

            switch (text.getAnchor()) {
                case END:
                    modX = -width;
                    break;
                case MIDDLE:
                    modX = -width / 2;
                    break;
                default:
                    break;
            }

            return mouseX >= posX + modX && mouseY >= posY && mouseX < posX + width + modX && mouseY < posY + height;
        }
    }

    public void mouseClicked(int mouseX, int mouseY) {
        boolean flag = isMouseAboveLabel(mouseX, mouseY);

        if (flag && text.getAction() != null) {
            Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("ui.button.click"), SoundCategory.MASTER, 1F, 1F, false, 0, AttenuationType.NONE, 0, 0, 0));
            text.getAction().perform(this.text, parent);
        }
    }
}
