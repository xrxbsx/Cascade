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

package de.jackwhite20.cascade.client.impl;

import de.jackwhite20.cascade.client.CascadeAbstractClient;
import de.jackwhite20.cascade.shared.pipeline.PipelineUtils;
import de.jackwhite20.cascade.shared.pipeline.initialize.CascadeChannelInitializer;
import de.jackwhite20.cascade.shared.thread.CascadeThreadFactory;
import io.netty.bootstrap.Bootstrap;

import java.net.InetSocketAddress;

/**
 * Created by JackWhite20 on 16.10.2016.
 */
public class CascadeSslClient extends CascadeAbstractClient {

    public CascadeSslClient(ClientConfig clientConfig) {

        super(clientConfig);

        if (clientConfig.sslContext() == null) {
            throw new IllegalArgumentException("sslContext must be set in the client config");
        }
    }

    @Override
    public void connect() {

        workerGroup = PipelineUtils.newEventLoopGroup(clientConfig.workerThreads(), new CascadeThreadFactory("Client"));

        Bootstrap b = new Bootstrap();
        b.group(workerGroup)
                .channel(PipelineUtils.getChannel())
                .handler(new CascadeChannelInitializer(clientConfig.host(), clientConfig.port(), clientConfig.sslContext(), clientConfig.protocol(), clientConfig.sessionListener(), clientConfig.cryptoFunction()))
                .remoteAddress(new InetSocketAddress(clientConfig.host(), clientConfig.port()));

        try {
            this.channel = b.connect().sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
