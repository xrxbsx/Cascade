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

package de.jackwhite20.cascade.shared.protocol.packet.internal;

import de.jackwhite20.cascade.shared.protocol.io.PacketReader;
import de.jackwhite20.cascade.shared.protocol.io.PacketWriter;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.PacketInfo;

/**
 * Created by JackWhite20 on 02.01.2016.
 */
@PacketInfo(id = -128)
public class UDPPortPacket extends Packet {

    private int port;

    public UDPPortPacket() {

    }

    public UDPPortPacket(int port) {

        this.port = port;
    }

    @Override
    public void read(PacketReader reader) throws Exception {

        port = reader.readInt();
    }

    @Override
    public void write(PacketWriter writer) throws Exception {

        writer.writeInt(port);
    }

    public int port() {

        return port;
    }
}
