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

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

/**
 * Created by JackWhite20 on 02.01.2016.
 */
public abstract class Packet {

    public abstract void read(ByteBuf byteBuf) throws Exception;

    public abstract void write(ByteBuf byteBuf) throws Exception;

    public String readString(ByteBuf byteBuf) throws UnsupportedEncodingException {

        byte[] bytes = new byte[byteBuf.readShort()];
        byteBuf.readBytes(bytes);

        return new String(bytes, "utf-8");
    }

    public void writeString(ByteBuf byteBuf, String string) throws UnsupportedEncodingException {

        byte[] bytes = string.getBytes("utf-8");

        byteBuf.writeShort(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
