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

package de.jackwhite20.cascade.server.impl;

import de.jackwhite20.cascade.server.AbstractCascadeServer;
import de.jackwhite20.cascade.shared.pipeline.PipelineUtils;
import de.jackwhite20.cascade.shared.pipeline.initialize.CascadeChannelInitializer;
import de.jackwhite20.cascade.shared.session.SessionListener;
import de.jackwhite20.cascade.shared.thread.CascadeThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;

import java.util.stream.Collectors;

/**
 * Created by JackWhite20 on 24.09.2016.
 */
public class CascadeServer extends AbstractCascadeServer {

    public CascadeServer(ServerConfig serverConfig) {

        super(serverConfig);
    }

    @Override
    public void start() {

        bossGroup = PipelineUtils.newEventLoopGroup(serverConfig.bossThreads(), new CascadeThreadFactory("Server Boss"));
        workerGroup = PipelineUtils.newEventLoopGroup(serverConfig.workerThreads(), new CascadeThreadFactory("Server Worker"));

        try {
            ServerBootstrap b = new ServerBootstrap();
            serverChannel = b.group(bossGroup, workerGroup)
                    .channel(PipelineUtils.getServerChannel())
                    .childHandler(new CascadeChannelInitializer(serverConfig.protocol(), serverConfig.sessionListener().stream().collect(Collectors.toList()), serverConfig.cryptoFunction()))
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_BACKLOG, serverConfig.backlog())
                    .bind(serverConfig.host(), serverConfig.port())
                    .sync()
                    .channel();

            serverConfig.sessionListener().forEach(SessionListener::onStarted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
