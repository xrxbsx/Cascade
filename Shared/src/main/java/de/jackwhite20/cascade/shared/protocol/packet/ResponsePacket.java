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

package de.jackwhite20.cascade.shared.protocol.packet;

import de.jackwhite20.cascade.shared.protocol.io.PacketReader;
import de.jackwhite20.cascade.shared.protocol.io.PacketWriter;

/**
 * Created by JackWhite20 on 13.01.2016.
 */
public abstract class ResponsePacket extends Packet {

    private int callbackId;

    public ResponsePacket(int callbackId) {

        this.callbackId = callbackId;
    }

    @Override
    public void read(PacketReader reader) throws Exception {

        callbackId = reader.readInt();
    }

    @Override
    public void write(PacketWriter writer) throws Exception {

        writer.writeInt(callbackId);
    }

    public int callbackId() {

        return callbackId;
    }
}
