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

import java.nio.*;

/**
 * Created by JackWhite20 on 03.01.2016.
 */
public class ByteBuf {

    private ByteBuffer byteBuffer;

    private int startLimit;

    /**
     * Creates a new instance of ByteBuf where the wrapped ByteBuffer will have the given capacity.
     *
     * @param capacity the initial capacity.
     */
    public ByteBuf(int capacity) {

        this.startLimit = capacity;
        this.byteBuffer = ByteBuffer.allocate(capacity);
}

    /**
     * Creates a new wrapped instance of ByteBuf with the given ByteBuffer instance.
     *
     * @param byteBuffer the byte buffer to wrap.
     */
    public ByteBuf(ByteBuffer byteBuffer) {

        this.startLimit = byteBuffer.limit();
        this.byteBuffer = byteBuffer;
    }

    /**
     * Gets the wrapped NIO byte buffer.
     *
     * @return the ByteBuffer.
     */
    public ByteBuffer nioBuffer() {

        return byteBuffer;
    }

    /**
     * Releases the buffer to the pool.
     */
    public void release() {

        BufferPool.release(this);
    }

    /**
     * Gets the start capacity.
     *
     * @return the start capacity.
     */
    public int startCapacity() {

        return startLimit;
    }

    public static ByteBuffer allocateDirect(int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }

    public int compareTo(ByteBuffer that) {
        return byteBuffer.compareTo(that);
    }

    public double getDouble(int index) {
        return byteBuffer.getDouble(index);
    }

    public ByteBuffer get(byte[] dst) {
        return byteBuffer.get(dst);
    }

    public boolean hasArray() {
        return byteBuffer.hasArray();
    }

    public Buffer flip() {
        return byteBuffer.flip();
    }

    public ByteBuffer putShort(int index, short value) {
        return byteBuffer.putShort(index, value);
    }

    public static ByteBuffer wrap(byte[] array, int offset, int length) {
        return ByteBuffer.wrap(array, offset, length);
    }

    public ByteBuffer putInt(int value) {
        return byteBuffer.putInt(value);
    }

    public int getInt() {
        return byteBuffer.getInt();
    }

    public Buffer clear() {
        return byteBuffer.clear();
    }

    public ByteBuffer put(byte[] src, int offset, int length) {
        return byteBuffer.put(src, offset, length);
    }

    public ByteBuffer putFloat(float value) {
        return byteBuffer.putFloat(value);
    }

    public ByteBuffer order(ByteOrder bo) {
        return byteBuffer.order(bo);
    }

    public ShortBuffer asShortBuffer() {
        return byteBuffer.asShortBuffer();
    }

    public byte get() {
        return byteBuffer.get();
    }

    public boolean isDirect() {
        return byteBuffer.isDirect();
    }

    public ByteBuffer compact() {
        return byteBuffer.compact();
    }

    public ByteBuffer putDouble(double value) {
        return byteBuffer.putDouble(value);
    }

    public float getFloat() {
        return byteBuffer.getFloat();
    }

    public ByteBuffer asReadOnlyBuffer() {
        return byteBuffer.asReadOnlyBuffer();
    }

    public ByteBuffer putChar(char value) {
        return byteBuffer.putChar(value);
    }

    public static ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocate(capacity);
    }

    public static ByteBuffer wrap(byte[] array) {
        return ByteBuffer.wrap(array);
    }

    public ByteBuffer putLong(int index, long value) {
        return byteBuffer.putLong(index, value);
    }

    public DoubleBuffer asDoubleBuffer() {
        return byteBuffer.asDoubleBuffer();
    }

    public byte get(int index) {
        return byteBuffer.get(index);
    }

    public int remaining() {
        return byteBuffer.remaining();
    }

    public ByteBuffer put(int index, byte b) {
        return byteBuffer.put(index, b);
    }

    public char getChar() {
        return byteBuffer.getChar();
    }

    public ByteBuffer putChar(int index, char value) {
        return byteBuffer.putChar(index, value);
    }

    public ByteOrder order() {
        return byteBuffer.order();
    }

    public double getDouble() {
        return byteBuffer.getDouble();
    }

    public FloatBuffer asFloatBuffer() {
        return byteBuffer.asFloatBuffer();
    }

    public boolean hasRemaining() {
        return byteBuffer.hasRemaining();
    }

    public ByteBuffer put(byte[] src) {
        return byteBuffer.put(src);
    }

    public int position() {
        return byteBuffer.position();
    }

    public float getFloat(int index) {
        return byteBuffer.getFloat(index);
    }

    public Buffer limit(int newLimit) {
        return byteBuffer.limit(newLimit);
    }

    public Buffer rewind() {
        return byteBuffer.rewind();
    }

    public Buffer mark() {
        return byteBuffer.mark();
    }

    public ByteBuffer putDouble(int index, double value) {
        return byteBuffer.putDouble(index, value);
    }

    public byte[] array() {
        return byteBuffer.array();
    }

    public boolean isReadOnly() {
        return byteBuffer.isReadOnly();
    }

    public int capacity() {
        return byteBuffer.capacity();
    }

    public CharBuffer asCharBuffer() {
        return byteBuffer.asCharBuffer();
    }

    public ByteBuffer putInt(int index, int value) {
        return byteBuffer.putInt(index, value);
    }

    public ByteBuffer duplicate() {
        return byteBuffer.duplicate();
    }

    public ByteBuffer putLong(long value) {
        return byteBuffer.putLong(value);
    }

    public ByteBuffer put(ByteBuffer src) {
        return byteBuffer.put(src);
    }

    public LongBuffer asLongBuffer() {
        return byteBuffer.asLongBuffer();
    }

    public Buffer reset() {
        return byteBuffer.reset();
    }

    public ByteBuffer slice() {
        return byteBuffer.slice();
    }

    public int getInt(int index) {
        return byteBuffer.getInt(index);
    }

    public Buffer position(int newPosition) {
        return byteBuffer.position(newPosition);
    }

    public long getLong() {
        return byteBuffer.getLong();
    }

    public short getShort(int index) {
        return byteBuffer.getShort(index);
    }

    public int arrayOffset() {
        return byteBuffer.arrayOffset();
    }

    public int limit() {
        return byteBuffer.limit();
    }

    public ByteBuffer get(byte[] dst, int offset, int length) {
        return byteBuffer.get(dst, offset, length);
    }

    public short getShort() {
        return byteBuffer.getShort();
    }

    public long getLong(int index) {
        return byteBuffer.getLong(index);
    }

    public ByteBuffer put(byte b) {
        return byteBuffer.put(b);
    }

    public IntBuffer asIntBuffer() {
        return byteBuffer.asIntBuffer();
    }

    public ByteBuffer putShort(short value) {
        return byteBuffer.putShort(value);
    }

    public ByteBuffer putFloat(int index, float value) {
        return byteBuffer.putFloat(index, value);
    }

    public char getChar(int index) {
        return byteBuffer.getChar(index);
    }
}
