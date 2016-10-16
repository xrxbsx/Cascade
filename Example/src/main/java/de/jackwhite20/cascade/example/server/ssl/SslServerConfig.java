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

package de.jackwhite20.cascade.example.server.ssl;

import de.jackwhite20.cascade.server.impl.ServerConfig;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import io.netty.handler.ssl.SslContext;

import java.net.StandardSocketOptions;

/**
 * Created by JackWhite20 on 16.10.2016.
 */
public class SslServerConfig extends ServerConfig {

    public SslServerConfig(Protocol protocol, SslContext sslContext) {

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
        // Set the ssl context
        sslContext(sslContext);
    }
}
