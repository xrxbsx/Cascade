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

import de.jackwhite20.cascade.example.shared.echo.ChatPacket;
import de.jackwhite20.cascade.shared.protocol.listener.PacketHandler;
import de.jackwhite20.cascade.shared.protocol.listener.PacketListener;
import de.jackwhite20.cascade.shared.session.Session;

/**
 * Created by JackWhite20 on 02.01.2016.
 */
public class EchoClientPacketListener implements PacketListener {

    /**
     * The method needs a @PacketHandler annotation, a session and as third param the ProtocolType if you need it.
     * The second param needs to be your packet class for which this method is responsible for.
     */
    @PacketHandler
    public void onChatPacket(Session session, ChatPacket chatPacket) {

        System.out.println("Received from Server: " + chatPacket.message());
    }
}
