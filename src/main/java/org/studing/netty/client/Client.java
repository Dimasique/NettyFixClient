package org.studing.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.studing.netty.client.helpers.FixClientHandler;
import org.studing.netty.client.codecs.FixDecoder;
import org.studing.netty.client.codecs.FixEncoder;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] args) throws Exception {

        String host = "0.0.0.0";
        int port = 9880;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new FixEncoder(),
                            new FixDecoder(), new IdleStateHandler(20,2000000,2000000, TimeUnit.SECONDS),
                            new FixClientHandler());
                }
            });

            ChannelFuture f = b.connect(host, port).sync();

            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss.SSS")
                    .withZone(ZoneOffset.UTC);

            Thread.sleep(200);

            FixMessage messageOrder = new FixMessage("D");
            messageOrder.setTag("49", "BANZAI");
            messageOrder.setTag("56", "EXEC");
            messageOrder.setMsgSeqNum();
            messageOrder.setTag("52", DATE_TIME_FORMATTER.format(new Date().toInstant()));
            messageOrder.setTag("54", "1");


            messageOrder.setTag("55", "a");
            messageOrder.setTag("40", "1");
            messageOrder.setTag("11", "1");
            messageOrder.setTag("60", DATE_TIME_FORMATTER.format(new Date().toInstant()));
            messageOrder.setTag("38", "1");
            messageOrder.prepareToSend();
            f.channel().writeAndFlush(messageOrder);

            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
