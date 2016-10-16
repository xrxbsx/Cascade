/*
 * Copyright (c) 2016 "JackWhite20"
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

package de.jackwhite20.cascade.example.client.ssl;

import de.jackwhite20.cascade.client.Client;
import de.jackwhite20.cascade.client.ClientFactory;
import de.jackwhite20.cascade.example.client.echo.EchoClientPacketListener;
import de.jackwhite20.cascade.example.client.echo.EchoClientProtocol;
import de.jackwhite20.cascade.example.shared.echo.ChatPacket;
import de.jackwhite20.cascade.example.shared.echo.TestObject;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.ssl.SslUtil;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;

/**
 * Created by JackWhite20 on 16.10.2016.
 */
public class SslClient {

    public static void main(String[] args) {

        new SslClient("localhost", 12345).connect();
    }

    private String host;

    private int port;

    SslContext sslContext;

    private Client client;

    public SslClient(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void connect() {

        try {
            // Create an ssl context with the util class
            // You can/should build one by yourself
            sslContext = SslUtil.insecureForClient();

            // Custom ssl context from a file cert and key
            //sslContext = SslUtil.customForClient(new File("keyCertChainFile"), new File("keyFile"));
        } catch (SSLException e) {
            e.printStackTrace();
        }

        // Create a new ssl client
        client = ClientFactory.createSsl(new SslClientConfig(new EchoClientProtocol(new EchoClientPacketListener()), sslContext));
        client.addSessionListener(new SessionListener() {

            @Override
            public void onConnected(Session session) {

                // Keep in mind that the onConnected method gets invoked after a successful SSL handshake

                System.out.println("Connected!");

                String message = "Hey my friend.";
                System.out.println("Sending to Server: " + message);
                // Send the packet reliable (TCP) to the server
                client.send(new ChatPacket(0, message, new TestObject("Some string")));
            }

            @Override
            public void onDisconnected(Session session) {

                System.out.println("Disconnected!");
            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onStopped() {

            }
        });
        client.connect();
    }

    public Client client() {

        return client;
    }
}
