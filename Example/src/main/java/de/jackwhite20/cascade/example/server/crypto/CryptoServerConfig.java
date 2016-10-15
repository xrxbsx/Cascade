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

package de.jackwhite20.cascade.example.server.crypto;

import de.jackwhite20.cascade.server.impl.ServerConfig;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.security.CryptoFunction;

import java.net.StandardSocketOptions;

/**
 * Created by JackWhite20 on 15.10.2016.
 */
public class CryptoServerConfig extends ServerConfig {

    public CryptoServerConfig(Protocol protocol) {

        // Set the host to bind to
        host("0.0.0.0");
        // Set the port to listen on
        port(12345);
        // Set the amount of threads for read events
        workerThreads(2);
        // Set the backlog for the incoming connections
        backlog(200);
        // Disable the Nagle algorithm
        option(StandardSocketOptions.TCP_NODELAY, true);
        // Set the protocol
        protocol(protocol);

        // This is the key used for the encryption algorithm
        // You should generate the key with random bytes
        // Keep in mind that the key needs to be the same client and server side
        // So it would make sense to generate a byte array key and save it in a binary file or something
        // in order to use it client and server side
        byte[] key = {'B', 'e', 's', 't', 'K', 'e', 'y', 'E', 'v', 'e', 'r', 'X', 'P', 'M', 'Y', 'q'};

        // For AES the key needs to be 16 bytes long
        // If you use for example xor you can generate a key with your chosen length
        // After the crypto function is set, all traffic is fully encrypted with the given algorithm
        cryptoFunction(CryptoFunction.aes(key));
    }
}
