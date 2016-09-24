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

package de.jackwhite20.cascade.shared.pipeline.initialize;

import de.jackwhite20.cascade.shared.pipeline.handler.PacketDecoder;
import de.jackwhite20.cascade.shared.pipeline.handler.PacketEncoder;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.session.impl.CascadeSession;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.List;

/**
 * Created by JackWhite20 on 24.09.2016.
 */
public class CascadeChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Protocol protocol;

    private List<SessionListener> sessionListener;

    public CascadeChannelInitializer(Protocol protocol, List<SessionListener> sessionListener) {

        this.protocol = protocol;
        this.sessionListener = sessionListener;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
        ch.pipeline().addLast(new PacketDecoder(protocol));
        ch.pipeline().addLast(new LengthFieldPrepender(4));
        ch.pipeline().addLast(new PacketEncoder(protocol));
        ch.pipeline().addLast(new CascadeSession(ch, protocol, sessionListener));
    }
}
