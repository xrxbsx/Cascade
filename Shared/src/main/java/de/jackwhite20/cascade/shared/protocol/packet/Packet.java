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

package de.jackwhite20.cascade.shared.protocol.packet;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

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

    public void writeArrayListString(ByteBuf byteBuf, ArrayList<String> list) throws UnsupportedEncodingException {

        byteBuf.writeInt(list.size());
        for (String s : list) {
            writeString(byteBuf, s);
        }
    }

    public ArrayList<String> readArrayListString(ByteBuf byteBuf) throws UnsupportedEncodingException {

        ArrayList<String> list = new ArrayList<>();

        int length = byteBuf.readInt();

        for (int i = 0; i < length; i++) {
            list.add(readString(byteBuf));
        }

        return list;
    }

    public void writeArrayListInteger(ByteBuf byteBuf, ArrayList<Integer> list) throws UnsupportedEncodingException {

        byteBuf.writeInt(list.size());
        list.forEach(byteBuf::writeInt);
    }

    public ArrayList<Integer> readArrayListInteger(ByteBuf byteBuf) throws UnsupportedEncodingException {

        ArrayList<Integer> list = new ArrayList<>();

        int length = byteBuf.readInt();

        for (int i = 0; i < length; i++) {
            list.add(byteBuf.readInt());
        }

        return list;
    }

    public void writeHashMapString(ByteBuf byteBuf, HashMap<String, String> hashMap) throws UnsupportedEncodingException {

        byteBuf.writeInt(hashMap.size());
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            writeString(byteBuf, entry.getKey());
            writeString(byteBuf, entry.getValue());
        }
    }

    public HashMap<String, String> readHashMapString(ByteBuf byteBuf) throws UnsupportedEncodingException {

        HashMap<String, String> hashMap = new HashMap<>();

        int length = byteBuf.readInt();
        for (int i = 0; i < length; i++) {
            hashMap.put(readString(byteBuf), readString(byteBuf));
        }

        return hashMap;
    }

    public <V extends Enum> void writeEnum(ByteBuf byteBuf, V v) throws IOException {

        byteBuf.writeInt(v.ordinal());
    }

    public <V extends Enum> V readEnum(ByteBuf byteBuf, Class<V> clazz) throws IOException {

        return clazz.getEnumConstants()[byteBuf.readInt()];
    }

    public void writeUUID(ByteBuf byteBuf, UUID uuid) {

        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID(ByteBuf byteBuf) {

        return new UUID(byteBuf.readLong(), byteBuf.readLong());
    }

    public void writeDate(ByteBuf byteBuf, Date date) {

        byteBuf.writeLong(date.getTime());
    }

    public Date readDate(ByteBuf byteBuf) {

        return new Date(byteBuf.readLong());
    }

    public void writeArrayString(ByteBuf byteBuf, String... strings) throws UnsupportedEncodingException {

        byteBuf.writeInt(strings.length);
        for (String string : strings) {
            writeString(byteBuf, string);
        }
    }

    public String[] readArrayString(ByteBuf byteBuf) throws UnsupportedEncodingException {

        int length = byteBuf.readInt();

        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            strings[i] = readString(byteBuf);
        }

        return strings;
    }

    public void writeArrayInteger(ByteBuf byteBuf, Integer... integers) throws UnsupportedEncodingException {

        byteBuf.writeInt(integers.length);
        for (Integer integer : integers) {
            byteBuf.writeInt(integer);
        }
    }

    public Integer[] readArrayInteger(ByteBuf byteBuf) {

        int length = byteBuf.readInt();

        Integer[] integers = new Integer[length];
        for (int i = 0; i < length; i++) {
            integers[i] = byteBuf.readInt();
        }

        return integers;
    }
}
