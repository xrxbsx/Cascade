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

package de.jackwhite20.cascade.shared.pipeline.handler.crypto.xor;

import de.jackwhite20.cascade.shared.security.CryptoFunction;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by JackWhite20 on 14.10.2016.
 */
public class XorDecryptionHandler extends MessageToMessageDecoder<ByteBuf> {

    private CryptoFunction cryptoFunction;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {

        int length = byteBuf.readInt();
        if (length > byteBuf.readableBytes()) {
            throw new IllegalStateException(String.format("cannot read byte array longer than %s bytes (got %s bytes)", byteBuf.readableBytes(), length));
        }

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        try {
            byte[] decrypted = cryptoFunction.decrypt(bytes);

            out.add(ctx
                    .alloc()
                    .buffer(decrypted.length)
                    .writeInt(decrypted.length)
                    .writeBytes(decrypted));
        } catch (Exception e) {
            ctx.fireExceptionCaught(e);
        }
    }

    public void setCryptoFunction(CryptoFunction cryptoFunction) {

        this.cryptoFunction = cryptoFunction;
    }
}
