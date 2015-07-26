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

package de.jackwhite20.cascade.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by JackWhite20 on 26.07.2015.
 */
public class ServerSession {

    private int id;

    private int tcpBufferSize;

    private int udpBufferSize;

    private SocketChannel socketChannel;

    private DatagramChannel datagramChannel;

    private SocketAddress remoteAddress;

    public ServerSession(int id, int tcpBufferSize, int udpBufferSize, SocketChannel socketChannel, DatagramChannel datagramChannel) {
        this.id = id;
        this.tcpBufferSize = tcpBufferSize;
        this.udpBufferSize = udpBufferSize;
        this.socketChannel = socketChannel;
        this.datagramChannel = datagramChannel;
        try {
            this.remoteAddress = socketChannel.getRemoteAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int id() {
        return id;
    }

    private int tcpBufferSize() {
        return tcpBufferSize;
    }

    private int udpBufferSize() {
        return udpBufferSize;
    }

    private SocketChannel socketChannel() {
        return socketChannel;
    }

    private DatagramChannel datagramChannel() {
        return datagramChannel;
    }

    private SocketAddress remoteAddress() {
        return remoteAddress;
    }

}
