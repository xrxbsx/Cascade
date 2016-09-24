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

package de.jackwhite20.cascade.shared.session;

import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;

import java.net.SocketAddress;

/**
 * Created by JackWhite20 on 09.01.2016.
 */
public interface Session {

    /**
     * Closed and disconnects the session.
     */
    void close();

    /**
     * Sends a packet.
     *
     * @param packet the packet.
     */
    void send(Packet packet);

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

    /**
     * Returns true if the session is connected to a remote host.
     * Otherwise it will return false.
     *
     * @return if the session is connected to a remote host or not.
     */
    boolean isConnected();
}
