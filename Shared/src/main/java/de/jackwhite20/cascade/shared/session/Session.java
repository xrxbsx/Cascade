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

package de.jackwhite20.cascade.shared.session;

import de.jackwhite20.cascade.shared.Compressor;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Created by JackWhite20 on 09.01.2016.
 */
public interface Session {

    void close();

    void send(Packet packet, ProtocolType protocolType);

    int id();

    SocketChannel socketChannel();

    DatagramChannel datagramChannel();

    Compressor compressor();

    int compressionThreshold();

    List<SessionListener> listener();

    Protocol protocol();

    SocketAddress remoteAddress();
}
