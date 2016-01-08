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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by JackWhite20 on 05.01.2016.
 */
public class BufferPoolTest {

    @Test
    public void testBufferPoolAcquireRelease() {

        ByteBuf byteBuf = BufferPool.acquire(5000);
        int firstHashCode = byteBuf.hashCode();
        int firstLimit = byteBuf.limit();
        byteBuf.release();
        assertEquals(firstLimit, 5000);

        ByteBuf byteBufSecond = BufferPool.acquire(2000);
        assertEquals(firstHashCode, byteBufSecond.hashCode());
        int secondLimit = byteBufSecond.limit();
        byteBufSecond.release();
        assertEquals(secondLimit, 2000);

        assertEquals(byteBufSecond.limit(), 5000);
    }

    @Test
    public void testBufferPoolClear() {

        ByteBuf byteBuf = BufferPool.acquire(5000);
        int firstHashCode = byteBuf.hashCode();
        byteBuf.release();

        BufferPool.clear();

        ByteBuf buf = BufferPool.acquire(5000);
        int secondHashCode = buf.hashCode();
        buf.release();

        assertTrue(firstHashCode != secondHashCode);
    }
}
