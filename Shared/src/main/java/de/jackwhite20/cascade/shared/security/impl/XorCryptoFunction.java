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

package de.jackwhite20.cascade.shared.security.impl;

import de.jackwhite20.cascade.shared.pipeline.handler.crypto.xor.XorDecryptionHandler;
import de.jackwhite20.cascade.shared.pipeline.handler.crypto.xor.XorEncryptionHandler;
import de.jackwhite20.cascade.shared.security.Algorithm;
import de.jackwhite20.cascade.shared.security.CryptoFunction;

/**
 * Created by JackWhite20 on 14.10.2016.
 */
public class XorCryptoFunction extends CryptoFunction {

    public XorCryptoFunction(byte[] key) {

        super(key, new XorEncryptionHandler(), new XorDecryptionHandler());

        // TODO: 14.10.2016 Cleaner
        ((XorEncryptionHandler) encoder).setCryptoFunction(this);
        ((XorDecryptionHandler) decoder).setCryptoFunction(this);
    }

    @Override
    public byte[] encrypt(byte[] bytes) {

        return Algorithm.xor(key, bytes);
    }

    @Override
    public byte[] decrypt(byte[] bytes) {

        return Algorithm.xor(key, bytes);
    }
}
