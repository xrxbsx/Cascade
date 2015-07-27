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

package de.jackwhite20.cascade.server.listener;

import de.jackwhite20.cascade.server.ServerSession;

/**
 * Created by JackWhite20 on 27.07.2015.
 */
public class ServerListenerAdapter implements ServerListener {

    @Override
    public void onServerStarted() {

    }

    @Override
    public void onClientConnected(int clientId, ServerSession session) {

    }

    @Override
    public void onClientDisconnected(ServerSession session) {

    }

    @Override
    public void onSent(ServerSession session) {

    }

    @Override
    public void onReceived(ServerSession session, byte[] buffer) {

    }

}
