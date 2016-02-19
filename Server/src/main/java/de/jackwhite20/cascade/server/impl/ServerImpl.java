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

package de.jackwhite20.cascade.server.impl;

import de.jackwhite20.cascade.server.Server;
import de.jackwhite20.cascade.server.ServerConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public class ServerImpl implements Server {

    private boolean running;

    private ServerConfig serverConfig;

    private InetSocketAddress bindAddress;

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    public ServerImpl(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
        this.bindAddress = serverConfig.address();
    }

    @Override
    public void start() {

        try {
            // Open and prepare the server socket
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(bindAddress, serverConfig.backlog());

            // Open and register the selector to accept connections
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            running = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

        running = false;

        if(selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(serverSocketChannel != null) {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean running() {

        return running;
    }
}
