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

package de.jackwhite20.cascade.shared.protocol.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by JackWhite20 on 02.01.2016.
 */
public class PacketWriter extends DataOutputStream {

    private ByteArrayOutputStream byteArrayOutputStream;

    public PacketWriter(ByteArrayOutputStream out) {

        super(out);

        this.byteArrayOutputStream = out;
    }

    public PacketWriter() {

        this(new ByteArrayOutputStream());
    }

    public void writeUUID(UUID uuid) throws IOException {

        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public void writeDate(Date date) throws IOException {

        writeLong(date.getTime());
    }

    public <V extends Enum> void writeEnum(V v) throws IOException {

        writeInt(v.ordinal());
    }

    public void writeString(String string) throws IOException {

        writeUTF(string);
    }

    public byte[] bytes() {

        return byteArrayOutputStream.toByteArray();
    }
}
