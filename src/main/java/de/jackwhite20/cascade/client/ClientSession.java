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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by JackWhite20 on 03.10.2015.
 */
public class ClientSession {

    private Client client;

    private SocketChannel socketChannel;

    private DatagramChannel datagramChannel;

    private ByteBuffer tcpBuffer;

    private ByteBuffer udpBuffer;

    private ClientListener listener;

    public ClientSession(Client client, SocketChannel socketChannel, DatagramChannel datagramChannel, ByteBuffer tcpBuffer, ByteBuffer udpBuffer, ClientListener listener) {

        this.client = client;
        this.socketChannel = socketChannel;
        this.datagramChannel = datagramChannel;
        this.tcpBuffer = tcpBuffer;
        this.udpBuffer = udpBuffer;
        this.listener = listener;
    }

    public void close() {

        try {
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            listener.onReceived(bytes);
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
                listener.onReceived(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTCP(byte[] buffer) {

        if(!socketChannel.isOpen())
            return;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            dataOutputStream.writeInt(buffer.length);
            dataOutputStream.write(buffer);

            byte[] ordered = byteArrayOutputStream.toByteArray();

            ByteBuffer sendBuffer = ByteBuffer.wrap(ordered);

            socketChannel.write(sendBuffer);

            if(listener != null)
                listener.onSent(ordered);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUDP(byte[] buffer) {

        if(!socketChannel.isOpen())
            return;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            dataOutputStream.writeInt(buffer.length);
            dataOutputStream.write(buffer);

            byte[] ordered = byteArrayOutputStream.toByteArray();

            ByteBuffer sendBuffer = ByteBuffer.wrap(ordered);

            datagramChannel.write(sendBuffer);

            if(listener != null)
                listener.onSent(ordered);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
