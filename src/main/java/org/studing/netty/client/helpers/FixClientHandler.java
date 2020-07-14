package org.studing.netty.client.helpers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.studing.netty.client.FixMessage;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FixClientHandler extends ChannelInboundHandlerAdapter {

    private final DateTimeFormatter DATE_TIME_FORMATTER;

    public FixClientHandler() {
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss.SSS")
                .withZone(ZoneOffset.UTC);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {

        FixMessage message = new FixMessage("A");

        message.setTag("49", "BANZAI");
        message.setTag("56", "EXEC");
        message.setMsgSeqNum();
        message.setTag("52", DATE_TIME_FORMATTER.format(new Date().toInstant()));
        message.setTag("98", "0");
        message.setTag("108", "30");
        message.setTag("141", "Y");
        message.setTag("553", "Dima");
        message.setTag("554", "123");
        message.prepareToSend();

        ctx.writeAndFlush(message);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println((FixMessage)msg);
        //ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        FixMessage message = new FixMessage("0");
        try {
            message.setTag("49", "BANZAI");
            message.setTag("56", "EXEC");
            message.setMsgSeqNum();

            message.setTag("52", DATE_TIME_FORMATTER.format(new Date().toInstant()));
            message.prepareToSend();
            ctx.writeAndFlush(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
