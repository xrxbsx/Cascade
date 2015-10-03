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

import de.jackwhite20.cascade.client.Client;
import de.jackwhite20.cascade.client.session.ClientSession;
import de.jackwhite20.cascade.client.ClientThread;
import de.jackwhite20.cascade.client.listener.ClientListenerAdapter;
import de.jackwhite20.cascade.client.settings.ClientSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JackWhite20 on 03.10.2015.
 */
public class ClientTest extends ClientListenerAdapter {

    public static void main(String[] args) throws Exception {

        List<ClientSession> sessions = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            ClientTest clientTest = new ClientTest();

            ClientSettings settings = new ClientSettings.ClientSettingsBuilder()
                    .withName("CascadeClient")
                    .withHost("localhost")
                    .withPort(12345)
                    .withListener(clientTest)
                    .withTcpBufferSize(1024)
                    .withUdpBufferSize(1024)
                    .build();
            Client client = new Client(settings);

            //System.out.println("Connecting to " + client.host() + ":" + client.port());

            ClientSession session = client.connect().get();

            sessions.add(session);
        }

        Scanner scanner = new Scanner(System.in);

        String line = null;
        while ((line = scanner.nextLine()) != null) {
            for (ClientSession session : sessions) {
                session.sendUDP(line.getBytes());
            }
        }
    }

    private ClientSession session;

    public static final AtomicInteger PONGS = new AtomicInteger(0);

    @Override
    public void onConnected(ClientSession session) {

        this.session = session;

        System.out.println("Connected!");

        //session.sendTCP("Init".getBytes());
    }

    @Override
    public void onDisconnected(ClientSession session) {

        System.out.println("Disconnected!");
    }

    @Override
    public void onReceived(byte[] buffer) {

        System.out.println("Received " + buffer.length + "bytes!");

        System.out.println("Pongs Received: " + PONGS.incrementAndGet());
    }
}
