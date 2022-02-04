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
import net.buildtheearth.bteutilities.BTEUtilities;
import net.buildtheearth.bteutilities.gui.actions.ActionOpenLink;
import net.buildtheearth.bteutilities.gui.elements.Background;
import net.buildtheearth.bteutilities.gui.elements.Button;
import net.buildtheearth.bteutilities.gui.elements.Image;
import net.buildtheearth.bteutilities.gui.elements.Label;
import net.buildtheearth.bteutilities.gui.util.RenderUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public abstract class CustomGui extends GuiScreen implements GuiYesNoCallback {
    public final String name;

    protected final ArrayList<Button> buttons = new ArrayList<>();
    protected final ArrayList<Label> labels = new ArrayList<>();
    protected final ArrayList<Image> images = new ArrayList<>();
    public Background background = null;
    public Object beingChecked;

    ArrayList<GuiCustomLabel> textLabels;
    private boolean updating = false;

    public CustomGui(String name) {
        super();
        this.name = name;
        setup();
    }

    protected abstract void setup();

    protected void onSlideshowFade() {

    }

    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }

    @Override
    public void initGui() {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();

        textLabels = new ArrayList<>();
        int idCounter = 6000;

        // Add Custom Buttons
        for (Button b : buttons) {
            if (b.getWrappedButtonID() != -1) {
                this.buttonList.add(alignButton(b, new GuiCustomWrappedButton(b.getWrappedButtonID(), b.getWrappedButtonID(), b)));
            } else {
                this.buttonList.add(alignButton(b, new GuiCustomButton(idCounter, b)));
                idCounter++;
            }
        }

        labels.forEach(t -> textLabels.add(GuiCustomLabel.from(this, t)));
    }

    private GuiCustomButton alignButton(Button configButton, GuiCustomButton guiButton) {
        if (configButton != null) {
            guiButton.x = modX(configButton.getPosX(), configButton.getAlignment());
            guiButton.y = modY(configButton.getPosY(), configButton.getAlignment());
        }
        return guiButton;
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (result) {
            String link = null;

            if (beingChecked instanceof Button) {
                Button button = (Button) beingChecked;
                if (button.getAction() instanceof ActionOpenLink) {
                    link = ((ActionOpenLink) button.getAction()).getLink();
                }
            } else if (beingChecked instanceof Label) {
                Label text = (Label) beingChecked;
                if (text.getAction() instanceof ActionOpenLink) {
                    link = ((ActionOpenLink) text.getAction()).getLink();
                }
            }

            if (link != null) {
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                    oclass.getMethod("browse", new Class[]{ URI.class }).invoke(object, new URI(link));
                } catch (Throwable throwable) {
                    BTEUtilities.getLogger().error("Couldn't open link", throwable);
                }
            }
        }

        this.mc.displayGuiScreen(this);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            textLabels.forEach(l -> l.mouseClicked(mouseX, mouseY));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        glBegin(GL_QUADS);
        glColor3f(0, 0, 0);
        glVertex3f(0, 0, 0);
        glColor3f(0, 0, 0);
        glVertex3f(0, this.height, 0);
        glColor3f(0, 0, 0);
        glVertex3f(this.width, this.height, 0);
        glColor3f(0, 0, 0);
        glVertex3f(this.width, 0, 0);
        glEnd();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        if (background != null) {
            if (background.getSlideshow() == null) {
                background.getImage().bind();
                drawBackground(background.getMode());
            } else {
                background.getSlideshow().getCurrentResource1().bind();
                drawBackground(background.getMode());
                if (background.getSlideshow().isFading()) {
                    GlStateManager.enableBlend();
                    background.getSlideshow().getCurrentResource2().bind();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, background.getSlideshow().getAlphaFade(partialTicks));
                    drawBackground(background.getMode());
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Images
        for (Image i : images) {
            int posX = modX(i.getPosX(), i.getAlignment());
            int posY = modY(i.getPosY(), i.getAlignment());
            if (i.getHoverImage() != null && mouseX >= posX && mouseX <= posX + i.getWidth() && mouseY >= posY && mouseY <= posY + i.getHeight()) {
                i.getHoverImage().bind();
            } else {
                i.getImage().bind();
            }
            drawCompleteImage(posX, posY, i.getWidth(), i.getHeight());
        }

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        textLabels.forEach(t -> t.drawLabel(mouseX, mouseY));
        buttonList.forEach(b -> b.drawButton(this.mc, mouseX, mouseY, partialTicks));
        buttonList.stream()
                .filter(b -> b instanceof GuiCustomButton)
                .forEach(b -> ((GuiCustomButton) b).drawTooltip(this.mc, mouseX, mouseY));
        labelList.forEach(l -> l.drawLabel(this.mc, mouseX, mouseY));
    }

    private void drawBackground(Mode mode) {
        int imageWidth = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
        int imageHeight = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);

        float factorWidth = width / (float) imageWidth;
        float factorHeight = height / (float) imageHeight;

        switch (mode) {
            case FILL:
                int drawWidth;
                int drawHeight;
                if (factorWidth > factorHeight) {
                    drawWidth = (int) (imageWidth * factorWidth);
                    drawHeight = (int) (imageHeight * factorWidth);
                } else {
                    drawWidth = (int) (imageWidth * factorHeight);
                    drawHeight = (int) (imageHeight * factorHeight);
                }

                RenderUtil.drawPartialImage(0, 0, 0, 0, drawWidth, drawHeight, imageWidth, imageHeight);
                break;
            case STRETCH:
                drawCompleteImage(0, 0, width, height);
                break;
            case CENTER:
                drawCompleteImage((int) (width / 2F - imageWidth / 2F), (int) (height / 2F - imageHeight / 2F), imageWidth, imageHeight);
                break;
            case TILE:
                int countX = (int) Math.ceil(width / (float) imageWidth);
                int countY = (int) Math.ceil(height / (float) imageHeight);

                for (int cX = 0; cX < countX; cX++) {
                    for (int cY = 0; cY < countY; cY++) {
                        drawCompleteImage(cX * imageWidth, cY * imageHeight, imageWidth, imageHeight);
                    }
                }
                break;
        }
    }

    @Override
    public void updateScreen() {
        if (background != null && background.getSlideshow() != null) {
            background.getSlideshow().update();
            if (background.getSlideshow().isFading() && !updating) {
                updating = true;
                onSlideshowFade();
            } else if (!background.getSlideshow().isFading()) {
                updating = false;
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_R)) {
            BTEUtilities.getInstance().reload();
            mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    private void drawCompleteImage(int posX, int posY, int width, int height) {
        glPushMatrix();
        glTranslatef(posX, posY, 0);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(0, 0, 0);
        glTexCoord2f(0, 1);
        glVertex3f(0, height, 0);
        glTexCoord2f(1, 1);
        glVertex3f(width, height, 0);
        glTexCoord2f(1, 0);
        glVertex3f(width, 0, 0);
        glEnd();
        glPopMatrix();
    }

    public int modX(int posX, Alignment alignment) {
        return (int) (posX + (width * alignment.getX()));
    }

    public int modY(int posY, Alignment alignment) {
        return (int) (posY + (height * alignment.getY()));
    }

    @Override
    protected void actionPerformed(@NonNull GuiButton button) {
        if (button instanceof GuiCustomWrappedButton && this.name.equals("mainmenu")) {
            GuiCustomWrappedButton wrapped = (GuiCustomWrappedButton) button;
            if (wrapped.wrappedButton != null) {
                ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(new GuiFakeMain(), wrapped.wrappedButton, new ArrayList<>());
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    return;
                }
                event.getButton().playPressSound(this.mc.getSoundHandler());
                if (this.equals(this.mc.currentScreen)) {
                    MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(new GuiFakeMain(), wrapped.wrappedButton, new ArrayList<>()));
                }
            }
        } else if (button.id >= 6000 && button instanceof GuiCustomButton) {
            GuiCustomButton custom = (GuiCustomButton) button;
            if (custom.b.getAction() != null) {
                custom.b.getAction().perform(custom.b, this);
            }
        }
    }
}
