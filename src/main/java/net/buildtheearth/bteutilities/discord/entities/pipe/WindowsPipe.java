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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@SuppressWarnings("BusyWait")
public class WindowsPipe extends Pipe {
    private final RandomAccessFile file;

    WindowsPipe(String location) {
        try {
            this.file = new RandomAccessFile(location, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        file.write(b);
    }

    @Override
    public Packet read() throws IOException {
        while (file.length() == 0 && status == PipeStatus.CONNECTED) {
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

        Packet.OpCode op = Packet.OpCode.values()[Integer.reverseBytes(file.readInt())];
        int len = Integer.reverseBytes(file.readInt());
        byte[] d = new byte[len];

        file.readFully(d);
        return new Packet(op, BTEUtilities.parser.parse(new String(d)).getAsJsonObject());
    }

    @Override
    public void close() throws IOException {
        send(Packet.OpCode.CLOSE, new JsonObject());
        status = PipeStatus.CLOSED;
        file.close();
    }
}
