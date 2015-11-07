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

package de.jackwhite20.cascade.example.server;

import de.jackwhite20.cascade.server.Server;
import de.jackwhite20.cascade.shared.CascadeSettings;
import de.jackwhite20.cascade.shared.session.ProtocolType;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListenerAdapter;

/**
 * Created by JackWhite20 on 07.11.2015.
 */
public class ExampleServer extends SessionListenerAdapter {

    public static void main(String[] args) {

        new ExampleServer("0.0.0.0", 12345).start();
    }

    private String host;

    private int port;

    private Server server;

    public ExampleServer(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void start() {

        server = new Server(new CascadeSettings.Builder()
                .withBackLog(200)
                .withSelectorCount(4)
                .withListener(this)
                .build());

        try {
            server.bind(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Server started!");
    }

    @Override
    public void onException(Session session, Throwable throwable) {

        System.err.println("Exception from " + session.id() + ":");
        throwable.printStackTrace();
    }

    @Override
    public void onReceived(Session session, byte[] buffer, ProtocolType protocolType) {

        if(protocolType == ProtocolType.UDP) {
            session.sendUnreliable(buffer);
        }else {
            session.sendReliable(buffer);
        }
    }

    @Override
    public void onDisconnected(Session session) {

        System.out.println(session.id() + " disconnected!");
    }

    @Override
    public void onConnected(Session session) {

        System.out.println(session.id() + " connected!");
    }
}
