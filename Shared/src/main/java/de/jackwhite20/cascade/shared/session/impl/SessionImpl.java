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
import de.jackwhite20.cascade.shared.server.Reactor;
import de.jackwhite20.cascade.shared.session.Session;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by JackWhite20 on 24.02.2016.
 */
public class SessionImpl implements Session, Runnable {

    private static final int READ_BUF_SIZE = 2048;

    private int id;

    private Reactor reactor;

    private SocketChannel socketChannel;

    private SelectionKey selectionKey;

    private ByteBuffer readBuf = ByteBuffer.allocate(READ_BUF_SIZE);

    private Queue<ByteBuffer> sendQueue = new ConcurrentLinkedQueue<>();

    private boolean disconnected = false;

    private Protocol protocol;

    private SocketAddress remoteAddress;

    private ConcurrentHashMap<Integer, PacketCallback> callbackPackets = new ConcurrentHashMap<>();

    public SessionImpl(int id, Reactor reactor, Selector selector, SocketChannel socketChannel, Protocol protocol) throws IOException {

        this.id = id;
        this.reactor = reactor;
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        this.remoteAddress = this.socketChannel.getRemoteAddress();
        this.protocol = protocol;

        // Register for read events
        selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        // Attach the this session object
        selectionKey.attach(this);

        // Wakeup if it is in the blocking select call
        selector.wakeup();
    }

    private void write() throws IOException {

        while (sendQueue.size() > 0) {
            socketChannel.write(sendQueue.poll());
        }

        selectionKey.interestOps(SelectionKey.OP_READ);
        selectionKey.selector().wakeup();
    }

    private void read() throws IOException {

        try {
            int numBytes = socketChannel.read(readBuf);

            if (numBytes == -1) {
                close();
            } else {
                readBuf.limit(readBuf.capacity());

                readBuf.flip();

                while (readBuf.remaining() > 0) {
                    readBuf.mark();

                    if (readBuf.remaining() < 4)
                        break;

                    int readableBytes = readBuf.getInt();
                    if (readBuf.remaining() < readableBytes) {
                        readBuf.reset();
                        break;
                    }

                    System.out.println("ReadableBytes: " + readableBytes);

                    byte[] bytes = new byte[readableBytes];
                    readBuf.get(bytes);

                    if (bytes.length == 0) {
                        System.err.println("0 bytes!");
                        System.exit(0);
                    }

                    reactor.workerThreadPool().execute(() -> process(bytes));
                }

                readBuf.compact();
            }
        } catch (IOException ex) {
            close();
        }
    }

    @SuppressWarnings("all")
    private void process(byte[] bytes) {

        try {
            PacketReader packetReader = new PacketReader(bytes);
            byte packetId = packetReader.readByte();

            Packet packet = protocol.create(packetId);
            packet.read(packetReader);

            if (!(packet instanceof ResponsePacket)) {
                protocol.call(packet.getClass(), this, packet);
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

    @Override
    public void run() {

        System.out.println("Readable: " + selectionKey.isReadable());
        System.out.println("Writeable: " + selectionKey.isWritable());
        try {
            if (selectionKey.isReadable()) {
                read();
            } else if (selectionKey.isWritable()) {
                write();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {

        if(disconnected)
            return;

        selectionKey.cancel();
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        reactor.sessionListener().onDisconnected(this);

        disconnected = true;
    }

    @Override
    public void send(Packet packet) {

        try {
            PacketWriter packetWriter = new PacketWriter();
            packetWriter.writeByte(protocol.findId(packet.getClass()));
            packet.write(packetWriter);

            byte[] buffer = packetWriter.bytes();

            ByteBuffer sendBuffer = ByteBuffer.allocate(buffer.length + 4);
            sendBuffer.putInt(buffer.length);
            sendBuffer.put(buffer);
            sendBuffer.flip();

            // Add to send queue for later processing
            sendQueue.offer(sendBuffer);

            // Set the key's interest to WRITE operation if not already there
            if ((selectionKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
            }

            // Wakeup if it is in the blocking select call
            selectionKey.selector().wakeup();
        } catch (Exception e) {
            close();

            e.printStackTrace();
        }
    }

    @Override
    public <T extends ResponsePacket> void send(RequestPacket packet, PacketCallback<T> packetCallback) {

        callbackPackets.put(packet.callbackId(), packetCallback);

        send(packet);
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
