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

import de.jackwhite20.cascade.shared.Disconnectable;
import de.jackwhite20.cascade.shared.callback.PacketCallback;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.io.PacketReader;
import de.jackwhite20.cascade.shared.protocol.io.PacketWriter;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.RequestPacket;
import de.jackwhite20.cascade.shared.protocol.packet.ResponsePacket;
import de.jackwhite20.cascade.shared.session.ProtocolType;
import de.jackwhite20.cascade.shared.session.Session;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JackWhite20 on 13.10.2015.
 */
public class SessionImpl implements Session {

    private static final int DEFAULT_TCP_BUFFER_SIZE = 1024;

    private static final int DEFAULT_UDP_BUFFER_SIZE = 65535;

    private static final int BUFFER_GROW_FACTOR = 2;

    private int id;

    private SocketChannel socketChannel;

    private DatagramChannel datagramChannel;
    
    private SocketAddress remoteAddress;

    private ByteBuffer tcpBuffer;

    private ByteBuffer udpBuffer;

    private Disconnectable disconnectable;

    private boolean disconnected = false;

    private Protocol protocol;

    private ConcurrentHashMap<Integer, PacketCallback> callbackPackets = new ConcurrentHashMap<>();

    public SessionImpl(int id, SocketChannel socketChannel, Disconnectable disconnectable, Protocol protocol) {

        this.id = id;
        this.socketChannel = socketChannel;
        try {
            this.remoteAddress = socketChannel.getRemoteAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tcpBuffer = ByteBuffer.allocate(DEFAULT_TCP_BUFFER_SIZE);
        this.udpBuffer = ByteBuffer.allocate(DEFAULT_UDP_BUFFER_SIZE);
        this.disconnectable = disconnectable;
        this.protocol = protocol;
    }

    public SessionImpl(int id, SocketChannel socketChannel, Protocol protocol) {

        this(id, socketChannel, null, protocol);
    }

    public void close() {

        // Don't call onDisconnected twice
        if(disconnected)
            return;

        if (disconnectable != null) {
            disconnectable.disconnect();
        } else {
            try {
                if (socketChannel != null)
                    socketChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (datagramChannel != null)
                    datagramChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        disconnected = true;
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

            PacketReader packetReader = new PacketReader(bytes);
            try {
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
                close();
                e.printStackTrace();
            }
        }

        tcpBuffer.compact();
    }

    @SuppressWarnings("all")
    public void readDatagram() {

        try {
            while (datagramChannel.receive(udpBuffer) != null) {
                udpBuffer.flip();

                if(udpBuffer.remaining() < 4)
                    continue;

                int readableBytes = udpBuffer.getInt();

                byte[] bytes = new byte[readableBytes];
                udpBuffer.get(bytes);

                PacketReader packetReader = new PacketReader(bytes);
                try {
                    byte packetId = packetReader.readByte();

                    Packet packet = protocol.create(packetId);
                    packet.read(packetReader);

                    if (!(packet instanceof ResponsePacket)) {
                        protocol.call(packet.getClass(), this, packet, ProtocolType.UDP);
                    }else {
                        ResponsePacket callbackPacket = ((ResponsePacket) packet);
                        if(callbackPackets.containsKey(callbackPacket.callbackId()))
                            callbackPackets.get(callbackPacket.callbackId()).receive(callbackPacket);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    close();
                }

                udpBuffer.clear();
            }
        } catch (IOException e) {
            close();
        }
    }

    public void send(Packet packet, ProtocolType protocolType) {

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
                datagramChannel.send(sendBuffer, datagramChannel.getRemoteAddress());
            } else
                socketChannel.write(sendBuffer);
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
    public <T extends ResponsePacket> void send(RequestPacket packet, ProtocolType protocolType, PacketCallback<T> packetCallback) {

        callbackPackets.put(packet.callbackId(), packetCallback);

        send(packet, protocolType);
    }

    @Override
    public <T extends ResponsePacket> void send(RequestPacket packet, PacketCallback<T> packetCallback) {

        send(packet, ProtocolType.TCP, packetCallback);
    }

    @Override
    public boolean connected() {

        return socketChannel.isConnected() && !disconnected;
    }

    public int id() {

        return id;
    }

    public SocketChannel socketChannel() {

        return socketChannel;
    }

    public DatagramChannel datagramChannel() {

        return datagramChannel;
    }

    public void datagramChannel(DatagramChannel datagramChannel) {

        this.datagramChannel = datagramChannel;
    }

    public Protocol protocol() {

        return protocol;
    }

    public SocketAddress remoteAddress() {

        return remoteAddress;
    }
}
