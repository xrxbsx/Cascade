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

package de.jackwhite20.cascade.example.server.echo;

import de.jackwhite20.cascade.server.impl.ServerConfig;
import de.jackwhite20.cascade.shared.protocol.Protocol;

import java.net.StandardSocketOptions;

/**
 * Created by JackWhite20 on 15.05.2016.
 */
public class EchoServerConfig extends ServerConfig {

    public EchoServerConfig(Protocol protocol) {

        // Set the host to bind to
        host("0.0.0.0");
        // Set the port to listen on
        port(12345);
        // Set the amount of threads for read events
        workerThreads(2);
        // Set the backlog for the incoming connections
        backlog(200);
        // Disable the Nagle algorithm
        option(StandardSocketOptions.TCP_NODELAY, true);
        // Set the protocol
        protocol(protocol);
    }
}
