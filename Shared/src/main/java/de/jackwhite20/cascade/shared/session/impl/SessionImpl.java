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

import de.jackwhite20.cascade.shared.Compressor;
import de.jackwhite20.cascade.shared.Disconnectable;
import de.jackwhite20.cascade.shared.pool.BufferPool;
import de.jackwhite20.cascade.shared.pool.ByteBuf;
import de.jackwhite20.cascade.shared.protocol.Protocol;
import de.jackwhite20.cascade.shared.protocol.io.PacketReader;
import de.jackwhite20.cascade.shared.protocol.io.PacketWriter;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.PacketInfo;
import de.jackwhite20.cascade.shared.session.ProtocolType;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.List;

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

    private Compressor compressor;

    private List<SessionListener> listener;

    private SocketAddress remoteAddress;

    private ByteBuffer tcpBuffer;

    private ByteBuffer udpBuffer;

    private Disconnectable disconnectable;

    private int compressionThreshold;

    private boolean disconnected = false;

    private Protocol protocol;

    public SessionImpl(int id, SocketChannel socketChannel, List<SessionListener> listener, Disconnectable disconnectable, int compressionThreshold, Protocol protocol) {

        this.id = id;
        this.socketChannel = socketChannel;
        this.compressor = new Compressor();
        this.listener = listener;
        try {
            this.remoteAddress = socketChannel.getRemoteAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tcpBuffer = ByteBuffer.allocate(DEFAULT_TCP_BUFFER_SIZE);
        this.udpBuffer = ByteBuffer.allocate(DEFAULT_UDP_BUFFER_SIZE);
        this.disconnectable = disconnectable;
        this.compressionThreshold = compressionThreshold;
        this.protocol = protocol;
    }

    public SessionImpl(int id, SocketChannel socketChannel, List<SessionListener> listener, int compressionThreshold, Protocol protocol) {

        this(id, socketChannel, listener, null, compressionThreshold, protocol);
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

                if (datagramChannel != null)
                    datagramChannel.close();
            } catch (Exception e) {
                listener.forEach(sessionListener -> sessionListener.onException(this, e));
            } finally {
                listener.forEach(sessionListener -> sessionListener.onDisconnected(this));
            }
        }

        disconnected = true;
    }

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

            if (tcpBuffer.remaining() < 5)
                break;

            byte isCompressed = tcpBuffer.get();
            int readableBytes = tcpBuffer.getInt();
            if (tcpBuffer.remaining() < readableBytes) {
                tcpBuffer.reset();
                break;
            }

            byte[] bytes = new byte[readableBytes];
            tcpBuffer.get(bytes);

            byte[] decompressed = (isCompressed == 0) ? bytes : compressor.decompress(bytes);

            PacketReader packetReader = new PacketReader(decompressed);
            try {
                byte packetId = packetReader.readByte();

                Packet packet = protocol.create(packetId);
                packet.read(packetReader);

                protocol.call(packet.getClass(), this, packet, ProtocolType.TCP);
            } catch (Exception e) {
                e.printStackTrace();
                close();
            }
        }

        tcpBuffer.compact();
    }

    public void readDatagram() {

        try {
            while (datagramChannel.receive(udpBuffer) != null) {
                udpBuffer.flip();

                if(udpBuffer.remaining() < 5)
                    continue;

                byte isCompressed = udpBuffer.get();
                int readableBytes = udpBuffer.getInt();

                byte[] bytes = new byte[readableBytes];
                udpBuffer.get(bytes);

                byte[] decompressed = (isCompressed == 0) ? bytes : compressor.decompress(bytes);

                PacketReader packetReader = new PacketReader(decompressed);
                try {
                    byte packetId = packetReader.readByte();

                    Packet packet = protocol.create(packetId);
                    packet.read(packetReader);

                    protocol.call(packet.getClass(), this, packet, ProtocolType.UDP);
                } catch (Exception e) {
                    e.printStackTrace();
                    close();
                }

                udpBuffer.clear();
            }
        } catch (IOException e) {
            close();

            listener.forEach(sessionListener -> sessionListener.onException(this, e));
        }
    }

    public void send(Packet packet, ProtocolType protocolType) {

        PacketWriter packetWriter = new PacketWriter();

        try {
            packetWriter.writeByte(protocol.findId(packet.getClass()));
            packet.write(packetWriter);

            byte[] buffer = packetWriter.bytes();

            boolean shouldCompress = (buffer.length >= compressionThreshold);

            byte[] compressed = (!shouldCompress) ? buffer : compressor.compress(buffer);

            ByteBuf sendBuffer = BufferPool.acquire(compressed.length + 5);
            sendBuffer.put((!shouldCompress) ? (byte) 0 : (byte) 1);
            sendBuffer.putInt(compressed.length);
            sendBuffer.put(compressed);
            sendBuffer.flip();

            if(protocolType == ProtocolType.UDP) {
                datagramChannel.send(sendBuffer.nioBuffer(), datagramChannel.getRemoteAddress());
            } else
                socketChannel.write(sendBuffer.nioBuffer());

            sendBuffer.release();
        } catch (Exception e) {
            close();

            e.printStackTrace();
        }
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

    public Compressor compressor() {

        return compressor;
    }

    public int compressionThreshold() {

        return compressionThreshold;
    }

    public List<SessionListener> listener() {

        return Collections.unmodifiableList(listener);
    }

    public Protocol protocol() {

        return protocol;
    }

    public SocketAddress remoteAddress() {

        return remoteAddress;
    }

    public ByteBuffer tcpBuffer() {

        return tcpBuffer;
    }

    public ByteBuffer udpBuffer() {

        return udpBuffer;
    }
}
