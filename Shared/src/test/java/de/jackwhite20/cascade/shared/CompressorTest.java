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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by JackWhite20 on 19.12.2015.
 */
public class CompressorTest {

    private Compressor compressor;

    private byte[] original;

    @Before
    public void before() {

        compressor = new Compressor();
        original = "I am a biiiiiiiig randooooom string i think. I am here to test the compression stuff.".getBytes();
    }

    @Test
    public void testCompressorCompressDecompress() {

        byte[] compressed = compressor.compress(original);
        byte[] decompressed = compressor.decompress(compressed);

        assertTrue(compressed.length < original.length);
        assertTrue(decompressed.length > compressed.length);
        assertArrayEquals(original, decompressed);
    }
}
