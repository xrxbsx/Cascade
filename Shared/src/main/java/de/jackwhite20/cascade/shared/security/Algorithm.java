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

package de.jackwhite20.cascade.shared.security;

/**
 * Created by JackWhite20 on 14.10.2016.
 */
public final class Algorithm {

    private Algorithm() {
        // No instance
    }

    /**
     * Returns the xor version of the given input with the given key.
     *
     * @param key The key.
     * @param input The input.
     * @return The xor result byte array.
     */
    public static byte[] xor(byte[] key, byte[] input) {

        byte[] out = new byte[input.length];

        for(int i = 0; i < input.length; i++) {
            out[i] = (byte) (input[i] ^ key[i % key.length]);
        }

        return out;
    }
}
