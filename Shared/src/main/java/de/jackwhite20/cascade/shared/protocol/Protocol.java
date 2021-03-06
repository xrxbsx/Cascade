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

package de.jackwhite20.cascade.shared.protocol;

import de.jackwhite20.cascade.shared.protocol.listener.Listeners;
import de.jackwhite20.cascade.shared.protocol.listener.PacketHandler;
import de.jackwhite20.cascade.shared.protocol.listener.PacketListener;
import de.jackwhite20.cascade.shared.protocol.packet.Packet;
import de.jackwhite20.cascade.shared.protocol.packet.PacketInfo;
import de.jackwhite20.cascade.shared.session.Session;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JackWhite20 on 02.01.2016.
 */
public class Protocol {

    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    private static final String CLASS_FILE_SUFFIX = ".class";

    private HashMap<Byte, Class<? extends Packet>> packets = new HashMap<>();

    private HashMap<Class<? extends Packet>, Byte> packetByteMap = new HashMap<>();

    private HashMap<Class<?>, Listeners> listeners = new HashMap<>();

    /**
     * Registers a new packet to the protocol.
     * The class must have a PacketInfo annotation with a value specified.
     *
     * @param clazz the packet class.
     */
    public void registerPacket(Class<? extends Packet> clazz) {

        if(clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null");
        }

        PacketInfo packetInfo = clazz.getAnnotation(PacketInfo.class);
        if(packetInfo == null) {
            throw new IllegalArgumentException("class " + clazz.getSimpleName() + " has no PacketInfo annotation");
        }

        byte id = packetInfo.value();

        if(packets.containsKey(id)) {
            throw new IllegalArgumentException("packet with value " + id + " is already registered");
        }

        packets.put(id, clazz);
        packetByteMap.put(clazz, id);
    }

    /**
     * Registers all packet classes in the given package and sub package.
     *
     * @param packageName the package name to start with.
     */
    public void registerPackets(String packageName) {

        for (Class<?> aClass : scanPackage(packageName)) {
            //noinspection unchecked
            registerPacket(((Class<? extends Packet>) aClass));
        }
    }

    /**
     * Unregisters a packet from the protocol.
     * The class must have a PacketInfo annotation with a value specified.
     *
     * @param clazz the packet class.
     */
    public void unregisterPacket(Class<? extends Packet> clazz) {

        if(clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null");
        }

        PacketInfo packetInfo = clazz.getAnnotation(PacketInfo.class);
        if(packetInfo == null) {
            throw new IllegalArgumentException("class " + clazz.getSimpleName() + " has no PacketInfo annotation");
        }

        byte id = packetInfo.value();

        if(!packets.containsKey(id)) {
            throw new IllegalArgumentException("packet with value " + id + " is not registered");
        }

        packets.remove(id);
        packetByteMap.remove(clazz);
    }

    /**
     * Registers a packet listener.
     *
     * @param packetListener the listener.
     */
    public void registerListener(PacketListener packetListener) {

        if(packetListener == null) {
            throw new IllegalArgumentException("packetListener cannot be null");
        }

        for (Method method : packetListener.getClass().getDeclaredMethods()) {
            if(method.getParameterCount() < 2) {
                continue;
            }

            if(!method.isAnnotationPresent(PacketHandler.class)) {
                continue;
            }

            Class<?> paramType = method.getParameterTypes()[1];

            if (!Packet.class.isAssignableFrom(paramType)) {
                continue;
            }

            if (!listeners.containsKey(paramType)) {
                listeners.put(paramType, new Listeners());
            }

            listeners.get(paramType).register(packetListener, method);
        }
    }

    /**
     * Unregisters a packet listener and it's method for the packet handler.
     *
     * @param packetListener the listener.
     */
    public void unregisterListener(PacketListener packetListener) {

        if(packetListener == null) {
            throw new IllegalArgumentException("packetListener cannot be null");
        }

        for (Method method : packetListener.getClass().getDeclaredMethods()) {
            if(method.getParameterCount() < 2) {
                continue;
            }

            if(!method.isAnnotationPresent(PacketHandler.class)) {
                continue;
            }

            Class<?> paramType = method.getParameterTypes()[1];

            if (!Packet.class.isAssignableFrom(paramType)) {
                continue;
            }

            if(listeners.containsKey(paramType)) {
                listeners.get(paramType).unregister(packetListener);
            }
        }
    }

    /**
     * Calls all methods from the listener which are registered with packet class.
     *
     * @param clazz the packet class.
     * @param session the session.
     * @param packet the packet instance.
     */
    public void call(Class<? extends Packet> clazz, Session session, Packet packet) {

        if(clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null");
        }

        if(session == null) {
            throw new IllegalArgumentException("session cannot be null");
        }

        if(packet == null) {
            throw new IllegalArgumentException("packet cannot be null");
        }

        Listeners l = listeners.get(clazz);
        if(l != null) {
            l.call(session, packet);
        } else {
            throw new IllegalStateException("no listener for packet " + clazz.getName());
        }
    }

    /**
     * Gets the value from the packet class with which it was registered.
     *
     * @param clazz the class.
     * @return the packet value.
     */
    public byte findId(Class<? extends Packet> clazz) throws IllegalStateException {

        if(!packetByteMap.containsKey(clazz)) {
            throw new IllegalStateException("the class " + clazz.getSimpleName() + " is not registered as a packet");
        }

        return packetByteMap.get(clazz);
    }

    /**
     * Creates an instance of a packet from the value.
     *
     * @param id the value.
     * @return the packet instance.
     */
    public Packet create(byte id) throws IllegalStateException {

        if(!packets.containsKey(id)) {
            throw new IllegalStateException("a packet with value " + id + " does not exists");
        }

        try {
            return packets.get(id).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Class<?>> scanPackage(String packageName) {

        String scannedPath = packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL url = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (url == null) {
            throw new IllegalArgumentException("package " + packageName + " does not exist");
        }

        File scannedDir = new File(url.getFile());
        List<Class<?>> classes = new ArrayList<>();

        File[] files = scannedDir.listFiles();
        if (files == null) {
            throw new IllegalStateException();
        }

        for (File file : files) {
            classes.addAll(scanSubPackages(file, packageName));
        }

        return classes;
    }

    private List<Class<?>> scanSubPackages(File file, String scannedPackage) {

        List<Class<?>> classes = new ArrayList<>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                throw new IllegalStateException();
            }

            for (File child : files) {
                classes.addAll(scanSubPackages(child, resource));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(PacketInfo.class)) {
                    classes.add(clazz);
                }
            } catch (ClassNotFoundException ignore) {
            }
        }

        return classes;
    }
}
