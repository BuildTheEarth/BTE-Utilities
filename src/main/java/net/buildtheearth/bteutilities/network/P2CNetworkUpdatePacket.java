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
import net.buildtheearth.bteutilities.discord.DiscordHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class P2CNetworkUpdatePacket implements IMessage {

    public String status;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.status = BTENetworkHandler.decodeStringFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class P2CNetworkUpdatePacketHandler implements IMessageHandler<P2CNetworkUpdatePacket, IMessage> {

        public P2CNetworkUpdatePacketHandler() {
        }

        @Override
        public IMessage onMessage(P2CNetworkUpdatePacket pkt, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (pkt.status != null) {
                    DiscordHandler.getInstance().setState(pkt.status);
                }
            });
            return null;
        }


    }

}
