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

package de.jackwhite20.cascade.server;

import de.jackwhite20.cascade.server.impl.ServerConfig;
import de.jackwhite20.cascade.shared.session.SessionListener;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

/**
 * Created by JackWhite20 on 16.10.2016.
 */
public abstract class AbstractCascadeServer implements Server {

    protected ServerConfig serverConfig;

    protected EventLoopGroup bossGroup;

    protected EventLoopGroup workerGroup;

    protected Channel serverChannel;

    public AbstractCascadeServer(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
    }

    @Override
    public abstract void start();

    @Override
    public void stop() {

        serverChannel.close();

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        serverConfig.sessionListener().forEach(SessionListener::onStopped);
    }

    @Override
    public boolean isRunning() {

        return serverChannel.isActive();
    }

    @Override
    public void addSessionListener(SessionListener... sessionListener) {

        serverConfig.sessionListener(sessionListener);
    }
}
