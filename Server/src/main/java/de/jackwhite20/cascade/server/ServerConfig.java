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

package de.jackwhite20.cascade.server;

import java.net.InetSocketAddress;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public abstract class ServerConfig {

    private String host;

    private int port;

    private int backlog;

    private int selectorCount;

    public String host() {

        return host;
    }

    public int port() {

        return port;
    }

    public int backlog() {

        return backlog;
    }

    public int selectorCount() {

        return selectorCount;
    }

    public void host(String host) {

        this.host = host;
    }

    public void port(int port) {

        this.port = port;
    }

    public void backlog(int backlog) {

        this.backlog = backlog;
    }

    public void selectorCount(int selectorCount) {

        this.selectorCount = selectorCount;
    }

    public void address(InetSocketAddress inetSocketAddress) {

        this.host = inetSocketAddress.getAddress().getHostName();
        this.port = inetSocketAddress.getPort();
    }

    public InetSocketAddress address() {

        return new InetSocketAddress(host, port);
    }
}
