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

import de.jackwhite20.cascade.server.Server;
import de.jackwhite20.cascade.server.listener.ServerListenerAdapter;
import de.jackwhite20.cascade.server.session.ServerSession;
import de.jackwhite20.cascade.server.settings.ServerSettings;

/**
 * Created by JackWhite20 on 26.07.2015.
 */
public class ServerTest extends ServerListenerAdapter {

    public static void main(String[] args) throws Exception {

        new ServerTest();
    }

    private Server server;

    public ServerTest() {

        ServerSettings settings = new ServerSettings.ServerSettingsBuilder()
                .withName("CascadeServer")
                .withBackLog(200)
                .withSelectorCount(4)
                .withTcpBufferSize(1024)
                .withUdpBufferSize(1024)
                .withListener(this)
                .build();
        server = new Server(settings);

        try {
            server.bind("0.0.0.0", 12345);
        } catch (Exception e) {
            System.err.println("Error while binding: " + e.getMessage());
        }

        System.out.println("Server started!");
    }

    @Override
    public void onClientDisconnected(ServerSession session) {

        System.out.println("Client disconnected: " + session.id());
    }

    @Override
    public void onClientConnected(int clientId, ServerSession session) {

        System.out.println("Client connected: " + clientId);
    }

    @Override
    public void onReceived(ServerSession session, byte[] buffer) {

        System.out.println("Received " + buffer.length + "bytes from " + session.id());

        session.sendTCP("Pong".getBytes());
    }
}
