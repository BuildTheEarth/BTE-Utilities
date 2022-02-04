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

package net.buildtheearth.bteutilities.discord;

import net.buildtheearth.bteutilities.BTEUtilities;
import net.buildtheearth.bteutilities.Config;
import net.buildtheearth.bteutilities.discord.entities.RichPresence;

import java.time.OffsetDateTime;

public class DiscordHandler {
    private static DiscordHandler instance = null;
    private final IPCClient client;
    private final OffsetDateTime offsetDateTime;

    private DiscordHandler() {
        client = new IPCClient(798659701738962945L);
        offsetDateTime = OffsetDateTime.now();
        client.onReadyEvent(client -> setState(null));
    }

    public static DiscordHandler getInstance() {
        return instance == null ? instance = new DiscordHandler() : instance;
    }

    public void configChanged() {
        if (Config.richPresence) {
            try {
                client.connect();
            } catch (Exception e) {
                BTEUtilities.getLogger().warn("Failed to connect to the discord client!");
            }
        } else {
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void setState(String state) {
        try {
            RichPresence.Assets assets = new RichPresence.Assets("btelogo", "BuildTheEarth.net", "minecraft", "Minecraft 1.12.2");
            RichPresence richPresence = new RichPresence(state, null, offsetDateTime, assets);
            client.sendRichPresence(richPresence);
        } catch (Exception ignored) {
        }
    }
}
