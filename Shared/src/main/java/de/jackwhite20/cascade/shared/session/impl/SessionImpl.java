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

package de.jackwhite20.cascade.shared.session.impl;

import de.jackwhite20.cascade.shared.callback.PacketCallback;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.io.PacketReader;
import de.jackwhite20.cascade.shared.protocol.io.PacketWriter;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.RequestPacket;
import de.jackwhite20.cascade.shared.protocol.packet.ResponsePacket;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JackWhite20 on 24.02.2016.
 */
public class SessionImpl implements Session {

    private static final int READ_BUF_SIZE = 2048;

    private static final int BUFFER_GROW_FACTOR = 2;

    private int id;

    private SocketChannel socketChannel;

    private ByteBuffer tcpBuffer = ByteBuffer.allocate(READ_BUF_SIZE);

    private boolean disconnected = false;

    private Protocol protocol;

    private SocketAddress remoteAddress;

    private ConcurrentHashMap<Integer, PacketCallback> callbackPackets = new ConcurrentHashMap<>();

    private SessionListener sessionListener;

    private Disconnectable disconnectable;

    public SessionImpl(int id, SocketChannel socketChannel, Protocol protocol, SessionListener sessionListener, Disconnectable disconnectable) throws IOException {

        this.id = id;
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        this.remoteAddress = this.socketChannel.getRemoteAddress();
        this.protocol = protocol;
        this.sessionListener = sessionListener;
        this.disconnectable = disconnectable;
    }

    public SessionImpl(int id, SocketChannel socketChannel, Protocol protocol, SessionListener sessionListener) throws IOException {

        this(id, socketChannel, protocol, sessionListener, null);
    }

    @SuppressWarnings("all")
    public void readSocket() {

        int read;

        tcpBuffer.limit(tcpBuffer.capacity());

        try {
            read = socketChannel.read(tcpBuffer);
            if (tcpBuffer.remaining() == 0) {
                ByteBuffer temp = ByteBuffer.allocate(tcpBuffer.capacity() * BUFFER_GROW_FACTOR);
                tcpBuffer.flip();
                temp.put(tcpBuffer);
                tcpBuffer = temp;

                int position = tcpBuffer.position();
                tcpBuffer.flip();
                // Reset to last position (the position from the half read packet) after flip
                tcpBuffer.position(position);

                // Read again to read the left packet
                readSocket();
            }
        } catch (IOException e) {
            read = -1;
        }

        if (read == -1) {
            close();

            return;
        }

        tcpBuffer.flip();

        while (tcpBuffer.remaining() > 0) {
            tcpBuffer.mark();

            if (tcpBuffer.remaining() < 4)
                break;

            int readableBytes = tcpBuffer.getInt();
            if (tcpBuffer.remaining() < readableBytes) {
                tcpBuffer.reset();
                break;
            }

            byte[] bytes = new byte[readableBytes];
            tcpBuffer.get(bytes);

            try {
                PacketReader packetReader = new PacketReader(bytes);
                byte packetId = packetReader.readByte();

                Packet packet = protocol.create(packetId);
                packet.read(packetReader);

                if (!(packet instanceof ResponsePacket)) {
                    protocol.call(packet.getClass(), this, packet, ProtocolType.TCP);
                }else {
                    ResponsePacket callbackPacket = ((ResponsePacket) packet);
                    if(callbackPackets.containsKey(callbackPacket.callbackId()))
                        callbackPackets.get(callbackPacket.callbackId()).receive(callbackPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
                close();
            }
        }

        tcpBuffer.compact();
    }

    @Override
    public void close() {

        if(disconnected)
            return;

        if(disconnectable == null) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            disconnectable.disconnect();
        }

        disconnected = true;

        if(sessionListener != null) {
            sessionListener.onDisconnected(this);
        }
    }

    @Override
    public void send(Packet packet, ProtocolType protocolType) {

        if(!socketChannel.isConnected()) {
            close();
            return;
        }

        PacketWriter packetWriter = new PacketWriter();

        try {
            packetWriter.writeByte(protocol.findId(packet.getClass()));
            packet.write(packetWriter);

            byte[] buffer = packetWriter.bytes();

            ByteBuffer sendBuffer = ByteBuffer.allocate(buffer.length + 4);
            sendBuffer.putInt(buffer.length);
            sendBuffer.put(buffer);
            sendBuffer.flip();

            if(protocolType == ProtocolType.UDP) {
                // TODO: 08.03.2016
            } else {
                socketChannel.write(sendBuffer);
            }
        } catch (Exception e) {
            close();

            e.printStackTrace();
        }
    }

    @Override
    public void send(Packet packet) {

        send(packet, ProtocolType.TCP);
    }

    @Override
    public <T extends ResponsePacket> void send(RequestPacket packet, PacketCallback<T> packetCallback) {

        callbackPackets.put(packet.callbackId(), packetCallback);

        send(packet, ProtocolType.TCP);
    }

    @Override
    public int id() {

        return id;
    }

    @Override
    public SocketChannel socketChannel() {

        return socketChannel;
    }

    @Override
    public Protocol protocol() {

        return protocol;
    }

    @Override
    public SocketAddress remoteAddress() {

        return remoteAddress;
    }

    @Override
    public boolean connected() {

        return !disconnected && socketChannel.isConnected();
    }
}
