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

package de.jackwhite20.cascade.shared.protocol.listener;

import de.jackwhite20.cascade.shared.protocol.Packet;
import de.jackwhite20.cascade.shared.session.Session;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JackWhite20 on 02.01.2016.
 */
public class Listeners {

    private Map<PacketListener, ArrayList<Method>> listeners = new HashMap<>();

    public void register(PacketListener messageListener, Method method) {

        if(listeners.containsKey(messageListener)) {
            listeners.get(messageListener).add(method);
        }else {
            listeners.put(messageListener, new ArrayList<>(Collections.singletonList(method)));
        }
    }

    public void call(Session session, Packet packet) {

        listeners.forEach((l, m) -> m.forEach(method -> {
            try {
                method.invoke(l, session, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
