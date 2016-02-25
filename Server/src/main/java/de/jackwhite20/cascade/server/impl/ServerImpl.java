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
import de.jackwhite20.cascade.shared.server.Reactor;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.session.impl.SessionImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public class ServerImpl implements Server, Reactor, Runnable {

    private boolean running;

    private ServerConfig serverConfig;

    private SessionListener sessionListener;

    private Selector selector;

    private AtomicInteger idCounter = new AtomicInteger(0);

    private ServerSocketChannel serverSocketChannel;

    private ExecutorService workerPool;

    public ServerImpl(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
    }

    private int nextId() {

        return idCounter.getAndIncrement();
    }

    @Override
    public void start() {

        try {
            workerPool = Executors.newFixedThreadPool(serverConfig.workerThreads() + 1);

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(serverConfig.host(), serverConfig.port()), serverConfig.backlog());
            serverSocketChannel.configureBlocking(false);

            running = true;

            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            selectionKey.attach(new Acceptor());

            workerPool.execute(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            while (running) {
                int count = selector.select();
                if (count == 0)
                    continue;

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    SelectionKey sk = it.next();
                    it.remove();
                    Runnable r = (Runnable) sk.attachment();
                    if (r != null) {
                        r.run();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void sessionListener(SessionListener sessionListener) {

        this.sessionListener = sessionListener;
    }

    @Override
    public void stop() {

        running = false;

        try {
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        workerPool.shutdown();
    }

    @Override
    public boolean running() {

        return running;
    }

    @Override
    public SessionListener sessionListener() {

        return sessionListener;
    }

    @Override
    public ExecutorService workerThreadPool() {

        return workerPool;
    }

    private class Acceptor implements Runnable {

        public void run() {

            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);

                    Session session = new SessionImpl(nextId(), ServerImpl.this, selector, socketChannel, serverConfig.protocol());

                    if(sessionListener != null) {
                        sessionListener.onConnected(session);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
