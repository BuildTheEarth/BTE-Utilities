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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class IconLoader {
    /**
     * Loads an icon in ByteBuffer form
     *
     * @param filepath The location of the Image to use as an icon.
     * @return An array of ByteBuffers containing the pixel data for the icon in varying sizes.
     */
    public static ByteBuffer[] load(String filepath) {
        return load(IconLoader.class.getClassLoader().getResourceAsStream(filepath));
    }

    /**
     * Loads an icon in ByteBuffer form.
     *
     * @param is A File pointing to the image.
     * @return An array of ByteBuffers containing the pixel data for the icon in various sizes (as recommended by the OS).
     */
    public static ByteBuffer[] load(InputStream is) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer[] buffers;
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN")) {
            buffers = new ByteBuffer[2];
            buffers[0] = loadInstance(image, 16);
            buffers[1] = loadInstance(image, 32);
        } else if (OS.contains("MAC")) {
            buffers = new ByteBuffer[1];
            buffers[0] = loadInstance(image, 128);
        } else {
            buffers = new ByteBuffer[1];
            buffers[0] = loadInstance(image, 32);
        }
        return buffers;
    }

    /**
     * Copies the supplied image into a square icon at the indicated size.
     *
     * @param image     The image to place onto the icon.
     * @param dimension The desired size of the icon.
     * @return A ByteBuffer of pixel data at the indicated size.
     */
    private static ByteBuffer loadInstance(BufferedImage image, int dimension) {
        BufferedImage scaledIcon = new BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g = scaledIcon.createGraphics();
        double ratio = getIconRatio(image, scaledIcon);
        double width = image.getWidth() * ratio;
        double height = image.getHeight() * ratio;
        g.drawImage(image, (int) ((scaledIcon.getWidth() - width) / 2), (int) ((scaledIcon.getHeight() - height) / 2), (int) (width), (int) (height), null);
        g.dispose();

        return convertToByteBuffer(scaledIcon);
    }

    /**
     * Gets the width/height ratio of the icon. This is meant to simplify scaling the icon to a new dimension.
     *
     * @param src  The base image that will be placed onto the icon.
     * @param icon The icon that will have the image placed on it.
     * @return The amount to scale the source image to fit it onto the icon appropriately.
     */
    private static double getIconRatio(BufferedImage src, BufferedImage icon) {
        double ratio;
        ratio = (double) (icon.getWidth()) / src.getWidth();
        double r2 = (double) (icon.getHeight()) / src.getHeight();
        return Math.min(r2, ratio);
    }

    /**
     * Converts a BufferedImage into a ByteBuffer of pixel data.
     *
     * @param image The image to convert.
     * @return A ByteBuffer that contains the pixel data of the supplied image.
     */
    public static ByteBuffer convertToByteBuffer(BufferedImage image) {
        byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
        int counter = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int colorSpace = image.getRGB(j, i);
                buffer[counter] = (byte) ((colorSpace << 8) >> 24);
                buffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
                buffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
                buffer[counter + 3] = (byte) (colorSpace >> 24);
                counter += 4;
            }
        }
        return ByteBuffer.wrap(buffer);
    }
}
