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

public enum DiscordBuild {
    /**
     * Constant for the current Discord Canary release.
     */
    CANARY("//canary.discordapp.com/api"),

    /**
     * Constant for the current Discord Public Test Build or PTB release.
     */
    PTB("//ptb.discordapp.com/api"),

    /**
     * Constant for the current stable Discord release.
     */
    STABLE("//discordapp.com/api"),

    ANY;

    private final String endpoint;

    DiscordBuild(String endpoint) {
        this.endpoint = endpoint;
    }

    DiscordBuild() {
        this(null);
    }

    /**
     * Gets a {@link DiscordBuild} matching the specified endpoint.<p>
     * <p>
     * This is only internally implemented.
     *
     * @param endpoint The endpoint to get from.
     * @return The DiscordBuild corresponding to the endpoint, or
     * {@link DiscordBuild#ANY} if none match.
     */
    public static DiscordBuild from(String endpoint) {
        for (DiscordBuild value : values()) {
            if (value.endpoint != null && value.endpoint.equals(endpoint)) {
                return value;
            }
        }
        return ANY;
    }
}
