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

import de.jackwhite20.cascade.example.shared.echo.ChatPacket;
import de.jackwhite20.cascade.server.Server;
import de.jackwhite20.cascade.shared.CascadeSettings;
import de.jackwhite20.cascade.shared.protocol.listener.PacketHandler;
import de.jackwhite20.cascade.shared.protocol.listener.PacketListener;
import de.jackwhite20.cascade.shared.session.ProtocolType;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListenerAdapter;
import de.jackwhite20.cascade.shared.session.impl.SessionImpl;

import java.net.StandardSocketOptions;

/**
 * Created by JackWhite20 on 07.11.2015.
 */
public class EchoServer extends SessionListenerAdapter implements PacketListener {

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

        // Create a new instance of Server and parse in CascadeSettings
        server = new Server(new CascadeSettings.Builder()
                // Set the backlog from the server to 200
                .withBackLog(200)
                // Set the count of threads that will handle read operations
                .withSelectorCount(4)
                // Set the session listener
                .withListener(this)
                // Set the protocol to the EchoServerProtocol and pass the packet listener to it
                .withProtocol(new EchoServerProtocol(this))
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

    @Override
    public void onException(Session session, Throwable throwable) {

        System.err.println("Exception from " + session.id() + ":");
        throwable.printStackTrace();
    }

    @Override
    public void onDisconnected(Session session) {

        System.out.println(session.id() + " disconnected!");
    }

    @Override
    public void onConnected(Session session) {

        System.out.println(session.id() + " connected!");
    }

    /**
     * The method needs a @PacketHandler annotation, a session and as third param the ProtocolType.
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
