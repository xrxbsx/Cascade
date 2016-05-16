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

package de.jackwhite20.cascade.server;

import de.jackwhite20.cascade.shared.session.SessionListener;

/**
 * Created by JackWhite20 on 19.02.2016.
 */
public interface Server {

    /**
     * Starts the server with the passed server config.
     */
    void start();

    /**
     * Sets the session listener.
     *
     * @param sessionListener the session listener.
     */
    void sessionListener(SessionListener sessionListener);

    /**
     * Stops the server and frees all used resources.
     */
    void stop();

    /**
     * Returns if the server is running.
     *
     * @return true if running otherwise false.
     */
    boolean running();
}
