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

import de.jackwhite20.cascade.server.selector.SelectorThread;
import de.jackwhite20.cascade.server.selector.SelectorThreadFactory;
import de.jackwhite20.cascade.shared.CascadeSettings;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JackWhite20 on 15.10.2015.
 */
public class Server {

    private boolean running;

    private CascadeSettings settings;

    private InetSocketAddress inetSocketAddress;

    private ExecutorService selectorPool;

    private ServerSocketChannel serverSocketChannel;

    private DatagramChannel serverDatagramChannel;

    private SessionListener listener;

    private Selector selector;

    private AtomicInteger idCounter = new AtomicInteger(0);

    private List<SelectorThread> selectorThreads = new ArrayList<>();

    private AtomicInteger selectorCounter = new AtomicInteger(0);

    private ReentrantLock selectorLock = new ReentrantLock();

    public Server(CascadeSettings settings) {

        this.settings = settings;
        this.listener = settings.listener();
    }

    @SuppressWarnings("unchecked")
    public void bind(InetSocketAddress inetSocketAddress) throws Exception {

        this.inetSocketAddress = inetSocketAddress;

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().setSoTimeout(0);

        serverDatagramChannel = DatagramChannel.open();
        serverDatagramChannel.configureBlocking(false);

        selector = Selector.open();

        serverSocketChannel.bind(inetSocketAddress, settings.backLog());
        serverDatagramChannel.bind(inetSocketAddress);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        selectorPool = Executors.newFixedThreadPool(settings.selectorCount() + 1, new SelectorThreadFactory());

        selectorPool.execute(new ServerThread());

        for (int i = 1; i <= settings.selectorCount(); i++) {
            SelectorThread selectorThread = new SelectorThread(i, selectorLock);
            selectorThreads.add(selectorThread);

            selectorPool.execute(selectorThread);
        }
    }

    public void bind(String host, int port) throws Exception {

        bind(new InetSocketAddress(host, port));
    }

    public void shutdown() {

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

        if(serverDatagramChannel != null) {
            try {
                serverDatagramChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        selectorThreads.forEach(SelectorThread::shutdown);

        // Shutdown the pool for the selectors
        selectorPool.shutdown();
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


    private class ServerThread implements Runnable {

        @Override
        @SuppressWarnings("unchecked")
        public void run() {

            running = true;

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

                            socketChannel.configureBlocking(false);
                            socketChannel.socket().setSoTimeout(0);
                            for (CascadeSettings.Option option : settings.options()) {
                                socketChannel.setOption(option.socketOption(), option.value());
                            }

                            SelectorThread selectorThread = nextSelector();
                            Selector nextSelector = selectorThread.selector();

                            selectorLock.lock();

                            // Important
                            nextSelector.wakeup();

                            SelectionKey tcpKey = null;
                            try {
                                tcpKey = socketChannel.register(nextSelector, SelectionKey.OP_READ);
                            } catch (Exception e) {
                                socketChannel.close();
                                e.printStackTrace();
                                continue;
                            }
                            SelectionKey udpKey = null;
                            try {
                                udpKey = serverDatagramChannel.register(nextSelector, SelectionKey.OP_READ);
                            } catch (Exception e) {
                                socketChannel.close();
                                e.printStackTrace();
                                continue;
                            }

                            selectorLock.unlock();

                            int clientId = nextId();

                            Session session = new Session(clientId, socketChannel, ((DatagramChannel) udpKey.channel()), listener);
                            tcpKey.attach(session);
                            udpKey.attach(session);

                            if(listener != null)
                                listener.onConnected(session);
                        }
                    }

                    keys.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}