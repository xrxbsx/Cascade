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

    private SessionListener listener;

    private List<Option> options = new ArrayList<>();

    public int backLog() {

        return backLog;
    }

    public int selectorCount() {

        return selectorCount;
    }

    public SessionListener listener() {

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

    public static class Builder {

        private CascadeSettings instance = new CascadeSettings();

        public <T> Builder withOption(SocketOption<T> socketOption, T value) {

            instance.options.add(new Option<>(socketOption, value));

            return this;
        }

        public Builder withBackLog(int backLog) {

            instance.backLog = backLog;

            return this;
        }

        public Builder withSelectorCount(int selectorCount) {

            instance.selectorCount = selectorCount;

            return this;
        }

        public Builder withListener(SessionListener listener) {

            instance.listener = listener;

            return this;
        }

        public CascadeSettings build() {

            return instance;
        }
    }
}
