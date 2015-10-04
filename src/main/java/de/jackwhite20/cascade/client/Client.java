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

import de.jackwhite20.cascade.client.session.ClientSessionImpl;
import de.jackwhite20.cascade.client.settings.ClientSettings;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

/**
 * Created by JackWhite20 on 03.10.2015.
 */
public class Client {

    private ClientSettings settings;

    private ClientThread clientThread;

    public Client(ClientSettings settings) {

        this.settings = settings;
        this.clientThread = new ClientThread(settings);
    }

    public Future<ClientSessionImpl> connect(String host, int port) {

        return clientThread.connect(host, port);
    }

    public Future<ClientSessionImpl> connect(InetSocketAddress address) {

        return connect(address.getHostName(), address.getPort());
    }

    public boolean isConnected() {

        return clientThread.isRunning();
    }

    public void disconnect() {

        clientThread.disconnect();
    }

    public String host() {

        return clientThread.host();
    }

    public int port() {

        return clientThread.port();
    }
}
