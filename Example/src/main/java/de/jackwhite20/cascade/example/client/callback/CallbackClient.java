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

package de.jackwhite20.cascade.example.client.callback;

import de.jackwhite20.cascade.client.Client;
import de.jackwhite20.cascade.example.shared.callback.TestRequestPacket;
import de.jackwhite20.cascade.example.shared.callback.TestResponsePacket;
import de.jackwhite20.cascade.shared.CascadeSettings;

import java.net.StandardSocketOptions;

/**
 * Created by JackWhite20 on 14.01.2016.
 */
public class CallbackClient {

    public static void main(String[] args) {

        new CallbackClient("localhost", 12345).connect();
    }

    private String host;

    private int port;

    private Client client;

    public CallbackClient(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void connect() {

        // Create a new instance of Client and parse in CascadeSettings
        client = new Client(new CascadeSettings.Builder()
                // You can also enable TCP_NODELAY like so
                .withOption(StandardSocketOptions.TCP_NODELAY, true)
                // Set the protocol to our CallbackClientProtocol
                .withProtocol(new CallbackClientProtocol())
                // Build the settings
                .build());

        // Connect the the host ip and port and set the timeout to 2000
        if (!client.connect(host, port, 2000)) {
            System.err.println("Could not connect to " + host + ":" + port);
            return;
        }

        System.out.println("Connected!");

        // Send a request packet with lambda
        client.<TestResponsePacket>send(new TestRequestPacket(5), packet -> {
            System.out.println("Name: " + packet.name());
            System.out.println("Description: " + packet.description());
        });

        // The alternative way
/*        client.send(new TestRequestPacket(5), new PacketCallback<TestResponsePacket>() {

            @Override
            public void receive(TestResponsePacket packet) {
                System.out.println("Name: " + packet.name());
                System.out.println("Description: " + packet.description());
            }
        });*/
    }
}
