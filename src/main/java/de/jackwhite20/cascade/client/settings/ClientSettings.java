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

package de.jackwhite20.cascade.client.settings;

import de.jackwhite20.cascade.client.listener.ClientListener;

/**
 * Created by JackWhite20 on 26.07.2015.
 */
public class ClientSettings {

    private String name;

    private int tcpBufferSize;

    private int udpBufferSize;

    private ClientListener listener;

    public String name() {
        
        return name;
    }

    public int tcpBufferSize() {

        return tcpBufferSize;
    }

    public int udpBufferSize() {

        return udpBufferSize;
    }

    public ClientListener listener() {

        return listener;
    }

    public static class Builder {

        private static ClientSettings instance = new ClientSettings();

        public Builder withName(String name) {

            instance.name = name;

            return this;
        }

        public Builder withTcpBufferSize(int tcpBufferSize) {

            instance.tcpBufferSize = tcpBufferSize;

            return this;
        }

        public Builder withUdpBufferSize(int udpBufferSize) {

            instance.udpBufferSize = udpBufferSize;

            return this;
        }

        public Builder withListener(ClientListener listener) {

            instance.listener = listener;

            return this;
        }

        public ClientSettings build() {

            return instance;
        }
    }
}
