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

package de.jackwhite20.cascade.example.server.bytes;

import de.jackwhite20.cascade.server.Server;
import de.jackwhite20.cascade.server.ServerFactory;
import de.jackwhite20.cascade.shared.protocol.impl.ByteArrayPacket;
import de.jackwhite20.cascade.shared.protocol.impl.ByteArrayProtocol;
import de.jackwhite20.cascade.shared.protocol.listener.PacketHandler;
import de.jackwhite20.cascade.shared.protocol.listener.PacketListener;
import de.jackwhite20.cascade.shared.session.impl.ProtocolType;
import de.jackwhite20.cascade.shared.session.impl.SessionImpl;

/**
 * Created by JackWhite20 on 03.01.2016.
 */
public class ByteArrayServer implements PacketListener {

    public static void main(String[] args) {

        new ByteArrayServer("0.0.0.0", 12345).start();
    }

    private String host;

    private int port;

    private Server server;

    public ByteArrayServer(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void start() {

        server = ServerFactory.create(host, port, 200, 4, new ByteArrayProtocol(this));
        server.start();

        System.out.println("Server started!");
    }

    @PacketHandler
    public void onByteArrayPacket(SessionImpl session, ByteArrayPacket byteArrayPacket, ProtocolType type) {

        byte[] bytes = byteArrayPacket.bytes();

        System.out.println("Received byte array string from client: " + new String(bytes));

        session.send(new ByteArrayPacket(bytes), type);
    }

    public Server server() {

        return server;
    }
}
