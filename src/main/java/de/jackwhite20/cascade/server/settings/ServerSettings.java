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

package de.jackwhite20.cascade.server.settings;

/**
 * Created by JackWhite20 on 26.07.2015.
 */
public class ServerSettings {

    private String name;

    private int backLog;

    private int selectorCount;

    private int tcpBufferSize;

    private int udpBufferSize;

    public String name() {
        return name;
    }

    public int backLog() {
        return backLog;
    }

    public int selectorCount() {
        return selectorCount;
    }

    public int tcpBufferSize() {
        return tcpBufferSize;
    }

    public int udpBufferSize() {
        return udpBufferSize;
    }

    public static class ServerSettingsBuilder {

        private static ServerSettings instance = new ServerSettings();

        public ServerSettingsBuilder withName(String name) {
            instance.name = name;

            return this;
        }

        public ServerSettingsBuilder withBackLog(int backLog) {
            instance.backLog = backLog;

            return this;
        }

        public ServerSettingsBuilder withSelectorCount(int selectorCount) {
            instance.selectorCount = selectorCount;

            return this;
        }

        public ServerSettingsBuilder withTcpBufferSize(int tcpBufferSize) {
            instance.tcpBufferSize = tcpBufferSize;

            return this;
        }

        public ServerSettingsBuilder withUdpBufferSize(int udpBufferSize) {
            instance.udpBufferSize = udpBufferSize;

            return this;
        }

        public ServerSettings build() {
            return instance;
        }

    }

}
