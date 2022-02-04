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

import lombok.NonNull;
import net.buildtheearth.bteutilities.gui.elements.Button;
import net.buildtheearth.bteutilities.gui.textures.ITexture;
import net.buildtheearth.bteutilities.gui.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

import java.util.ArrayList;
import java.util.List;

public class GuiCustomButton extends GuiButton {
    public final Button b;
    private final ITexture texture;
    private final int normalText;
    private final int hoverText;

    public GuiCustomButton(int buttonId, Button b) {
        super(buttonId, b.getPosX(), b.getPosY(), b.getWidth(), b.getHeight(), I18n.format(b.getText()));

        this.texture = b.getTexture();
        this.normalText = b.getNormalTextColor();
        this.hoverText = b.getHoverTextColor();
        this.b = b;
    }

    public void drawTooltip(Minecraft mc, int mouseX, int mouseY) {
        FontRenderer fontrenderer = mc.fontRenderer;
        if (hovered && this.b.getTooltip() != null) {
            this.drawHoveringText(mc, getTooltip(this.b.getTooltip()), mouseX, mouseY, fontrenderer);
        }
    }

    @SuppressWarnings("unused")
    public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            boolean newHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            if (newHovered && !this.hovered && b.getHoverSound() != null) {
                Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(b.getHoverSound()), SoundCategory.MASTER, 1F, 1F, false, 0, AttenuationType.NONE, 0, 0, 0));
            }

            this.hovered = newHovered;
            int k = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);

            if (this.texture != null) {
                texture.bind();

                RenderUtil.drawPartialImage(this.x, this.y, 0, (k - 1) * b.getImageHeight(), b.getWidth(), b.getHeight(), b.getImageWidth(), b.getImageHeight());
            } else {
                mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
                this.drawTexturedModalRect(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
            }

            this.mouseDragged(mc, mouseX, mouseY);
            int l = normalText;

            if (packedFGColour != 0) {
                l = packedFGColour;
            } else if (!this.enabled) {
                l = 10526880;
            } else if (this.hovered) {
                l = hoverText;
            }

            this.drawCenteredString(fontrenderer, hovered ? I18n.format(b.getHoverText()) : I18n.format(b.getText()), this.x + this.width / 2 + b.getTextOffsetX(), this.y + (this.height - 8) / 2 + b.getTextOffsetY(), l, b.isShadow());
        }
    }

    protected void drawHoveringText(Minecraft mc, List<String> textLines, int x, int y, FontRenderer font) {
        if (!textLines.isEmpty()) {
            if (mc.currentScreen == null) {
                return;
            }
            int width = mc.currentScreen.width;
            int height = mc.currentScreen.height;
            GlStateManager.disableDepth();
            int k = 0;

            for (String s : textLines) {
                int l = font.getStringWidth(s);
                if (l > k) {
                    k = l;
                }
            }

            int j2 = x + 12;
            int k2 = y - 12;
            int i1 = 8;

            if (textLines.size() > 1) {
                i1 += 2 + (textLines.size() - 1) * 10;
            }

            if (j2 + k > width) {
                j2 -= 28 + k;
            }

            if (k2 + i1 + 6 > height) {
                k2 = this.height - i1 - 6;
            }

            this.zLevel = 300.0F;
            int j1 = -267386864;
            this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < textLines.size(); ++i2) {
                String s1 = textLines.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0) {
                    k2 += 2;
                }

                k2 += 10;
            }

            this.zLevel = 0.0F;
            GlStateManager.enableDepth();
        }
    }

    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color, boolean shadow) {
        if (shadow) {
            fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y, color);
        } else {
            fontRendererIn.drawString(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y, color, false);
        }
    }

    @Override
    public void playPressSound(@NonNull SoundHandler soundHandlerIn) {
        if (b.getPressSound() != null) {
            soundHandlerIn.playSound(new PositionedSoundRecord(new ResourceLocation(b.getPressSound()), SoundCategory.MASTER, 1F, 1F, false, 0, AttenuationType.NONE, 0, 0, 0));
        } else {
            super.playPressSound(soundHandlerIn);
        }
    }

    private ArrayList<String> getTooltip(String tooltipString) {
        ArrayList<String> tooltip = new ArrayList<>();
        String[] split = tooltipString.split("\n");
        for (String s : split) {
            tooltip.add(I18n.format(s));
        }
        return tooltip;
    }
}
