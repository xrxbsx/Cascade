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
import de.jackwhite20.cascade.shared.security.CryptoFunction;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.session.impl.CascadeSession;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

import java.util.List;

/**
 * Created by JackWhite20 on 24.09.2016.
 */
public class CascadeChannelInitializer extends ChannelInitializer<SocketChannel> {

    private String host;

    private int port;

    private SslContext sslContext;

    private Protocol protocol;

    private List<SessionListener> sessionListener;

    private CryptoFunction cryptoFunction;

    public CascadeChannelInitializer(String host, int port, SslContext sslContext, Protocol protocol, List<SessionListener> sessionListener, CryptoFunction cryptoFunction) {

        this.host = host;
        this.port = port;
        this.sslContext = sslContext;
        this.protocol = protocol;
        this.sessionListener = sessionListener;
        this.cryptoFunction = cryptoFunction;
    }

    public CascadeChannelInitializer(Protocol protocol, List<SessionListener> sessionListener, CryptoFunction cryptoFunction) {

        this("", 0, null, protocol, sessionListener, cryptoFunction);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        if (sslContext != null) {
            ch.pipeline().addLast(sslContext.newHandler(ch.alloc(), host, port));
        }

        // In
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
        if (cryptoFunction != null) {
            ch.pipeline().addLast(cryptoFunction.getDecoder());
        }
        ch.pipeline().addLast(new PacketDecoder(protocol));

        // Out
        ch.pipeline().addLast(new LengthFieldPrepender(4));
        if (cryptoFunction != null) {
            ch.pipeline().addLast(cryptoFunction.getEncoder());
        }
        ch.pipeline().addLast(new PacketEncoder(protocol));

        // Handler
        ch.pipeline().addLast(new CascadeSession(ch, protocol, sessionListener));
    }
}
