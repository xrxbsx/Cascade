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

package de.jackwhite20.cascade.server;

import de.jackwhite20.cascade.server.settings.ServerSettings;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

/**
 * Created by JackWhite20 on 03.10.2015.
 */
public class Server {

    private ServerSettings settings;

    private ServerThread serverThread;

    public Server(ServerSettings settings) {

        this.settings = settings;
        this.serverThread = new ServerThread(settings);
    }

    public Future<ServerThread> bind(InetSocketAddress inetSocketAddress) {

        return serverThread.bind(inetSocketAddress);
    }

    public Future<ServerThread> bind(String host, int port) {

        return serverThread.bind(host, port);
    }

    public void stop() {

        serverThread.shutdown();
    }

    public InetSocketAddress address() {

        return serverThread.address();
    }

    public ServerSettings settings() {

        return settings;
    }
}
