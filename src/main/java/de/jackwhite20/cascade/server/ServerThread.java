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

import de.jackwhite20.cascade.server.listener.ServerListener;
import de.jackwhite20.cascade.server.selector.SelectorThread;
import de.jackwhite20.cascade.server.selector.SelectorThreadFactory;
import de.jackwhite20.cascade.server.session.ServerSession;
import de.jackwhite20.cascade.server.settings.ServerSettings;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JackWhite20 on 26.07.2015.
 */
public class ServerThread extends Thread {

    private boolean running;

    private ServerSettings settings;

    private InetSocketAddress inetSocketAddress;

    private ExecutorService pool = Executors.newFixedThreadPool(1);

    private ExecutorService selectorPool;

    private ServerSocketChannel serverSocketChannel;

    private DatagramChannel serverDatagramChannel;

    private ServerListener listener;

    private Selector selector;

    private AtomicInteger idCounter = new AtomicInteger(0);

    private List<SelectorThread> selectorThreads = new ArrayList<>();

    private AtomicInteger selectorCounter = new AtomicInteger(0);

    private ReentrantLock selectorLock = new ReentrantLock();

    public ServerThread(ServerSettings settings) {

        this.settings = settings;
        this.listener = settings.listener();
    }

    public Future<ServerThread> bind(InetSocketAddress inetSocketAddress) {

        return pool.submit(() -> {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);

            serverDatagramChannel = DatagramChannel.open();
            serverDatagramChannel.configureBlocking(false);

            selector = Selector.open();

            serverSocketChannel.bind(inetSocketAddress, settings.backLog());
            serverDatagramChannel.bind(inetSocketAddress);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            setName(settings.name());
            start();

            selectorPool = Executors.newFixedThreadPool(settings.selectorCount(), new SelectorThreadFactory());

            for (int i = 1; i <= settings.selectorCount(); i++) {
                SelectorThread selectorThread = new SelectorThread(i, selectorLock);
                selectorThreads.add(selectorThread);

                selectorPool.execute(selectorThread);
            }
/*
                new Thread(() -> {

                    while (true) {
                        for (SelectorThread selectorThread : selectorThreads) {
                            System.out.println(selectorThread.id() + ": " + selectorThread.selector().keys().size() + "keys");

                            if(selectorThread.selector().keys().size() > 0)
                                System.out.println(selectorThread.selector().keys().iterator().next().isValid());
                        }
                        //System.out.println("Keys: " + selector().keys().size());

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();*/

            return ServerThread.this;
        });
    }

    public Future<ServerThread> bind(String host, int port) {

        return bind(new InetSocketAddress(host, port));
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

        for (SelectorThread selectorThread : selectorThreads) {
            selectorThread.shutdown();
        }

        // Exit the pool
        pool.shutdown();

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

    @Override
    public void run() {

        running = true;

        while (running) {
            try {
                selector.select();

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
                        socketChannel.socket().setTcpNoDelay(true);
                        socketChannel.socket().setKeepAlive(true);
                        socketChannel.socket().setSoTimeout(0);

                        SelectorThread selectorThread = nextSelector();
                        Selector nextSelector = selectorThread.selector();

                        selectorLock.lock();

                        nextSelector.wakeup();

                        SelectionKey tcpKey = null;
                        try {
                            tcpKey = socketChannel.register(nextSelector, SelectionKey.OP_READ);
                        } catch (Exception e) {
                            socketChannel.close();
                            continue;
                        }
                        SelectionKey udpKey = null;
                        try {
                            udpKey = serverDatagramChannel.register(nextSelector, SelectionKey.OP_READ);
                        } catch (Exception e) {
                            socketChannel.close();
                            continue;
                        }

                        selectorLock.unlock();

                        int clientId = nextId();

                        ServerSession session = new ServerSession(clientId, settings.tcpBufferSize(), settings.udpBufferSize(), socketChannel, ((DatagramChannel) udpKey.channel()), listener, tcpKey, udpKey);
                        tcpKey.attach(session);
                        udpKey.attach(session);

                        if(listener != null)
                            listener.onClientConnected(clientId, session);
                    }
                }

                keys.clear();
            } catch (Exception e) {
                break;
            }
        }
    }

    public InetSocketAddress adress() {

        return inetSocketAddress;
    }

    public ServerSettings settings() {

        return settings;
    }

    public ServerSocketChannel socketChannel() {

        return serverSocketChannel;
    }

    public DatagramChannel datagramChannel() {

        return serverDatagramChannel;
    }

    public Selector selector() {

        return selector;
    }

}
