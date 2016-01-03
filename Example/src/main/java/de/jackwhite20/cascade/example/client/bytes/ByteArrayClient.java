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
import de.jackwhite20.cascade.shared.CascadeSettings;
import de.jackwhite20.cascade.shared.protocol.impl.ByteArrayPacket;
import de.jackwhite20.cascade.shared.protocol.impl.ByteArrayProtocol;
import de.jackwhite20.cascade.shared.protocol.listener.PacketHandler;
import de.jackwhite20.cascade.shared.protocol.listener.PacketListener;
import de.jackwhite20.cascade.shared.session.ProtocolType;
import de.jackwhite20.cascade.shared.session.Session;

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

        // Create a new instance of Client and parse in CascadeSettings
        client = new Client(new CascadeSettings.Builder()
                // You can also enable TCP_NODELAY like so
                .withOption(StandardSocketOptions.TCP_NODELAY, true)
                // Set the protocol to the predefined ByteArrayProtocol and pass the packet listener to it
                .withProtocol(new ByteArrayProtocol(this))
                // Build the settings
                .build());

        // Connect the the host ip and port and set the timeout to 2000
        if (!client.connect(host, port, 2000)) {
            System.err.println("Could not connect to " + host + ":" + port);
            return;
        }

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
