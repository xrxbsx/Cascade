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
import de.jackwhite20.cascade.shared.callback.PacketCallback;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.RequestPacket;
import de.jackwhite20.cascade.shared.protocol.packet.ResponsePacket;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.session.impl.Disconnectable;
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
public class ClientImpl implements Client, Disconnectable {

    private AtomicInteger idCounter = new AtomicInteger(0);

    private boolean running = false;

    private boolean connected = false;

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
    public boolean connect() {

        if(running) {
            throw new IllegalStateException("client is already connected");
        }

        try {
            selector = Selector.open();

            // Setup the socket channel and connect
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(clientConfig.host(), clientConfig.port()));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            for (Config.Option option : clientConfig.options()) {
                //noinspection unchecked
                socketChannel.setOption(option.socketOption(), option.value());
            }

            running = true;

            new Thread(new ClientThread()).start();

            connectLatch.await();

            return connected;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void disconnect() {

        if(running) {
            running = false;

            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connectLatch.getCount() != 0) {
                connectLatch.countDown();
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

    /**
     * Sends a packet and executes the packet callback when the response packet gets received.
     * The response packet must extend ResponsePacket.
     *
     * @param packet the packet.
     * @param protocolType the the protocol type.
     * @param packetCallback the packet callback.
     */
    @Override
    public <T extends ResponsePacket> void send(RequestPacket packet, ProtocolType protocolType, PacketCallback<T> packetCallback) {

        session.send(packet, protocolType, packetCallback);
    }

    /**
     * Sends a packet over TCP (ProtocolType.TCP) and executes the packet callback when the response packet gets received.
     * The response packet must extend ResponsePacket.
     *
     * @param packet the packet.
     * @param packetCallback the packet callback.
     */
    @Override
    public <T extends ResponsePacket> void send(RequestPacket packet, PacketCallback<T> packetCallback) {

        session.send(packet, packetCallback);
    }

    @Override
    public void sessionListener(SessionListener sessionListener) {

        this.sessionListener = sessionListener;
    }

    private class ClientThread implements Runnable {

        @Override
        public void run() {

            while (running) {
                try {
                    if (selector.select() == 0) {
                        continue;
                    }

                    Set<SelectionKey> keys = selector.selectedKeys();

                    Iterator<SelectionKey> keyIterator = keys.iterator();

                    while(keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();

                        keyIterator.remove();

                        if(!key.isValid()) {
                            continue;
                        }

                        if(key.isConnectable()) {
                            socketChannel.finishConnect();

                            // Create the session object
                            session = new SessionImpl(idCounter.getAndIncrement(), socketChannel, protocol, sessionListener, ClientImpl.this);

                            // Register the read operation and attach the session object
                            socketChannel.register(selector, SelectionKey.OP_READ, session);

                            if(sessionListener != null) {
                                sessionListener.onConnected(session);
                            }

                            connected = true;

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

            // Some serious error, so disconnect if possible
            disconnect();
        }
    }
}
