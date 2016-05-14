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

import de.jackwhite20.cascade.example.server.callback.CallbackServerProtocol;
import de.jackwhite20.cascade.example.shared.echo.ChatPacket;
import de.jackwhite20.cascade.server.Server;
import de.jackwhite20.cascade.server.ServerFactory;
import de.jackwhite20.cascade.shared.protocol.listener.PacketHandler;
import de.jackwhite20.cascade.shared.protocol.listener.PacketListener;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.session.impl.ProtocolType;
import de.jackwhite20.cascade.shared.session.impl.SessionImpl;

/**
 * Created by JackWhite20 on 07.11.2015.
 */
public class EchoServer implements PacketListener {

    public static void main(String[] args) {

        new EchoServer("0.0.0.0", 12345).start();
    }

    private String host;

    private int port;

    private Server server;

    public EchoServer(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void start() {

        // Create a new server instance with the given server config class
        server = ServerFactory.create(new EchoServerConfig(new CallbackServerProtocol(this)));
        // You can also pass in your option directly
        //server = ServerFactory.create(host, port, 200, 4, new EchoServerProtocol(this));
        server.sessionListener(new SessionListener() {

            @Override
            public void onConnected(Session session) {

                System.out.println(session.id() + " connected!");
            }

            @Override
            public void onDisconnected(Session session) {

                System.out.println(session.id() + " disconnected!");
            }
        });
        server.start();

        System.out.println("Server started!");
    }

    /**
     * The method needs a @PacketHandler annotation, a session and as third param the ProtocolType if you need it.
     * The second param needs to be your packet class for which this method is responsible for.
     */
    @PacketHandler
    public void onChatPacket(SessionImpl session, ChatPacket chatPacket, ProtocolType type) {

        System.out.println("Received from Client " + session.id() + ": " + chatPacket.message());

        // Send the packet back with the same ProtocolType
        session.send(chatPacket, type);
    }

    public Server server() {

        return server;
    }
}
