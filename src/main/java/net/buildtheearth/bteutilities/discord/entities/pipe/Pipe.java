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

package net.buildtheearth.bteutilities.discord.entities.pipe;

import com.google.gson.JsonObject;
import net.buildtheearth.bteutilities.BTEUtilities;
import net.buildtheearth.bteutilities.discord.entities.DiscordBuild;
import net.buildtheearth.bteutilities.discord.entities.Packet;
import net.buildtheearth.bteutilities.discord.exceptions.NoDiscordClientException;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

public abstract class Pipe {
    private static final Logger LOGGER = BTEUtilities.getLogger();
    private static final int VERSION = 1;
    private final static String[] unixPaths = { "XDG_RUNTIME_DIR", "TMPDIR", "TMP", "TEMP" };
    PipeStatus status = PipeStatus.CONNECTING;
    private DiscordBuild build;

    Pipe() {
    }

    public static Pipe openPipe(long clientId, DiscordBuild... preferredOrder) throws NoDiscordClientException {

        if (preferredOrder == null || preferredOrder.length == 0) {
            preferredOrder = new DiscordBuild[]{ DiscordBuild.ANY };
        }

        Pipe pipe = null;

        // store some files so we can get the preferred client
        Pipe[] open = new Pipe[DiscordBuild.values().length];
        for (int i = 0; i < 10; i++) {
            try {
                String location = getPipeLocation(i);
                LOGGER.debug(String.format("Searching for IPC: %s", location));
                pipe = createPipe(location);

                JsonObject json = new JsonObject();
                json.addProperty("v", VERSION);
                json.addProperty("client_id", Long.toString(clientId));
                pipe.send(Packet.OpCode.HANDSHAKE, json);

                Packet p = pipe.read(); // this is a valid client at this point

                pipe.build = DiscordBuild.from(p.getData().getAsJsonObject("data").getAsJsonObject("config").get("api_endpoint").getAsString());

                LOGGER.debug(String.format("Found a valid client (%s) with packet: %s", pipe.build.name(), p));
                // we're done if we found our first choice
                if (pipe.build == preferredOrder[0] || DiscordBuild.ANY == preferredOrder[0]) {
                    LOGGER.info(String.format("Found preferred client: %s", pipe.build.name()));
                    break;
                }

                open[pipe.build.ordinal()] = pipe; // didn't find first choice yet, so store what we have
                open[DiscordBuild.ANY.ordinal()] = pipe; // also store in 'any' for use later

                pipe.build = null;
                pipe = null;
            } catch (IOException ex) {
                pipe = null;
            }
        }

        if (pipe == null) {
            // we already know we don't have our first pick
            // check each of the rest to see if we have that
            for (int i = 1; i < preferredOrder.length; i++) {
                DiscordBuild cb = preferredOrder[i];
                LOGGER.debug(String.format("Looking for client build: %s", cb.name()));
                if (open[cb.ordinal()] != null) {
                    pipe = open[cb.ordinal()];
                    open[cb.ordinal()] = null;
                    if (cb == DiscordBuild.ANY) // if we pulled this from the 'any' slot, we need to figure out which build it was
                    {
                        for (int k = 0; k < open.length; k++) {
                            if (open[k] == pipe) {
                                pipe.build = DiscordBuild.values()[k];
                                open[k] = null; // we don't want to close this
                            }
                        }
                    } else {
                        pipe.build = cb;
                    }

                    LOGGER.info(String.format("Found preferred client: %s", pipe.build.name()));
                    break;
                }
            }
            if (pipe == null) {
                throw new NoDiscordClientException();
            }
        }
        // close unused files, except skip 'any' because its always a duplicate
        for (int i = 0; i < open.length; i++) {
            if (i == DiscordBuild.ANY.ordinal()) {
                continue;
            }
            if (open[i] != null) {
                try {
                    open[i].close();
                } catch (IOException ex) {
                    // This isn't really important to applications and better
                    // as debug info
                    LOGGER.debug("Failed to close an open IPC pipe!", ex);
                }
            }
        }

        pipe.status = PipeStatus.CONNECTED;

        return pipe;
    }

    private static Pipe createPipe(String location) {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return new WindowsPipe(location);
        } else if (osName.contains("linux") || osName.contains("mac")) {
            try {
                return new UnixPipe(location);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Unsupported OS: " + osName);
        }
    }

    /**
     * Generates a nonce.
     *
     * @return A random {@link UUID}.
     */
    private static String generateNonce() {
        return UUID.randomUUID().toString();
    }

    /**
     * Finds the IPC location in the current system.
     *
     * @param i Index to try getting the IPC at.
     * @return The IPC location.
     */
    private static String getPipeLocation(int i) {
        if (System.getProperty("os.name").contains("Win")) {
            return "\\\\?\\pipe\\discord-ipc-" + i;
        }
        String tmppath = null;
        for (String str : unixPaths) {
            tmppath = System.getenv(str);
            if (tmppath != null) {
                break;
            }
        }
        if (tmppath == null) {
            tmppath = "/tmp";
        }
        return tmppath + "/discord-ipc-" + i;
    }

    /**
     * Sends json with the given {@link Packet.OpCode}.
     *
     * @param op   The {@link Packet.OpCode} to send data with.
     * @param data The data to send.
     */
    public void send(Packet.OpCode op, JsonObject data) {
        try {
            String nonce = generateNonce();
            data.addProperty("nonce", nonce);
            Packet p = new Packet(op, data);
            write(p.toBytes());
        } catch (IOException ex) {
            LOGGER.error("Encountered an IOException while sending a packet and disconnected!");
            status = PipeStatus.DISCONNECTED;
        }
    }

    /**
     * Blocks until reading a {@link Packet} or until the
     * read thread encounters bad data.
     *
     * @return A valid {@link Packet}.
     * @throws IOException If the pipe breaks.
     */
    public abstract Packet read() throws IOException;

    public abstract void write(byte[] b) throws IOException;

    public PipeStatus getStatus() {
        return status;
    }

    public abstract void close() throws IOException;
}
