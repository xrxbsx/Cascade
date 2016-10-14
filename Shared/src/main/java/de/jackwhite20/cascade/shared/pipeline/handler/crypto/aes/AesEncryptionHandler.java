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

package de.jackwhite20.cascade.shared.pipeline.handler.crypto.aes;

import de.jackwhite20.cascade.shared.security.CryptoFunction;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by JackWhite20 on 14.10.2016.
 */
public class AesEncryptionHandler extends MessageToMessageEncoder<ByteBuf> {

    protected CryptoFunction cryptoFunction;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {

        if (byteBuf.readableBytes() > 0) {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);

            try {
                byte[] encrypted = cryptoFunction.encrypt(bytes);

                out.add(ctx
                        .alloc()
                        .buffer(encrypted.length)
                        .writeBytes(encrypted));
            } catch (Exception e) {
                ctx.fireExceptionCaught(e);
            }
        }
    }

    public void setCryptoFunction(CryptoFunction cryptoFunction) {

        this.cryptoFunction = cryptoFunction;
    }
}
