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

import de.jackwhite20.cascade.shared.callback.PacketCallback;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.RequestPacket;
import de.jackwhite20.cascade.shared.protocol.packet.ResponsePacket;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.session.impl.ProtocolType;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public interface Client {

    /**
     * Connects the client with the passed client config.
     *
     * This method will block until the socket is connected or an exception is thrown.
     */
    boolean connect();

    /**
     * Sets the session listener.
     *
     * @param sessionListener the session listener.
     */
    void sessionListener(SessionListener sessionListener);

    /**
     * Disconnects the client and frees all used resources.
     */
    void disconnect();

    /**
     * Returns if the client is connected and running.
     *
     * @return true if running otherwise false.
     */
    boolean running();

    /**
     * Sends a packet over TCP (ProtocolType.TCP).
     *
     * @param packet the packet.
     */
    void send(Packet packet);

    /**
     * Sends a packet over the given protocol type.
     *
     * @param packet the packet.
     * @param protocolType the protocol type.
     */
    void send(Packet packet, ProtocolType protocolType);

    /**
     * Sends a packet and executes the packet callback when the response packet gets received.
     * The response packet must extend ResponsePacket.
     *
     * @param packet the packet.
     * @param protocolType the the protocol type.
     * @param packetCallback the packet callback.
     */
    <T extends ResponsePacket> void send(RequestPacket packet, ProtocolType protocolType, PacketCallback<T> packetCallback);

    /**
     * Sends a packet over TCP (ProtocolType.TCP) and executes the packet callback when the response packet gets received.
     * The response packet must extend ResponsePacket.
     *
     * @param packet the packet.
     * @param packetCallback the packet callback.
     */
    <T extends ResponsePacket> void send(RequestPacket packet, PacketCallback<T> packetCallback);
}
