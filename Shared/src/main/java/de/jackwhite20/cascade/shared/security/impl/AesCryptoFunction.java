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

import de.jackwhite20.cascade.shared.pipeline.handler.crypto.aes.AesDecryptionHandler;
import de.jackwhite20.cascade.shared.pipeline.handler.crypto.aes.AesEncryptionHandler;
import de.jackwhite20.cascade.shared.security.CryptoFunction;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by JackWhite20 on 14.10.2016.
 */
public class AesCryptoFunction extends CryptoFunction {

    private Cipher cipher;

    private Key keySpec;

    public AesCryptoFunction(byte[] key) {

        super(key, new AesEncryptionHandler(), new AesDecryptionHandler());

        // TODO: 14.10.2016 Cleaner
        ((AesEncryptionHandler) encoder).setCryptoFunction(this);
        ((AesDecryptionHandler) decoder).setCryptoFunction(this);

        try {
            this.cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        this.keySpec = new SecretKeySpec(key, "AES");
    }

    @Override
    public byte[] encrypt(byte[] bytes) throws Exception {

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return cipher.doFinal(bytes);
    }

    @Override
    public byte[] decrypt(byte[] bytes) throws Exception {

        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return cipher.doFinal(bytes);
    }
}
