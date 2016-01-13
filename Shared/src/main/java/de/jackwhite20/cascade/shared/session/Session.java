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

    /**
     * Closed and disconnects the session.
     */
    void close();

    /**
     * Sends a packet with the given protocol type.
     *
     * @param packet the packet.
     * @param protocolType the protocol type.
     */
    void send(Packet packet, ProtocolType protocolType);

    /**
     * Sends a packet over TCP (ProtocolType.TCP).
     *
     * @param packet the packet.
     */
    void send(Packet packet);

    /**
     * Gets the id from the session.
     *
     * @return the id.
     */
    int id();

    /**
     * Gets the socket channel from this session.
     *
     * @return the socket channel.
     */
    SocketChannel socketChannel();

    /**
     * Gets the datagram channel from this session.
     *
     * @return the datagram channel.
     */
    DatagramChannel datagramChannel();

    /**
     * Gets the compressor object from this session.
     * It is used to compress or decompress data.
     *
     * @return the compressor object.
     */
    Compressor compressor();

    /**
     * Gets the compression threshold.
     *
     * @return the compression threshold.
     */
    int compressionThreshold();

    /**
     * Gets a list with the session listeners.
     *
     * @return the list of session listeners.
     */
    List<SessionListener> listener();

    /**
     * Gets the protocol class with which packets and packet listeners are registered.
     *
     * @return the protocol.
     */
    Protocol protocol();

    /**
     * Gets the remote socket address from the session.
     *
     * @return the remote socket address.
     */
    SocketAddress remoteAddress();
}
