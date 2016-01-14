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

package de.jackwhite20.cascade.example.server.callback;

import de.jackwhite20.cascade.server.Server;
import de.jackwhite20.cascade.shared.CascadeSettings;

import java.net.StandardSocketOptions;

/**
 * Created by JackWhite20 on 14.01.2016.
 */
public class CallbackServer {

    public static void main(String[] args) {

        new CallbackServer("0.0.0.0", 12345).start();
    }

    private String host;

    private int port;

    private Server server;

    public CallbackServer(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void start() {

        // Create a new instance of Server and parse in CascadeSettings
        server = new Server(new CascadeSettings.Builder()
                // Set the backlog from the server to 200
                .withBackLog(200)
                // Set the count of threads that will handle read operations
                .withSelectorCount(2)
                // Set the protocol to the CallbackServerProtocol
                .withProtocol(new CallbackServerProtocol(new CallbackServerPacketListener(this)))
                // You can also enable TCP_NODELAY like this
                .withOption(StandardSocketOptions.TCP_NODELAY, true)
                // Build the settings
                .build());

        try {
            // Bind the server to the address and port and start listening
            server.bind(host, port, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Server started!");
    }

    public Server server() {

        return server;
    }
}
