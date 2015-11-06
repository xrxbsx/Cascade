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

package de.jackwhite20.cascade.client;

import de.jackwhite20.cascade.shared.CascadeSettings;
import de.jackwhite20.cascade.shared.Disconnectable;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JackWhite20 on 15.10.2015.
 */
public class Client implements Disconnectable {

    public static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private CascadeSettings settings;

    private Selector selector;

    private SocketChannel socketChannel;

    private DatagramChannel datagramChannel;

    private Session session;

    private SessionListener listener;

    private String host;

    private int port;

    private boolean running = false;

    public Client(CascadeSettings settings) {

        this.listener = settings.listener();
        this.settings = settings;
    }

    @SuppressWarnings("unchecked")
    public void connect(String host, int port) {

        this.host = host;
        this.port = port;

        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            socketChannel.socket().setSoTimeout(0);
            for (CascadeSettings.Option option : settings.options()) {
                socketChannel.setOption(option.socketOption(), option.value());
            }

            socketChannel.connect(new InetSocketAddress(host, port));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            running = true;

            new Thread(new ClientThread()).start();
        } catch (IOException e) {
            if (listener != null)
                listener.onException(session, e);
        }
    }

    public void connect(InetSocketAddress address) {

        connect(address.getHostName(), address.getPort());
    }

    public void sendReliable(byte[] buffer) {

        session.sendReliable(buffer);
    }

    public void sendUnreliable(byte[] buffer) {

        session.sendUnreliable(buffer);
    }

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

        if(datagramChannel != null) {
            try {
                datagramChannel.disconnect();
                datagramChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(listener != null)
            listener.onDisconnected(session);
    }

    public CascadeSettings settings() {

        return settings;
    }

    public Session session() {

        return session;
    }

    public SessionListener listener() {

        return listener;
    }

    public String host() {

        return host;
    }

    public int port() {

        return port;
    }

    public boolean running() {

        return running;
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

                            datagramChannel = DatagramChannel.open();
                            // Start UDP with our local Port, so the server does now our UDP Port
                            datagramChannel.bind(socketChannel.socket().getLocalSocketAddress());
                            datagramChannel.configureBlocking(false);
                            datagramChannel.connect(new InetSocketAddress(host, port));

                            SelectionKey tcpRead = socketChannel.register(selector, SelectionKey.OP_READ);
                            SelectionKey udpRead = datagramChannel.register(selector, SelectionKey.OP_READ);

                            session = new Session(ID_COUNTER.getAndIncrement(), socketChannel, datagramChannel, listener, Client.this);

                            tcpRead.attach(session);
                            udpRead.attach(session);

                            if(listener != null)
                                listener.onConnected(session);
                        }

                        if(key.isValid() && key.isReadable()) {
                            Session session = (Session) key.attachment();

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
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
