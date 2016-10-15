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

import de.jackwhite20.cascade.shared.security.impl.AesCryptoFunction;
import de.jackwhite20.cascade.shared.security.impl.XorCryptoFunction;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * Created by JackWhite20 on 14.10.2016.
 */
public abstract class CryptoFunction {

    protected byte[] key;

    protected MessageToMessageEncoder<ByteBuf> encoder;

    protected MessageToMessageDecoder<ByteBuf> decoder;

    public CryptoFunction(byte[] key, MessageToMessageEncoder<ByteBuf> encoder, MessageToMessageDecoder<ByteBuf> decoder) {

        this.key = key;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public byte[] getKey() {

        return key;
    }

    public MessageToMessageEncoder<ByteBuf> getEncoder() {

        return encoder;
    }

    public MessageToMessageDecoder<ByteBuf> getDecoder() {

        return decoder;
    }

    public abstract byte[] encrypt(byte[] bytes) throws Exception;

    public abstract byte[] decrypt(byte[] bytes) throws Exception;

    public static CryptoFunction xor(byte[] key) {

        return new XorCryptoFunction(key);
    }

    public static CryptoFunction aes(byte[] key) {

        if (key.length != 16) {
            throw new IllegalArgumentException("key length must be 16 bytes");
        }

        return new AesCryptoFunction(key);
    }
}
