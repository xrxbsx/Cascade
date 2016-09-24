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

package de.jackwhite20.cascade.shared;

import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.session.SessionListener;

import java.net.SocketOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackWhite20 on 09.03.2016.
 */
public abstract class Config {

    private String host;

    private int port;

    private Protocol protocol;

    private List<SessionListener> sessionListener = new ArrayList<>();

    private List<Option> options = new ArrayList<>();

    private int workerThreads = 2;

    public String host() {

        return host;
    }

    public void host(String host) {

        this.host = host;
    }

    public int port() {

        return port;
    }

    public void port(int port) {

        this.port = port;
    }

    public Protocol protocol() {

        return protocol;
    }

    public void protocol(Protocol protocol) {

        this.protocol = protocol;
    }

    public List<SessionListener> sessionListener() {

        return sessionListener;
    }

    public void sessionListener(SessionListener sessionListener) {

        this.sessionListener.add(sessionListener);
    }

    public <T> void option(SocketOption<T> socketOption, T value) {

        options.add(new Option<>(socketOption, value));
    }

    public List<Option> options() {

        return options;
    }

    public int workerThreads() {

        return workerThreads;
    }

    public void workerThreads(int workerThreads) {

        this.workerThreads = workerThreads;
    }

    public static class Option<T> {

        private SocketOption<T> socketOption;

        private T value;

        public Option(SocketOption<T> socketOption, T value) {

            this.socketOption = socketOption;
            this.value = value;
        }

        public SocketOption<T> socketOption() {

            return socketOption;
        }

        public T value() {

            return value;
        }
    }
}
