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

package de.jackwhite20.cascade.client;

import de.jackwhite20.cascade.client.impl.ClientConfig;
import de.jackwhite20.cascade.client.impl.ClientSessionListener;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.session.SessionListener;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

import java.net.SocketAddress;

/**
 * Created by JackWhite20 on 16.10.2016.
 */
public abstract class CascadeAbstractClient implements Client {

    protected ClientConfig clientConfig;

    protected EventLoopGroup workerGroup;

    protected Channel channel;

    public CascadeAbstractClient(ClientConfig clientConfig) {

        this.clientConfig = clientConfig;
        this.clientConfig.sessionListener(new ClientSessionListener(this));
    }

    public abstract void connect();

    @Override
    public void disconnect() {

        if (channel.isActive()) {
            channel.close();
        }

        workerGroup.shutdownGracefully();
    }

    @Override
    public void send(Packet packet) {

        channel.writeAndFlush(packet);
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
    public void addSessionListener(SessionListener... sessionListener) {

        clientConfig.sessionListener(sessionListener);
    }
}
