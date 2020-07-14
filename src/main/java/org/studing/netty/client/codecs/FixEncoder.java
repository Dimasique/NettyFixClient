package org.studing.netty.client.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.studing.netty.client.FixMessage;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FixEncoder extends MessageToByteEncoder<FixMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          FixMessage msg, ByteBuf out) throws Exception {

        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> tags = msg.getTags();

        for(String key : tags.keySet()) {
            stringBuilder.append(key).append(tags.get(key)).append(FixMessage.SEPARATOR);
        }

        out.writeBytes(new String(stringBuilder).getBytes(StandardCharsets.US_ASCII));
    }
}
