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

package de.jackwhite20.cascade.example.shared.callback;

import de.jackwhite20.cascade.shared.protocol.io.PacketReader;
import de.jackwhite20.cascade.shared.protocol.io.PacketWriter;
import de.jackwhite20.cascade.shared.protocol.packet.PacketInfo;
import de.jackwhite20.cascade.shared.protocol.packet.RequestPacket;

/**
 * Created by JackWhite20 on 14.01.2016.
 */
@PacketInfo(id = 10)
public class TestRequestPacket extends RequestPacket {

    // Some example data for the request
    private int id;

    /**
     * The default constructor in classes which extends Packet are important.
     */
    public TestRequestPacket() {

    }

    public TestRequestPacket(int id) {

        this.id = id;
    }

    @Override
    public void read(PacketReader reader) throws Exception {

        // The call to super.read(reader) method is needed first
        super.read(reader);
        id = reader.readInt();
    }

    @Override
    public void write(PacketWriter writer) throws Exception {

        // The call to super.write(writer) method is needed first
        super.write(writer);
        writer.writeInt(id);
    }

    public int id() {

        return id;
    }
}
