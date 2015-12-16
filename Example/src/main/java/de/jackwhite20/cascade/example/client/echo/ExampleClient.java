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

package de.jackwhite20.cascade.example.client.echo;

import de.jackwhite20.cascade.client.Client;
import de.jackwhite20.cascade.shared.CascadeSettings;

import java.net.StandardSocketOptions;

/**
 * Created by JackWhite20 on 07.11.2015.
 */
public class ExampleClient {

    public static void main(String[] args) {

        new ExampleClient("localhost", 12345).connect();
    }

    private String host;

    private int port;

    private Client client;

    public ExampleClient(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void connect() {

        // Create a new instance of Client and parse in CascadeSettings
        client = new Client(new CascadeSettings.Builder()
                // Add a new session listener to handle incoming packets
                .withListener(new ClientListener())
                // You can also enable TCP_NODELAY like so
                .withOption(StandardSocketOptions.TCP_NODELAY, true)
                .build());

        // Connect the the host ip and port and set the timeout to 2000
        if (!client.connect(host, port, 2000)) {
            System.err.println("Could not connect to " + host + ":" + port);
            return;
        }

        System.out.println("Connected!");

        String message = "Hey my friend.";
        System.out.println("Sending to Server: " + message);
        // Send the message reliable (TCP) to the server
        client.sendReliable(message.getBytes());
    }
}
