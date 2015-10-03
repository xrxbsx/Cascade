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

import de.jackwhite20.cascade.client.listener.ClientListener;
import de.jackwhite20.cascade.client.settings.ClientSettings;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by JackWhite20 on 03.10.2015.
 */
public class Client extends Thread {

    private ClientSettings settings;

    private Selector selector;

    private SocketChannel socketChannel;

    private DatagramChannel datagramChannel;

    private ClientSession session;

    private ClientListener listener;

    private String host;

    private int port;

    private int tcpBufferSize;

    private int udpBufferSize;

    private boolean running = false;

    public Client(ClientSettings settings) {

        this.host = settings.host();
        this.port = settings.port();
        this.tcpBufferSize = settings.tcpBufferSize();
        this.udpBufferSize = settings.udpBufferSize();
        this.listener = settings.listener();
        this.settings = settings;
    }

    public void connect() {

        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            socketChannel.socket().setKeepAlive(true);
            socketChannel.socket().setTcpNoDelay(true);
            socketChannel.socket().setSoTimeout(0);

            socketChannel.connect(new InetSocketAddress(host, port));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            running = true;

            setName("Receive-Thread");
            start();
        } catch (IOException e) {
            // Error handling
        }
    }

    public void sendTCP(byte[] buffer) {

        session.sendTCP(buffer);
    }

    public void sendUDP(byte[] buffer) {

        session.sendUDP(buffer);
    }

    public void disconnect() throws IOException {

        running = false;

        if(selector != null)
            selector.close();

        if(socketChannel != null)
            socketChannel.close();

        if(datagramChannel != null)
            datagramChannel.close();

        if(listener != null)
            listener.onDisconnected();
    }

    @Override
    public void run() {

        while (running) {
            try {
                selector.select();

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
                        datagramChannel.bind(socketChannel.getLocalAddress());
                        datagramChannel.configureBlocking(false);
                        datagramChannel.connect(new InetSocketAddress(host, port));

                        SelectionKey tcpRead = socketChannel.register(selector, SelectionKey.OP_READ);
                        SelectionKey udpRead = datagramChannel.register(selector, SelectionKey.OP_READ);

                        session = new ClientSession(this, socketChannel, datagramChannel, ByteBuffer.allocate(tcpBufferSize), ByteBuffer.allocate(udpBufferSize), listener);

                        tcpRead.attach(session);
                        udpRead.attach(session);

                        if(listener != null)
                            listener.onConnected();
                    }

                    if(key.isValid() && key.isReadable()) {
                        ClientSession session = (ClientSession) key.attachment();

                        if(session == null)
                            continue;

                        if(selectableChannel instanceof DatagramChannel) {
                            session.readDatagram();
                        }else {
                            session.readSocket();
                        }
                    }

                }

                keys.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
