/*
 * Copyright (c) 2015 "JackWhite20"
 *
 * This file is part of Cascade.
 *
 * Cascade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.jackwhite20.cascade.shared.pipeline.handler;

import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by JackWhite20 on 24.09.2016.
 */
public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private Protocol protocol;

    public PacketDecoder(Protocol protocol) {

        this.protocol = protocol;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {

        int length = byteBuf.readInt();

        // Allow packets with no data
        if (length > 0) {
            byte id = byteBuf.readByte();

            try {
                Packet packet = protocol.create(id);
                packet.read(byteBuf);

                out.add(packet);
            } catch (IllegalStateException e) {
                ctx.fireExceptionCaught(e);
            }
        }
    }
}
