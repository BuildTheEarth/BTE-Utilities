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

package net.buildtheearth.bteutilities.gui.util;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
    public static void drawPartialImage(int posX, int posY, int imageX, int imageY, int width, int height, int imagePartWidth, int imagePartHeight) {
        double imageWidth = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
        double imageHeight = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);

        double einsTeilerWidth = 1F / imageWidth;
        double uvWidth = einsTeilerWidth * imagePartWidth;
        double uvX = einsTeilerWidth * imageX;

        double einsTeilerHeight = 1F / imageHeight;
        double uvHeight = einsTeilerHeight * imagePartHeight;
        double uvY = einsTeilerHeight * imageY;

        glPushMatrix();
        glTranslatef(posX, posY, 0);
        glBegin(GL_QUADS);
        glTexCoord2d(uvX, uvY);
        glVertex3f(0, 0, 0);
        glTexCoord2d(uvX, uvY + uvHeight);
        glVertex3f(0, height, 0);
        glTexCoord2d(uvX + uvWidth, uvY + uvHeight);
        glVertex3f(width, height, 0);
        glTexCoord2d(uvX + uvWidth, uvY);
        glVertex3f(width, 0, 0);
        glEnd();
        glPopMatrix();
    }
}
