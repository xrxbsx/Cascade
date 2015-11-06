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

package de.jackwhite20.cascade.shared.session;

import de.jackwhite20.cascade.shared.Disconnectable;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.List;

/**
 * Created by JackWhite20 on 13.10.2015.
 */
public class Session {

    private static final int DEFAULT_TCP_BUFFER_SIZE = 1024;

    private static final int DEFAULT_UDP_BUFFER_SIZE = 65535;

    private static final int BUFFER_GROW_FACTOR = 2;

    private int id;

    private SocketChannel socketChannel;

    private DatagramChannel datagramChannel;

    private List<SessionListener> listener;

    private SocketAddress remoteAddress;

    private ByteBuffer tcpBuffer;

    private ByteBuffer udpBuffer;

    private Disconnectable disconnectable;

    public Session(int id, SocketChannel socketChannel, DatagramChannel datagramChannel, List<SessionListener> listener, Disconnectable disconnectable) {

        this.id = id;
        this.socketChannel = socketChannel;
        this.datagramChannel = datagramChannel;
        this.listener = listener;
        try {
            this.datagramChannel.configureBlocking(false);
            this.remoteAddress = socketChannel.getRemoteAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tcpBuffer = ByteBuffer.allocate(DEFAULT_TCP_BUFFER_SIZE);
        this.udpBuffer = ByteBuffer.allocate(DEFAULT_UDP_BUFFER_SIZE);
        this.disconnectable = disconnectable;
    }

    public Session(int id, SocketChannel socketChannel, DatagramChannel datagramChannel, List<SessionListener> listener) {

        this(id, socketChannel, datagramChannel, listener, null);
    }

    public void close() {

        if (disconnectable != null) {
            disconnectable.disconnect();
        } else {
            try {
                if (socketChannel != null)
                    socketChannel.close();

                if (datagramChannel != null)
                    datagramChannel.disconnect();
            } catch (Exception e) {
                listener.forEach(sessionListener -> sessionListener.onException(this, e));
            } finally {
                listener.forEach(sessionListener -> sessionListener.onDisconnected(this));
            }
        }
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

            if (tcpBuffer.remaining() < 4)
                break;

            int readableBytes = tcpBuffer.getInt();
            if (tcpBuffer.remaining() < readableBytes) {
                tcpBuffer.reset();
                break;
            }

            byte[] bytes = new byte[readableBytes];
            tcpBuffer.get(bytes);


            listener.forEach(sessionListener -> sessionListener.onReceived(this, bytes, ProtocolType.TCP));
        }

        tcpBuffer.compact();
    }

    public void readDatagram() {

        try {
            while (datagramChannel.receive(udpBuffer) != null) {
                udpBuffer.flip();

                byte[] bytes = new byte[udpBuffer.remaining()];
                udpBuffer.get(bytes);


                listener.forEach(sessionListener -> sessionListener.onReceived(this, bytes, ProtocolType.UDP));

                udpBuffer.clear();
            }
        } catch (IOException e) {
            close();

            listener.forEach(sessionListener -> sessionListener.onException(this, e));
        }
    }

    public void sendReliable(byte[] buffer) {

        ByteBuffer sendBuffer = ByteBuffer.allocate(buffer.length + 4);
        sendBuffer.putInt(buffer.length);
        sendBuffer.put(buffer);
        sendBuffer.flip();

        try {
            socketChannel.write(sendBuffer);
        } catch (IOException e) {
            close();

            listener.forEach(sessionListener -> sessionListener.onException(this, e));
        }
    }

    public void sendUnreliable(byte[] buffer) {

        try {
            datagramChannel.send(ByteBuffer.wrap(buffer), remoteAddress);
        } catch (IOException e) {
            close();

            listener.forEach(sessionListener -> sessionListener.onException(this, e));
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

    public List<SessionListener> listener() {

        return Collections.unmodifiableList(listener);
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
