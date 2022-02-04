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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.buildtheearth.bteutilities.gui.textures.ITexture;

@RequiredArgsConstructor
public class Slideshow {
    @Getter
    public final ITexture[] images;
    @Getter
    @Setter
    private int displayDuration = 60;
    @Getter
    @Setter
    private int fadeDuration = 20;
    private int counter = 0;
    @Getter
    private boolean fading = false;

    public void update() {
        counter++;
        fading = (counter % (displayDuration + fadeDuration)) > displayDuration;
    }

    public float getAlphaFade(float partial) {
        float counterProgress = ((counter + partial) % (displayDuration + fadeDuration)) - displayDuration;

        float durationTeiler = 1F / fadeDuration;
        return durationTeiler * counterProgress;
    }

    public ITexture getCurrentResource1() {
        int index = counter / ((displayDuration + fadeDuration)) % images.length;
        return images[index];
    }

    public ITexture getCurrentResource2() {
        if (fading) {
            int index = (counter + fadeDuration) / ((displayDuration + fadeDuration)) % images.length;
            return images[index];
        }
        return null;
    }

    public int getIndex() {
        return (counter + fadeDuration) / ((displayDuration + fadeDuration)) % images.length;
    }
}
