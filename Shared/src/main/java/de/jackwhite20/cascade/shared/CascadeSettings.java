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

package de.jackwhite20.cascade.shared;

import de.jackwhite20.cascade.shared.session.SessionListener;

import java.net.SocketOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackWhite20 on 16.10.2015.
 */
public class CascadeSettings {

    private int backLog;

    private int selectorCount = 2;

    private List<SessionListener> listener = new ArrayList<>();

    private List<Option> options = new ArrayList<>();

    public int backLog() {

        return backLog;
    }

    public int selectorCount() {

        return selectorCount;
    }

    public List<SessionListener> listener() {

        return listener;
    }

    public List<Option> options() {

        return options;
    }

    public static class Option<T> {

        private SocketOption<T> socketOption;

        private T value;

        public Option(SocketOption<T> socketOption, T value) {

            this.socketOption = socketOption;
            this.value = value;
        }

        public SocketOption<T> socketOption() {

            return socketOption;
        }

        public T value() {

            return value;
        }
    }


    /**
     * Represents a class to easily build a settings object.
     */
    public static class Builder {

        private CascadeSettings instance = new CascadeSettings();

        /**
         * Adds a socket option with the value.
         *
         * @param socketOption a StandardSocketOptions value.
         * @param value the value.
         * @return the builder.
         */
        public <T> Builder withOption(SocketOption<T> socketOption, T value) {

            instance.options.add(new Option<>(socketOption, value));

            return this;
        }

        /**
         * Sets the backlog.
         * Will only take effect on a server.
         *
         * @param backLog the backlog.
         * @return the builder.
         */
        public Builder withBackLog(int backLog) {

            instance.backLog = backLog;

            return this;
        }

        /**
         * Sets the count of the selectors (thread count that handles IO from clients).
         *
         * @param selectorCount the count.
         * @return the builder.
         */
        public Builder withSelectorCount(int selectorCount) {

            instance.selectorCount = selectorCount;

            return this;
        }

        /**
         * Adds a session listener.
         *
         * @param listener the listener.
         * @return the builder.
         */
        public Builder withListener(SessionListener listener) {

            instance.listener.add(listener);

            return this;
        }

        /**
         * Returns the CascadeSettings object with the values.
         *
         * @return the settings object.
         */
        public CascadeSettings build() {

            return instance;
        }
    }
}
