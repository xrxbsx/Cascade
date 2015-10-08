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

package de.jackwhite20.cascade.settings;

import de.jackwhite20.cascade.client.listener.ClientListenerAdapter;
import de.jackwhite20.cascade.client.settings.ClientSettings;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by JackWhite20 on 08.10.2015.
 */
public class ClientSettingsTest {

    ClientListenerAdapter clientListenerAdapter;

    @Before
    public void prepare() {

        clientListenerAdapter = new ClientListenerAdapter();
    }

    @Test
    public void testClientSettings() {

        ClientSettings settings = new ClientSettings.Builder()
                .withName("TestClient")
                .withTcpBufferSize(1024)
                .withUdpBufferSize(1024)
                .withListener(clientListenerAdapter)
                .build();

        assertEquals(settings.name(), "TestClient");
        assertEquals(settings.tcpBufferSize(), 1024);
        assertEquals(settings.udpBufferSize(), 1024);
        assertEquals(settings.listener(), clientListenerAdapter);
    }
}
