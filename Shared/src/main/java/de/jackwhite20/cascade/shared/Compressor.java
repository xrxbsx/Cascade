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

package de.jackwhite20.cascade.shared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by JackWhite20 on 16.12.2015.
 */
public class Compressor {

    public static final int BUFFER_SIZE = 2048;

    private Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);

    private Inflater inflater = new Inflater();

    /**
     * Compresses the given byte array.
     *
     * @param data the byte array to compress.
     * @return the compressed byte array.
     */
    public byte[] compress(byte[] data) {

        deflater.reset();
        deflater.setInput(data);
        deflater.finish();

        byte[] buffer = new byte[BUFFER_SIZE];

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Decompresses the given byte array.
     *
     * @param data the compressed byte array.
     * @return the decompressed byte array.
     */
    public byte[] decompress(byte[] data) {

        inflater.reset();
        inflater.setInput(data);

        byte[] buffer = new byte[BUFFER_SIZE];

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
