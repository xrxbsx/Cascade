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

package de.jackwhite20.cascade.example.server.ssl;

import de.jackwhite20.cascade.example.server.echo.EchoServerProtocol;
import de.jackwhite20.cascade.example.shared.echo.ChatPacket;
import de.jackwhite20.cascade.server.Server;
import de.jackwhite20.cascade.server.ServerFactory;
import de.jackwhite20.cascade.shared.protocol.listener.PacketHandler;
import de.jackwhite20.cascade.shared.protocol.listener.PacketListener;
import de.jackwhite20.cascade.shared.session.Session;
import de.jackwhite20.cascade.shared.session.SessionListener;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * Created by JackWhite20 on 16.10.2016.
 */
public class SslServer implements PacketListener {

    public static void main(String[] args) {

        new SslServer("0.0.0.0", 12345).start();
    }

    private String host;

    private int port;

    SslContext sslContext;

    private Server server;

    public SslServer(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public void start() {

        try {
            // Create an ssl context with a self signed certificate
            // You can/should build one by yourself
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .build();

            // Custom ssl context from a file cert and key
            //sslContext = SslUtil.customForServer(new File("keyCertChainFile"), new File("keyFile"));
        } catch (CertificateException | SSLException e) {
            e.printStackTrace();
        }

        // Create a new server instance with the given server config class
        server = ServerFactory.createSsl(new SslServerConfig(new EchoServerProtocol(this), sslContext));
        server.addSessionListener(new SessionListener() {

            @Override
            public void onConnected(Session session) {

                // Keep in mind that the onConnected method gets invoked after a successful SSL handshake

                System.out.println("Client connected!");
            }

            @Override
            public void onDisconnected(Session session) {

                System.out.println("Client disconnected!");
            }

            @Override
            public void onStarted() {

                System.out.println("Server started!");
            }

            @Override
            public void onStopped() {

                System.out.println("Server stopped!");
            }
        });
        server.start();
    }

    /**
     * The method needs a @PacketHandler annotation and the session as the first argument.
     *
     * The second param needs to be your packet class for which this method is responsible for.
     */
    @PacketHandler
    public void onChatPacket(Session session, ChatPacket chatPacket) {

        System.out.println("Received from Client: " + chatPacket);

        // Send the packet back with the same ProtocolType
        session.send(chatPacket);
    }

    public Server server() {

        return server;
    }
}
