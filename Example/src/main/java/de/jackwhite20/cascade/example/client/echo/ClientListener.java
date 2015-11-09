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

import de.jackwhite20.cascade.shared.session.ProtocolType;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListenerAdapter;

/**
 * Created by JackWhite20 on 07.11.2015.
 */
public class ClientListener extends SessionListenerAdapter {

    private final Object connectLock;

    public ClientListener(Object connectLock) {

        this.connectLock = connectLock;
    }

    @Override
    public void onException(Session session, Throwable throwable) {

        System.err.println("Exception from " + session.id() + ":");
        throwable.printStackTrace();
    }

    @Override
    public void onReceived(Session session, byte[] buffer, ProtocolType protocolType) {

        System.out.println("Received from Server: " + new String(buffer));

        session.close();
    }

    @Override
    public void onDisconnected(Session session) {

        System.out.println("Disconnected!");
    }

    @Override
    public void onConnected(Session session) {

        synchronized (connectLock) {
            connectLock.notify();
        }
    }
}