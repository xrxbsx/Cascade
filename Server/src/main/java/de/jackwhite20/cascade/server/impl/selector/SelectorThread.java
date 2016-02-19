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

package de.jackwhite20.cascade.server.impl.selector;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JackWhite20 on 26.07.2015.
 */
public class SelectorThread implements Runnable {

    private boolean running;

    private int id;

    private Selector selector;

    private ReentrantLock selectorLock;

    public SelectorThread(int id, ReentrantLock selectorLock) {

        this.id = id;
        this.selectorLock = selectorLock;
    }

    public int id() {

        return id;
    }

    public Selector selector() {

        return selector;
    }

    public void shutdown() {

        running = false;

        if(selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new IllegalStateException("failed to open selector " + id + ": " + e.getMessage());
        }

        running = true;

        while (running) {
            try {
                selectorLock.lock();
                selectorLock.unlock();

                if (selector.select() == 0)
                    continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();

                while(keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    keyIterator.remove();

                    SelectableChannel selectableChannel = key.channel();

                    if(!key.isValid())
                        continue;

                    if (key.isReadable()) {
                        // TODO: 19.02.2016
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
