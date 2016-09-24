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

package de.jackwhite20.cascade.client;

import de.jackwhite20.cascade.shared.protocol.packet.Packet;

import java.net.SocketAddress;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public interface Client {

    /**
     * Connects the client with the passed client config.
     *
     * This method will block until the socket is connected or an exception is thrown.
     */
    void connect();

    /**
     * Disconnects the client and frees all used resources.
     */
    void disconnect();

    /**
     * Returns if the client is connected.
     *
     * @return true if connected otherwise false.
     */
    boolean isConnected();

    /**
     * Sends a packet over TCP (ProtocolType.TCP).
     *
     * @param packet the packet.
     */
    void send(Packet packet);

    /**
     * Returns the remote address of this client.
     *
     * @return the remote address.
     */
    SocketAddress remoteAddress();
}
