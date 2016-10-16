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

package de.jackwhite20.cascade.shared.session.impl;

import de.jackwhite20.cascade.shared.pipeline.PipelineUtils;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;

/**
 * Created by JackWhite20 on 24.09.2016.
 */
public class CascadeSession extends SimpleChannelInboundHandler<Packet> implements Session {

    private Channel channel;

    private Protocol protocol;

    private List<SessionListener> sessionListener;

    public CascadeSession(Channel channel, Protocol protocol, List<SessionListener> sessionListener) {

        this.channel = channel;
        this.protocol = protocol;
        this.sessionListener = sessionListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // Only call onConnected after successful handshake if SSL is used
        SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
        if (sslHandler != null) {
            sslHandler.handshakeFuture().addListener(
                    future -> {
                        if (future.isSuccess()) {
                            for (SessionListener listener : sessionListener) {
                                listener.onConnected(this);
                            }
                        }
                    });
        } else {
            for (SessionListener listener : sessionListener) {
                listener.onConnected(this);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        PipelineUtils.closeOnFlush(channel);

        for (SessionListener listener : sessionListener) {
            listener.onDisconnected(this);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {

        protocol.call(packet.getClass(), this, packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        if (cause instanceof IOException) {
            PipelineUtils.closeOnFlush(channel);
        } else {
            cause.printStackTrace();
        }
    }

    @Override
    public void send(Packet packet) {

        channel.writeAndFlush(packet);
    }

    @Override
    public void close() {

        PipelineUtils.closeOnFlush(channel);
    }

    @Override
    public boolean isConnected() {

        return channel.isActive();
    }

    @Override
    public SocketAddress remoteAddress() {

        return channel.remoteAddress();
    }

    @Override
    public Protocol protocol() {

        return protocol;
    }
}
