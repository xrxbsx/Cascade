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

import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by JackWhite20 on 12.01.2016.
 */
public class PacketReaderWriterTest {

    @Test
    public void testWriteReadEnum() throws Exception {

        PacketWriter packetWriter = new PacketWriter();
        packetWriter.writeEnum(Color.BLUE);
        byte[] output = packetWriter.bytes();

        assertTrue(output != null);

        PacketReader packetReader = new PacketReader(output);
        Color color = packetReader.readEnum(Color.class);

        assertEquals(color, Color.BLUE);
    }

    @Test
    public void testWriteReadDate() throws Exception {

        Date currentDate = new Date();

        PacketWriter packetWriter = new PacketWriter();
        packetWriter.writeDate(currentDate);

        PacketReader packetReader = new PacketReader(packetWriter.bytes());
        Date receivedDate = packetReader.readDate();

        assertEquals(currentDate, receivedDate);
    }

    @Test
    public void testWriteReadUUID() throws Exception {

        UUID currentUUID = UUID.randomUUID();

        PacketWriter packetWriter = new PacketWriter();
        packetWriter.writeUUID(currentUUID);

        PacketReader packetReader = new PacketReader(packetWriter.bytes());
        UUID receivedUUID = packetReader.readUUID();

        assertEquals(currentUUID, receivedUUID);
    }

    @Test
    public void testWriteReadString() throws Exception {

        String string = "Hello";

        PacketWriter packetWriter = new PacketWriter();
        packetWriter.writeString(string);

        PacketReader packetReader = new PacketReader(packetWriter.bytes());
        String receivedString = packetReader.readString();

        assertEquals(string, receivedString);
    }

    public enum Color {
        BLUE
    }
}
