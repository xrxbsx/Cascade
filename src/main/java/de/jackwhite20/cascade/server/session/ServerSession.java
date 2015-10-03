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

package de.jackwhite20.cascade.server.session;

import de.jackwhite20.cascade.server.listener.ServerListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by JackWhite20 on 26.07.2015.
 */
public class ServerSession {

    private int id;

    private int tcpBufferSize;

    private int udpBufferSize;

    private SocketChannel socketChannel;

    private DatagramChannel datagramChannel;

    private ServerListener listener;

    private SelectionKey tcpKey;

    private SelectionKey udpKey;

    private SocketAddress remoteAddress;

    private ByteBuffer tcpBuffer;

    private ByteBuffer udpBuffer;

    public ServerSession(int id, int tcpBufferSize, int udpBufferSize, SocketChannel socketChannel, DatagramChannel datagramChannel, ServerListener listener, SelectionKey tcpKey, SelectionKey udpKey) {

        this.id = id;
        this.tcpBufferSize = tcpBufferSize;
        this.udpBufferSize = udpBufferSize;
        this.socketChannel = socketChannel;
        this.datagramChannel = datagramChannel;
        this.listener = listener;
        this.tcpKey = tcpKey;
        this.udpKey = udpKey;
        try {
            this.datagramChannel.configureBlocking(false);
            this.remoteAddress = socketChannel.getRemoteAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tcpBuffer = ByteBuffer.allocate(tcpBufferSize);
        this.udpBuffer = ByteBuffer.allocate(udpBufferSize);
    }

    public void close(boolean silent) {
        try {

            if(udpKey != null)
                udpKey.cancel();
            if(tcpKey != null)
                tcpKey.cancel();

            if(socketChannel == null)
                return;

            socketChannel.close();
            datagramChannel.disconnect();

            if(!silent)
                listener.onClientDisconnected(this);
        }catch (Exception e) {
            //
        }
    }

    public void close() {
        close(false);
    }

    public void readSocket() {

        int read = -1;

        tcpBuffer.clear();

        try {
            read = socketChannel.read(tcpBuffer);
        } catch (IOException e) {
            //
        }

        if(read == -1) {
            close();

            return;
        }

        tcpBuffer.flip();

        int dataLength = tcpBuffer.getInt();

        byte[] bytes = new byte[dataLength];
        tcpBuffer.get(bytes);

        if(listener != null)
            listener.onReceived(this, bytes);
    }

    public void readDatagram() {

        udpBuffer.clear();

        try {
            SocketAddress address = datagramChannel.receive(udpBuffer);

            if(address == null)
                return;

            udpBuffer.flip();

            int dataLength = udpBuffer.getInt();

            byte[] bytes = new byte[dataLength];
            udpBuffer.get(bytes);

            if(listener != null)
                listener.onReceived(this, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTCP(byte[] buffer) {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            dataOutputStream.writeInt(buffer.length);
            dataOutputStream.write(buffer);

            ByteBuffer sendBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

            socketChannel.write(sendBuffer);

            if(listener != null)
                listener.onSent(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUDP(byte[] buffer) {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            dataOutputStream.writeInt(buffer.length);
            dataOutputStream.write(buffer);

            ByteBuffer sendBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

            datagramChannel.send(sendBuffer, remoteAddress);

            if(listener != null)
                listener.onSent(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int id() {

        return id;
    }

    public int tcpBufferSize() {

        return tcpBufferSize;
    }

    public int udpBufferSize() {

        return udpBufferSize;
    }

    public SocketChannel socketChannel() {

        return socketChannel;
    }

    public DatagramChannel datagramChannel() {

        return datagramChannel;
    }

    public SocketAddress remoteAddress() {

        return remoteAddress;
    }
}
