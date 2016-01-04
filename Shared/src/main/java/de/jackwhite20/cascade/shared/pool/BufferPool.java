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

package de.jackwhite20.cascade.shared.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by JackWhite20 on 31.12.2015.
 */
public class BufferPool {

    private static final int THRESHOLD = 250;

    private static final List<ByteBuf> POOL = Collections.synchronizedList(new ArrayList<>());

    public static ByteBuf acquire(int capacity) {

        if(capacity < THRESHOLD)
            return new ByteBuf(capacity);

        synchronized (POOL) {
            Iterator<ByteBuf> iterator = POOL.iterator();
            while (iterator.hasNext()) {
                ByteBuf byteBuf = iterator.next();
                if (byteBuf.limit() >= capacity) {
                    iterator.remove();
                    byteBuf.limit(capacity);
                    return byteBuf;
                }
            }
        }

        return new ByteBuf(capacity);
    }

    public static void release(ByteBuf byteBuffer) {

        if(byteBuffer.limit() < THRESHOLD)
            return;

        // First clear
        byteBuffer.clear();
        // Set the limit to the start capacity afterwards to set the real limit after an acquire
        byteBuffer.limit(byteBuffer.startCapacity());

        POOL.add(byteBuffer);
    }

    public static void clear() {

        POOL.clear();
    }
}
