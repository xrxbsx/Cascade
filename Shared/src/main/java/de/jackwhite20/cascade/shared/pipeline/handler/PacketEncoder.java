/*
 * Copyright (c) 2016 "JackWhite20"
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
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by JackWhite20 on 24.09.2016.
 */
public class PacketEncoder extends MessageToMessageEncoder<Packet> {

    private Protocol protocol;

    public PacketEncoder(Protocol protocol) {

        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out) throws Exception {

        try {
            byte id = protocol.findId(packet.getClass());

            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeByte(id);
            packet.write(byteBuf);

            out.add(byteBuf);
        } catch (IllegalStateException e) {
            ctx.fireExceptionCaught(e);
        }
    }
}
