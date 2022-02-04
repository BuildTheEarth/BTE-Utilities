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

package net.buildtheearth.bteutilities.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class BTENetworkHandler {
    public static final SimpleNetworkWrapper CHANNEL_CLIENT = NetworkRegistry.INSTANCE.newSimpleChannel("buildtheearth:client");

    public static void registerHandlers() {
        CHANNEL_CLIENT.registerMessage(P2CNetworkUpdatePacket.P2CNetworkUpdatePacketHandler.class, P2CNetworkUpdatePacket.class, 0, Side.CLIENT);
    }

    public static String decodeStringFromByteBuf(ByteBuf buf) {
        PacketBuffer packetBuffer = getPacketBuffer(buf);
        return packetBuffer.readString(Integer.MAX_VALUE / 4);
    }

    private static PacketBuffer getPacketBuffer(ByteBuf buf) {
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.setIndex(readerIndex, writerIndex);
        return packetBuffer;
    }
}
