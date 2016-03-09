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
import de.jackwhite20.cascade.shared.Config;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.session.impl.ProtocolType;
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

    private AtomicInteger idCounter = new AtomicInteger(0);

    private boolean running = false;

    private ClientConfig clientConfig;

    private Selector selector;

    private SocketChannel socketChannel;

    private Session session;

    private Protocol protocol;

    private SessionListener sessionListener;

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

            for (Config.Option option : clientConfig.options()) {
                //noinspection unchecked
                socketChannel.setOption(option.socketOption(), option.value());
            }

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
    public void send(Packet packet, ProtocolType protocolType) {

        session.send(packet, protocolType);
    }

    @Override
    public void send(Packet packet) {

        send(packet, ProtocolType.TCP);
    }

    @Override
    public void sessionListener(SessionListener sessionListener) {

        this.sessionListener = sessionListener;
    }

    private class ClientThread implements Runnable {

        @Override
        public void run() {

            running = true;

            while (running) {
                try {
                    if (selector.select() == 0)
                        continue;

                    Set<SelectionKey> keys = selector.selectedKeys();

                    Iterator<SelectionKey> keyIterator = keys.iterator();

                    while(keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();

                        keyIterator.remove();

                        if(!key.isValid())
                            continue;

                        if(key.isConnectable()) {
                            socketChannel.finishConnect();

                            session = new SessionImpl(idCounter.getAndIncrement(), socketChannel, protocol, sessionListener);

                            socketChannel.register(selector, SelectionKey.OP_READ).attach(session);

                            if(sessionListener != null)
                                sessionListener.onConnected(session);

                            connectLatch.countDown();
                        }

                        if(key.isValid() && key.isReadable()) {
                            SessionImpl session = (SessionImpl) key.attachment();

                            if(session == null) {
                                continue;
                            }

                            SelectableChannel selectableChannel = key.channel();
                            
                            if(selectableChannel instanceof DatagramChannel) {
                                // TODO: 08.03.2016
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
