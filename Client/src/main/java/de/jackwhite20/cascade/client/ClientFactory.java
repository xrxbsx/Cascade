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

package de.jackwhite20.cascade.client;

import de.jackwhite20.cascade.client.impl.CascadeClient;
import de.jackwhite20.cascade.client.impl.ClientConfig;
import de.jackwhite20.cascade.shared.Config;
import de.jackwhite20.cascade.shared.Options;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.session.SessionListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public class ClientFactory {

    public static Client create(ClientConfig clientConfig) {

        return new CascadeClient(clientConfig);
    }

    public static Client create(String host, int port, Protocol protocol, List<Config.Option> options) {

        return create(new DefaultClientConfig(host, port, protocol, options, null));
    }

    public static Client create(String host, int port, Protocol protocol, Options options) {

        return create(new DefaultClientConfig(host, port, protocol, options.list(), null));
    }

    public static Client create(String host, int port, Protocol protocol, List<Config.Option> options, SessionListener sessionListener) {

        return create(new DefaultClientConfig(host, port, protocol, options, sessionListener));
    }

    public static Client create(String host, int port, Protocol protocol, Options options, SessionListener sessionListener) {

        return create(new DefaultClientConfig(host, port, protocol, options.list(), sessionListener));
    }

    public static Client create(String host, int port, Protocol protocol, SessionListener sessionListener) {

        return create(new DefaultClientConfig(host, port, protocol, sessionListener));
    }

    public static Client create(String host, int port, Protocol protocol) {

        return create(host, port, protocol, Collections.emptyList(), null);
    }

    private static class DefaultClientConfig extends ClientConfig {

        public DefaultClientConfig(String host, int port, Protocol protocol, List<Config.Option> options, SessionListener... sessionListener) {

            host(host);
            port(port);
            protocol(protocol);
            if(options != null) {
                options.forEach(option -> option(option.socketOption(), option.value()));
            }
            if (sessionListener != null) {
                for (SessionListener listener : sessionListener) {
                    sessionListener(listener);
                }
            }
        }

        public DefaultClientConfig(String host, int port, Protocol protocol, SessionListener... sessionListener) {

            this(host, port, protocol, null, sessionListener);
        }
    }
}
