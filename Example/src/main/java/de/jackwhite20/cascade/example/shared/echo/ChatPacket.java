/*
 * Copyright (c) 2016 "JackWhite20"
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

package de.jackwhite20.cascade.example.shared.echo;

import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.PacketInfo;
import io.netty.buffer.ByteBuf;

/**
 * Created by JackWhite20 on 02.01.2016.
 *
 * The PacketInfo annotation is used set a packet id.
 */
@PacketInfo(0)
public class ChatPacket extends Packet {

    private int id;

    private String message;

    private TestObject testObject;

    /**
     * The default constructor in classes which extends Packet are important.
     */
    public ChatPacket() {

    }

    public ChatPacket(int id, String message, TestObject testObject) {

        this.id = id;
        this.message = message;
        this.testObject = testObject;
    }

    @Override
    public void read(ByteBuf byteBuf) throws Exception {

        id = byteBuf.readInt();

        // Set the max length to read
        message = readString(byteBuf, 14);

        testObject = readObject(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) throws Exception {

        byteBuf.writeInt(id);

        // Set the max length to send
        writeString(byteBuf, message, 14);

        writeObject(byteBuf, testObject);
    }

    public int getId() {

        return id;
    }

    public String getMessage() {

        return message;
    }

    public TestObject getTestObject() {

        return testObject;
    }

    @Override
    public String toString() {

        return "ChatPacket{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", testObject=" + testObject +
                '}';
    }
}
