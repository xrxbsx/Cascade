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

package de.jackwhite20.cascade.client.impl;

import de.jackwhite20.cascade.client.Client;
import de.jackwhite20.cascade.client.ClientConfig;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.impl.SessionImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public class ClientImpl implements Client {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private boolean running = false;

    private ClientConfig clientConfig;

    private Selector selector;

    private SocketChannel socketChannel;

    private Session session;

    private Protocol protocol;

    private CountDownLatch connectLatch = new CountDownLatch(1);

    public ClientImpl(ClientConfig clientConfig) {

        this.clientConfig = clientConfig;
        this.protocol = clientConfig.protocol();
    }

    @Override
    public void connect() {

        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            socketChannel.connect(new InetSocketAddress(clientConfig.host(), clientConfig.port()));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            running = true;

            new Thread(new ClientThread()).start();

            connectLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {

        running = false;

        if(selector != null) {
            selector.wakeup();
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(socketChannel != null) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean running() {

        return running;
    }

    @Override
    public void send(Packet packet) {

        session.send(packet);
    }

    private class ClientThread implements Runnable {

        @Override
        public void run() {

            while (running) {
                try {
                    if (selector.select() == 0)
                        continue;

                    Set<SelectionKey> keys = selector.selectedKeys();

                    Iterator<SelectionKey> keyIterator = keys.iterator();

                    while(keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();

                        keyIterator.remove();

                        SelectableChannel selectableChannel = key.channel();

                        if(!key.isValid())
                            continue;

                        if(key.isConnectable()) {
                            socketChannel.finishConnect();

                            SelectionKey tcpRead = socketChannel.register(selector, SelectionKey.OP_READ);

                            session = new SessionImpl(ID_COUNTER.getAndIncrement(), socketChannel, protocol);
                            tcpRead.attach(session);

                            connectLatch.countDown();
                        }

                        if(key.isValid() && key.isReadable()) {
                            SessionImpl session = (SessionImpl) key.attachment();

                            if(session == null)
                                continue;

                            if(selectableChannel instanceof DatagramChannel) {
                                session.readDatagram();
                            }else {
                                session.readSocket();
                            }
                        }
                    }
                } catch (Exception e) {
                    break;
                }
            }
        }
    }
}
