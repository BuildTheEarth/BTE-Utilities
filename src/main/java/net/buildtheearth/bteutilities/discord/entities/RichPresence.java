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

package net.buildtheearth.bteutilities.discord.entities;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import net.buildtheearth.bteutilities.BTEUtilities;

import java.time.OffsetDateTime;

@AllArgsConstructor
public class RichPresence {
    private final String state;
    private final String details;
    private final OffsetDateTime start;
    private final Assets assets;

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("state", state);
        object.addProperty("details", details);
        Timestamps timestamps = new Timestamps(start.toEpochSecond(), null);
        object.add("timestamps", BTEUtilities.gson.toJsonTree(timestamps));
        object.add("assets", BTEUtilities.gson.toJsonTree(assets));
        object.addProperty("instance", false);
        return object;
    }

    @AllArgsConstructor
    private static class Timestamps {
        public long start;
        public Object end;
    }

    @AllArgsConstructor
    public static class Assets {
        @SerializedName("large_image")
        public String largeImageKey;
        @SerializedName("large_text")
        public String largeImageText;
        @SerializedName("small_image")
        public String smallImageKey;
        @SerializedName("small_text")
        public String smallImageText;
    }
}
