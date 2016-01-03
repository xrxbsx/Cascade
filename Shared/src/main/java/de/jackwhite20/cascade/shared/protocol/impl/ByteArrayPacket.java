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

package de.jackwhite20.cascade.shared.protocol.impl;

import de.jackwhite20.cascade.shared.protocol.io.PacketReader;
import de.jackwhite20.cascade.shared.protocol.io.PacketWriter;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.PacketInfo;

/**
 * Created by JackWhite20 on 03.01.2016.
 */
@PacketInfo(id = -127)
public class ByteArrayPacket extends Packet {

    private byte[] bytes;

    public ByteArrayPacket() {

    }

    public ByteArrayPacket(byte[] bytes) {

        this.bytes = bytes;
    }

    @SuppressWarnings("all")
    @Override
    public void read(PacketReader reader) throws Exception {

        bytes = new byte[reader.readInt()];
        reader.read(bytes);
    }

    @Override
    public void write(PacketWriter writer) throws Exception {

        writer.writeInt(bytes.length);
        writer.write(bytes);
    }

    public byte[] bytes() {

        return bytes;
    }
}
