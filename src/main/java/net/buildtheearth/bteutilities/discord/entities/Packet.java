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
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.buildtheearth.bteutilities.BTEUtilities;
import net.buildtheearth.bteutilities.discord.IPCClient;

import java.nio.ByteBuffer;

@AllArgsConstructor
@Getter
public class Packet {
    private final OpCode op;
    private final JsonObject data;

    /**
     * Converts this {@link Packet} to a {@code byte} array.
     *
     * @return This Packet as a {@code byte} array.
     */
    public byte[] toBytes() {
        byte[] d = BTEUtilities.gson.toJson(data).getBytes();
        ByteBuffer packet = ByteBuffer.allocate(d.length + 2 * Integer.BYTES);
        packet.putInt(Integer.reverseBytes(op.ordinal()));
        packet.putInt(Integer.reverseBytes(d.length));
        packet.put(d);
        return packet.array();
    }

    @Override
    public String toString() {
        return "Pkt:" + getOp() + getData().toString();
    }

    /**
     * Discord response OpCode values that are
     * sent with response data to and from Discord
     * and the {@link IPCClient IPCClient}
     * connected.
     */
    public enum OpCode {
        HANDSHAKE, FRAME, CLOSE, PING, PONG
    }
}
