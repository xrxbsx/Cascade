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

import de.jackwhite20.cascade.server.settings.ServerSettings;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by JackWhite20 on 26.07.2015.
 */
public class Server extends Thread {

    private boolean running;

    private ServerSettings settings;

    private InetSocketAddress inetSocketAddress;

    private ExecutorService pool = Executors.newFixedThreadPool(1);

    private ExecutorService selectorPool;

    private ServerSocketChannel serverSocketChannel;

    private DatagramChannel serverDatagramChannel;

    private Selector selector;

    public Server(ServerSettings settings) {
        this.settings = settings;
    }

    public Future<Server> bind(InetSocketAddress inetSocketAddress) {
        return pool.submit(new Callable<Server>() {

            @Override
            public Server call() throws Exception {
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
                    SelectorThread selectorThread = new SelectorThread(i);

                    selectorPool.execute(selectorThread);
                }

                return Server.this;
            }

        });
    }

    public Future<Server> bind(String host, int port) {
        return bind(new InetSocketAddress(host, port));
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                if(selector.select() == 0)
                    continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

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

                        System.out.println("Client connected!");
                    }
                }

                keys.clear();

            } catch (Exception e) {
                e.printStackTrace();
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
