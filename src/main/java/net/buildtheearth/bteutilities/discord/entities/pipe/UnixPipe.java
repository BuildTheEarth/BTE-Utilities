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
import net.buildtheearth.bteutilities.discord.entities.Packet;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@SuppressWarnings("BusyWait")
public class UnixPipe extends Pipe {

    private final AFUNIXSocket socket;

    UnixPipe(String location) throws IOException {
        socket = AFUNIXSocket.newInstance();
        socket.connect(AFUNIXSocketAddress.of(new File(location)));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public Packet read() throws IOException {
        InputStream is = socket.getInputStream();

        while (is.available() == 0 && status == PipeStatus.CONNECTED) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }

        if (status == PipeStatus.DISCONNECTED) {
            throw new IOException("Disconnected!");
        }
        if (status == PipeStatus.CLOSED) {
            return new Packet(Packet.OpCode.CLOSE, null);
        }

        byte[] d = new byte[8];
        is.read(d);
        ByteBuffer bb = ByteBuffer.wrap(d);

        Packet.OpCode op = Packet.OpCode.values()[Integer.reverseBytes(bb.getInt())];
        d = new byte[Integer.reverseBytes(bb.getInt())];

        is.read(d);
        return new Packet(op, BTEUtilities.parser.parse(new String(d)).getAsJsonObject());
    }

    @Override
    public void write(byte[] b) throws IOException {
        socket.getOutputStream().write(b);
    }

    @Override
    public void close() throws IOException {
        send(Packet.OpCode.CLOSE, new JsonObject());
        status = PipeStatus.CLOSED;
        socket.close();
    }
}
