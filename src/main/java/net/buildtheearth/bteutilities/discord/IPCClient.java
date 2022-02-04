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

import com.google.gson.JsonObject;
import net.buildtheearth.bteutilities.BTEUtilities;
import net.buildtheearth.bteutilities.discord.entities.DiscordBuild;
import net.buildtheearth.bteutilities.discord.entities.Packet;
import net.buildtheearth.bteutilities.discord.entities.RichPresence;
import net.buildtheearth.bteutilities.discord.entities.pipe.Pipe;
import net.buildtheearth.bteutilities.discord.entities.pipe.PipeStatus;
import net.buildtheearth.bteutilities.discord.exceptions.NoDiscordClientException;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.function.Consumer;

public final class IPCClient implements Closeable {
    private static final Logger LOGGER = BTEUtilities.getLogger();
    private final long clientId;
    private volatile Pipe pipe;
    private Consumer<IPCClient> onReadyEvent = null;

    /**
     * Constructs a new IPCClient using the provided {@code clientId}.<br>
     * This is initially unconnected to Discord.
     *
     * @param clientId The Rich Presence application's client ID, which can be found
     *                 <a href=https://discordapp.com/developers/applications/me>here</a>
     */
    public IPCClient(long clientId) {
        this.clientId = clientId;
    }

    /**
     * Finds the current process ID.
     *
     * @return The current process ID.
     */
    private static int getPID() {
        String pr = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(pr.substring(0, pr.indexOf('@')));
    }

    public void onReadyEvent(Consumer<IPCClient> client) {
        onReadyEvent = client;
    }

    /**
     * Opens the connection between the IPCClient and Discord.<p>
     *
     * <b>This must be called before any data is exchanged between the
     * IPCClient and Discord.</b>
     *
     * @param preferredOrder the priority order of client builds to connect to
     * @throws IllegalStateException    There is an open connection on this IPCClient.
     * @throws NoDiscordClientException No client of the provided {@link DiscordBuild build type}(s) was found.
     */
    public void connect(DiscordBuild... preferredOrder) throws NoDiscordClientException {
        checkConnected(false);
        pipe = null;

        pipe = Pipe.openPipe(clientId, preferredOrder);

        LOGGER.debug("Client is now connected and ready!");
        if (onReadyEvent != null) {
            onReadyEvent.accept(this);
        }
    }

    /**
     * Sends a {@link RichPresence} to the Discord client.<p>
     * <p>
     * This is where the IPCClient will officially display
     * a Rich Presence in the Discord client.<p>
     * <p>
     * Sending this again will overwrite the last provided
     * {@link RichPresence}.
     *
     * @param presence The {@link RichPresence} to send.
     * @throws IllegalStateException If a connection was not made prior to invoking
     *                               this method.
     * @see RichPresence
     */
    public void sendRichPresence(RichPresence presence) {
        checkConnected(true);
        LOGGER.debug("Sending RichPresence to discord: " + (presence == null ? null : presence.toJson().toString()));
        JsonObject object = new JsonObject();
        object.addProperty("cmd", "SET_ACTIVITY");
        JsonObject args = new JsonObject();
        args.addProperty("pid", getPID());
        args.add("activity", presence == null ? null : presence.toJson());
        object.add("args", args);
        pipe.send(Packet.OpCode.FRAME, object);
    }

    /**
     * Gets the IPCClient's current {@link PipeStatus}.
     *
     * @return The IPCClient's current {@link PipeStatus}.
     */
    public PipeStatus getStatus() {
        if (pipe == null) {
            return PipeStatus.UNINITIALIZED;
        }

        return pipe.getStatus();
    }

    /**
     * Attempts to close an open connection to Discord.<br>
     * This can be reopened with another call to {@link #connect(DiscordBuild...)}.
     *
     * @throws IllegalStateException If a connection was not made prior to invoking
     *                               this method.
     */
    @Override
    public void close() {
        checkConnected(true);

        try {
            pipe.close();
        } catch (IOException e) {
            LOGGER.debug("Failed to close pipe", e);
        }
    }

    /**
     * Makes sure that the client is connected (or not) depending on if it should
     * for the current state.
     *
     * @param connected Whether to check in the context of the IPCClient being
     *                  connected or not.
     */
    private void checkConnected(boolean connected) {
        if (connected && getStatus() != PipeStatus.CONNECTED) {
            throw new IllegalStateException(String.format("IPCClient (ID: %d) is not connected!", clientId));
        }
        if (!connected && getStatus() == PipeStatus.CONNECTED) {
            throw new IllegalStateException(String.format("IPCClient (ID: %d) is already connected!", clientId));
        }
    }
}
