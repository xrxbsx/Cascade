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

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by JackWhite20 on 26.07.2015.
 */
public class SelectorThread implements Runnable {

    private boolean running;

    private int id;

    private Selector selector;

    public SelectorThread(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public Selector selector() {
        return selector;
    }

    @Override
    public void run() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            System.err.println("Failed to open Selector " + id + "!");
        }

        running = true;

        while (running) {
            try {
                if(selector.select() == 0) {
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();

                while(keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    SelectableChannel selectableChannel = key.channel();

                    if(!key.isValid())
                        continue;
                }

                keys.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
