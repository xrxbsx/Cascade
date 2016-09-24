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

package de.jackwhite20.cascade.shared.pipeline;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.PlatformDependent;

import java.util.concurrent.ThreadFactory;

/**
 * Created by JackWhite20 on 24.09.2016.
 */
public class PipelineUtils {

    private static boolean epoll;

    static {
        if (!PlatformDependent.isWindows()) {
            epoll = Epoll.isAvailable();
        }
    }

    private PipelineUtils() {
        // No instance
    }

    public static EventLoopGroup newEventLoopGroup(int threads, ThreadFactory factory) {

        return epoll ? new EpollEventLoopGroup(threads, factory) : new NioEventLoopGroup(threads, factory);
    }

    public static Class<? extends ServerChannel> getServerChannel() {

        return epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends Channel> getChannel() {

        return epoll ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static void closeOnFlush(Channel ch) {

        if (ch != null && ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
