package org.studing.netty.client.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.studing.netty.client.FixMessage;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FixDecoder extends ReplayingDecoder<FixMessage> {

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) throws Exception {

        StringBuilder tag = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean readingTag = true;
        boolean messageProcessing = true;
        FixMessage message = new FixMessage();

        while(messageProcessing) {
            CharSequence seq = in.readCharSequence(1, StandardCharsets.US_ASCII);
            char c = seq.charAt(0);


            if (c == '=') {
                readingTag = false;
            }

            else if (c == (char)1) {
                readingTag = true;
                try {
                    message.setTag(new String(tag), new String(value));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


                if (new String(tag).equals("10")) {

                    out.add(message);
                    messageProcessing = false;
                }
                tag = new StringBuilder();
                value = new StringBuilder();
            }

            else if (readingTag) {
                tag.append(c);
            }

            else {
                value.append(c);
            }
        }
    }
}
