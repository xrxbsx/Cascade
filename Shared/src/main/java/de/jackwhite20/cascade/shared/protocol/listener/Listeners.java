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

import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.session.Session;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JackWhite20 on 02.01.2016.
 */
public class Listeners {

    private Map<PacketListener, ArrayList<Method>> listeners = new ConcurrentHashMap<>();

    public void register(PacketListener packetListener, Method method) {

        if(listeners.containsKey(packetListener)) {
            listeners.get(packetListener).add(method);
        }else {
            listeners.put(packetListener, new ArrayList<>(Collections.singletonList(method)));
        }
    }

    public void unregister(PacketListener packetListener) {

        listeners.remove(packetListener);
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
