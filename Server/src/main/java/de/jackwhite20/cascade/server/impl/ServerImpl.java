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
import de.jackwhite20.cascade.server.selector.SelectorThread;
import de.jackwhite20.cascade.shared.Config;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.session.impl.SessionImpl;

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
public class ServerImpl implements Server, Runnable {

    private boolean running;

    private ServerConfig serverConfig;

    private SessionListener sessionListener;

    private Selector selector;

    private AtomicInteger idCounter = new AtomicInteger(0);

    private ServerSocketChannel serverSocketChannel;

    private ExecutorService workerPool;

    private ReentrantLock selectorLock = new ReentrantLock();

    private List<SelectorThread> selectorThreads = new ArrayList<>();

    private AtomicInteger selectorCounter = new AtomicInteger(0);

    public ServerImpl(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
        this.sessionListener = serverConfig.sessionListener();
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

    @Override
    public void start() {

        try {
            workerPool = Executors.newFixedThreadPool(serverConfig.workerThreads() + 1);

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(serverConfig.host(), serverConfig.port()), serverConfig.backlog());
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            workerPool.execute(this);

            for (int i = 1; i <= serverConfig.workerThreads(); i++) {
                SelectorThread selectorThread = new SelectorThread(i, selectorLock);
                selectorThreads.add(selectorThread);

                workerPool.execute(selectorThread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        running = true;

        while (running) {
            try {
                if (selector.select() == 0) {
                    continue;
                }

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

                        socketChannel.configureBlocking(false);

                        for (Config.Option option : serverConfig.options()) {
                            //noinspection unchecked
                            socketChannel.setOption(option.socketOption(), option.value());
                        }

                        SelectorThread selectorThread = nextSelector();
                        Selector nextSelector = selectorThread.selector();

                        selectorLock.lock();

                        // Important
                        nextSelector.wakeup();

                        try {
                            SelectionKey tcpKey = socketChannel.register(nextSelector, SelectionKey.OP_READ);

                            int clientId = nextId();

                            SessionImpl session = new SessionImpl(clientId, socketChannel, serverConfig.protocol(), sessionListener);
                            tcpKey.attach(session);

                            if(sessionListener != null) {
                                sessionListener.onConnected(session);
                            }
                        } catch (Exception e) {
                            socketChannel.close();
                            e.printStackTrace();
                        } finally {
                            selectorLock.unlock();
                        }
                    }
                }

                keys.clear();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
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
}
