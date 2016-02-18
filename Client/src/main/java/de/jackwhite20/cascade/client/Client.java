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
import de.jackwhite20.cascade.shared.callback.PacketCallback;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.RequestPacket;
import de.jackwhite20.cascade.shared.protocol.packet.ResponsePacket;
import de.jackwhite20.cascade.shared.protocol.packet.internal.UDPPortPacket;
import de.jackwhite20.cascade.shared.session.ProtocolType;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.session.impl.SessionImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JackWhite20 on 15.10.2015.
 */
public class Client implements Disconnectable {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private CascadeSettings settings;

    private Selector selector;

    private SocketChannel socketChannel;

    private DatagramChannel datagramChannel;

    private Session session;

    private String host;

    private int port;

    private boolean running = false;

    private Object waitObject = new Object();

    private boolean connected = false;

    private int timeout;

    private InternalPacketListener internalPacketListener;

    private Protocol protocol;

    /**
     * Creates a new instance with the given settings.
     *
     * @param settings the settings.
     */
    public Client(CascadeSettings settings) {

        this.settings = settings;
        this.protocol = settings.protocol();
        this.protocol.registerListener(internalPacketListener = new InternalPacketListener(this));
        this.protocol.registerPacket(UDPPortPacket.class);
    }

    /**
     * Connects the client to the remote host.
     *
     * @param host the host ip.
     * @param port the host port.
     * @param timeout the timeout in milliseconds.
     *
     * @return true if it has successfully connected and is running.
     */
    @SuppressWarnings("all")
    public boolean connect(String host, int port, int timeout) {

        this.host = host;
        this.port = port;
        this.timeout = timeout;

        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            for (CascadeSettings.Option option : settings.options()) {
                socketChannel.setOption(option.socketOption(), option.value());
            }
            socketChannel.socket().setSoTimeout(timeout);

            socketChannel.connect(new InetSocketAddress(host, port));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            running = true;

            new Thread(new ClientThread()).start();

            synchronized (waitObject) {
                try {
                    waitObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return connected;
        } catch (IOException e) {
            settings.listener().forEach(sessionListener -> sessionListener.onException(session, e));
            connected = false;
            notifyWaitObject();
        }

        return false;
    }

    /**
     * Connects the client to the remote host.
     * The timeout is set to 10 minutes.
     *
     * @param host the host ip.
     * @param port the host port.
     *
     * @return true if it has succesfully connected and is running.
     */
    public boolean connect(String host, int port) {

        return connect(host, port, 600000);
    }

    /**
     * Connects the client to the remote host.
     *
     * @param address the host address.
     * @param timeout the timeout in milliseconds.
     */
    public boolean connect(InetSocketAddress address, int timeout) {

        return connect(address.getHostName(), address.getPort(), timeout);
    }

    /**
     * Connects the client to the remote host.
     * The timeout is set to 10 minutes.
     *
     * @param address the host address.
     */
    public boolean connect(InetSocketAddress address) {

        return connect(address, 600000);
    }

    /**
     * Sends the given packet with the protocol type.
     *
     * @param packet the packet.
     * @param type the protocol type.
     */
    public void send(Packet packet, ProtocolType type) {

        session.send(packet, type);
    }

    /**
     * Sends a packet over TCP (ProtocolType.TCP).
     *
     * @param packet the packet.
     */
    public void send(Packet packet) {

        session.send(packet);
    }

    /**
     * Sends a packet and executes the packet callback when the response packet gets received.
     * The response packet must extend ResponsePacket.
     *
     * @param packet the packet.
     * @param protocolType the the protocol type.
     * @param packetCallback the packet callback.
     */
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
    public <T extends ResponsePacket> void send(RequestPacket packet, PacketCallback<T> packetCallback) {

        session.send(packet, packetCallback);
    }

    /**
     * Disconnects the client.
     */
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
                datagramChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        settings.listener().forEach(sessionListener -> sessionListener.onDisconnected(session));
    }

    /**
     * Connects the datagram channel to the remote host with the received port.
     *
     * @param port the port.
     */
    protected void connectDatagramChannel(int port) {

        try {
            datagramChannel.connect(new InetSocketAddress(host, port));

            protocol.unregisterListener(internalPacketListener);
            protocol.unregisterPacket(UDPPortPacket.class);

            notifyWaitObject();
            settings.listener().forEach(sessionListener -> sessionListener.onConnected(session));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies the waitObject that is probably waiting.
     */
    @SuppressWarnings("all")
    private void notifyWaitObject() {

        synchronized (waitObject) {
            waitObject.notify();
        }
    }

    /**
     * Returns the cascade settings.
     *
     * @return the settings.
     */
    public CascadeSettings settings() {

        return settings;
    }

    /**
     * Returns the current client session.
     *
     * @return the session.
     */
    public Session session() {

        return session;
    }

    /**
     * Returns the session listener from this client.
     *
     * @return the listeners as an unmodifiable list.
     */
    public List<SessionListener> listener() {

        return Collections.unmodifiableList(settings.listener());
    }

    /**
     * Returns the hosts ip.
     *
     * @return the ip.
     */
    public String host() {

        return host;
    }

    /**
     * Returns the hosts port.
     *
     * @return the port.
     */
    public int port() {

        return port;
    }

    /**
     * Returns whether this client is connected and running.
     *
     * @return true or false.
     */
    public boolean running() {

        return running;
    }

    /**
     * Returns whether this client is successfully connected to the remote side.
     *
     * @return true or false.
     */
    public boolean connected() {

        return connected;
    }

    /**
     * Returns the timeout in milliseconds.
     *
     * @return the timeout.
     */
    public int timeout() {

        return timeout;
    }

    /**
     * Returns the current protocol instance.
     *
     * @return the protocol.
     */
    public Protocol protocol() {

        return protocol;
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

                            session = new SessionImpl(ID_COUNTER.getAndIncrement(), socketChannel, settings.listener(), Client.this, settings.compressionThreshold(), settings.protocol());
                            tcpRead.attach(session);

                            connected = true;

                            if(!settings.udp()) {
                                notifyWaitObject();

                                settings.listener().forEach(sessionListener -> sessionListener.onConnected(session));
                            }else {
                                datagramChannel = DatagramChannel.open();
                                datagramChannel.bind(new InetSocketAddress("0.0.0.0", 0));
                                datagramChannel.configureBlocking(false);

                                SelectionKey udpRead = datagramChannel.register(selector, SelectionKey.OP_READ);
                                udpRead.attach(session);
                                ((SessionImpl) session).datagramChannel(datagramChannel);

                                session.send(new UDPPortPacket(datagramChannel.socket().getLocalPort()), ProtocolType.TCP);
                            }
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
                    connected = false;
                    settings.listener().forEach(sessionListener -> sessionListener.onException(session, e));
                    notifyWaitObject();
                    break;
                }
            }
        }
    }
}
