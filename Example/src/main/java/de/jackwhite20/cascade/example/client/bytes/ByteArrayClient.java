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

package de.jackwhite20.cascade.example.client.bytes;

import de.jackwhite20.cascade.client.Client;
import de.jackwhite20.cascade.client.ClientFactory;
import de.jackwhite20.cascade.shared.Options;
import de.jackwhite20.cascade.shared.protocol.impl.ByteArrayPacket;
import de.jackwhite20.cascade.shared.protocol.impl.ByteArrayProtocol;
import de.jackwhite20.cascade.shared.protocol.listener.PacketHandler;
import de.jackwhite20.cascade.shared.protocol.listener.PacketListener;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.impl.ProtocolType;

import java.net.StandardSocketOptions;

/**
 * Created by JackWhite20 on 03.01.2016.
 */
public class ByteArrayClient implements PacketListener {

    public static void main(String[] args) {

        new ByteArrayClient("localhost", 12345).connect();
    }

    private String host;

    private int port;

    private Client client;

    public ByteArrayClient(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void connect() {

        // Create a new client and set the TCP_NODELAY option to true
        client = ClientFactory.create(host, port, new ByteArrayProtocol(this), Options.of(StandardSocketOptions.TCP_NODELAY, true));
        client.connect();

        System.out.println("Connected!");

        String message = "Hey my friend.";
        System.out.println("Sending to Server: " + message);
        // Send the packet reliable (TCP) to the server
        client.send(new ByteArrayPacket(message.getBytes()), ProtocolType.TCP);
    }

    @PacketHandler
    public void onByteArrayPacket(Session session, ByteArrayPacket byteArrayPacket, ProtocolType type) {

        System.out.println("Received byte array string from server: " + new String(byteArrayPacket.bytes()));
    }

    public Client client() {

        return client;
    }
}
