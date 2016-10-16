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

package de.jackwhite20.cascade.shared.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.InputStream;

/**
 * Created by JackWhite20 on 16.10.2016.
 */
public final class SslUtil {

    private SslUtil() {
        // No instance
    }

    /**
     * Returns a client ssl context with an insecure trust manager.
     *
     * @return The ssl context.
     * @throws SSLException If something went wrong.
     */
    public static SslContext insecureForClient() throws SSLException {

        return SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
    }

    /**
     * Returns a client ssl context with the given cert and key file as key manager.
     *
     * @param keyCertChainFile The key cert chain file.
     * @param keyFile The key file.
     * @return The ssl context.
     * @throws SSLException If something went wrong.
     */
    public static SslContext customForClient(File keyCertChainFile, File keyFile) throws SSLException {

        return SslContextBuilder.forClient()
                .keyManager(keyCertChainFile, keyFile)
                .build();
    }

    /**
     * Returns a client ssl context with the given cert and key input stream as key manager.
     *
     * @param keyCertChainInputStream The key cert chain input stream.
     * @param keyInputStream The key input stream.
     * @return The ssl context.
     * @throws SSLException If something went wrong.
     */
    public static SslContext customForClient(InputStream keyCertChainInputStream, InputStream keyInputStream) throws SSLException {

        return SslContextBuilder.forClient()
                .keyManager(keyCertChainInputStream, keyInputStream)
                .build();
    }

    /**
     * Returns a server ssl context with the given cert and key file as key manager.
     *
     * @param keyCertChainFile The key cert chain file.
     * @param keyFile The key file.
     * @return The ssl context.
     * @throws SSLException If something went wrong.
     */
    public static SslContext customForServer(File keyCertChainFile, File keyFile) throws SSLException {

        return SslContextBuilder.forClient()
                .keyManager(keyCertChainFile, keyFile)
                .build();
    }

    /**
     * Returns a server ssl context with the given cert and key input stream as key manager.
     *
     * @param keyCertChainInputStream The key cert chain input stream.
     * @param keyInputStream The key input stream.
     * @return The ssl context.
     * @throws SSLException If something went wrong.
     */
    public static SslContext customForServer(InputStream keyCertChainInputStream, InputStream keyInputStream) throws SSLException {

        return SslContextBuilder.forClient()
                .keyManager(keyCertChainInputStream, keyInputStream)
                .build();
    }
}
