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
import de.jackwhite20.cascade.client.listener.ClientListenerAdapter;
import de.jackwhite20.cascade.client.settings.ClientSettings;

/**
 * Created by JackWhite20 on 03.10.2015.
 */
public class ClientTest extends ClientListenerAdapter {

    public static void main(String[] args) {

        ClientTest clientTest = new ClientTest();

        ClientSettings settings = new ClientSettings.ClientSettingsBuilder()
                .withHost("localhost")
                .withPort(12345)
                .withListener(clientTest)
                .withTcpBufferSize(1024)
                .withUdpBufferSize(1024)
                .build();
        Client client = new Client(settings);

        client.connect();

        clientTest.setClient(client);
    }

    private Client client;

    public void setClient(Client client) {

        this.client = client;
    }

    @Override
    public void onConnected() {

        System.out.println("Connected!");

        client.sendUDP("Init".getBytes());
    }

    @Override
    public void onDisconnected() {

        System.out.println("Disconnected!");
    }

    @Override
    public void onReceived(byte[] buffer) {

        System.out.println("Received " + buffer.length + "bytes!");

        client.sendUDP("Ping".getBytes());
    }
}
