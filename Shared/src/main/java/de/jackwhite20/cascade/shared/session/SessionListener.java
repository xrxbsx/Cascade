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

package de.jackwhite20.cascade.shared.session;

/**
 * Created by JackWhite20 on 13.10.2015.
 */
public interface SessionListener {

    /**
     * Called when the session has connected.
     *
     * @param session the session instance.
     */
    void onConnected(Session session);

    /**
     * Called when the session has disconnected.
     *
     * @param session the session instance.
     */
    void onDisconnected(Session session);

    /**
     * Called when the server has started.
     */
    void onStarted();

    /**
     * Called when the server has stopped.
     */
    void onStopped();
}
