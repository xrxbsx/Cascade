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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by JackWhite20 on 31.12.2015.
 */
public class BufferPool {

    private static final int THRESHOLD = 250;

    private static final List<ByteBuffer> POOL = Collections.synchronizedList(new ArrayList<>());

    public static ByteBuffer acquire(int capacity) {

        if(capacity < THRESHOLD)
            return ByteBuffer.allocate(capacity);

        synchronized (POOL) {
            Iterator<ByteBuffer> iterator = POOL.iterator();
            while (iterator.hasNext()) {
                ByteBuffer byteBuffer = iterator.next();

                if (byteBuffer.limit() >= capacity) {
                    iterator.remove();
                    return byteBuffer;
                }
            }
        }

        return ByteBuffer.allocate(capacity);
    }

    public static void release(ByteBuffer byteBuffer) {

        if(byteBuffer.limit() < THRESHOLD)
            return;

        synchronized (POOL) {
            POOL.add(byteBuffer);
        }
    }
}
