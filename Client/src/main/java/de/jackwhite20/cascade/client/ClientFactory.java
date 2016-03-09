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

package de.jackwhite20.cascade.client;

import de.jackwhite20.cascade.client.impl.ClientConfig;
import de.jackwhite20.cascade.client.impl.ClientImpl;
import de.jackwhite20.cascade.shared.Config;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.session.SessionListener;

import java.util.List;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public class ClientFactory {

    public static Client create(ClientConfig clientConfig) {

        return new ClientImpl(clientConfig);
    }

    public static Client create(String host, int port, int workerThreads, Protocol protocol, List<Config.Option> options, SessionListener sessionListener) {

        return create(new DefaultClientConfig(host, port, workerThreads, protocol, options, sessionListener));
    }

    public static Client create(String host, int port, int workerThreads, Protocol protocol, SessionListener sessionListener) {

        return create(new DefaultClientConfig(host, port, workerThreads, protocol, sessionListener));
    }

    public static Client create(String host, int port, int workerThreads, Protocol protocol) {

        return create(host, port, workerThreads, protocol, null);
    }

    private static class DefaultClientConfig extends ClientConfig {

        public DefaultClientConfig(String host, int port, int workerThreads, Protocol protocol, List<Config.Option> options, SessionListener sessionListener) {

            host(host);
            port(port);
            workerThreads(workerThreads);
            protocol(protocol);
            if(options != null) {
                //noinspection unchecked
                options.forEach(option -> option(option.socketOption(), option.value()));
            }
            sessionListener(sessionListener);
        }

        public DefaultClientConfig(String host, int port, int workerThreads, Protocol protocol, SessionListener sessionListener) {

            this(host, port, workerThreads, protocol, null, sessionListener);
        }
    }
}