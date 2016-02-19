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
import de.jackwhite20.cascade.server.impl.selector.SelectorThread;
import de.jackwhite20.cascade.server.impl.selector.SelectorThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public class ServerImpl implements Server {

    private boolean running;

    private ServerConfig serverConfig;

    private InetSocketAddress bindAddress;

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    private ExecutorService selectorPool;

    private AtomicInteger idCounter = new AtomicInteger(0);

    private List<SelectorThread> selectorThreads = new ArrayList<>();

    private AtomicInteger selectorCounter = new AtomicInteger(0);

    private ReentrantLock selectorLock = new ReentrantLock();

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

            selectorPool = Executors.newFixedThreadPool(serverConfig.selectorCount() + 1, new SelectorThreadFactory());

            running = true;

            selectorPool.execute(new AcceptThread());

            for (int i = 1; i <= serverConfig.selectorCount(); i++) {
                SelectorThread selectorThread = new SelectorThread(i, selectorLock);
                selectorThreads.add(selectorThread);

                selectorPool.execute(selectorThread);
            }
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

        // Try to shutdown the multiplexing selector threads
        selectorThreads.forEach(SelectorThread::shutdown);

        // Shutdown the pool for the selectors
        selectorPool.shutdown();
    }

    @Override
    public boolean running() {

        return running;
    }

    private int nextId() {

        return idCounter.getAndIncrement();
    }

    private SelectorThread nextSelector() {

        int next = selectorCounter.getAndIncrement();

        if(next >= selectorThreads.size()) {
            selectorCounter.set(0);
            next = 0;
        }

        return selectorThreads.get(next);
    }

    private class AcceptThread implements Runnable {

        @Override
        public void run() {
            while (running) {
                try {
                    if (selector.select() == 0)
                        continue;

                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = keys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();

                        keyIterator.remove();

                        if(!key.isValid())
                            continue;

                        if(key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();

                            if(socketChannel == null)
                                continue;

                            // We don't want a blocking channel
                            socketChannel.configureBlocking(false);

                            SelectorThread selectorThread = nextSelector();
                            Selector nextSelector = selectorThread.selector();

                            selectorLock.lock();

                            // Wake the selector up so it returns from the select method
                            nextSelector.wakeup();

                            SelectionKey tcpKey;
                            try {
                                tcpKey = socketChannel.register(nextSelector, SelectionKey.OP_READ);
                            } catch (Exception e) {
                                socketChannel.close();
                                e.printStackTrace();
                                continue;
                            }

                            selectorLock.unlock();

                            int clientId = nextId();

                            // TODO: 19.02.2016
                        }
                    }

                    keys.clear();
                } catch (Exception ignored) {
                    // TODO: Decide if we should break or continue
                }
            }
        }
    }
}
