package netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 客户端
 *
 * @author hejq
 * @date 2019/7/16 14:12
 */
public class Client {

    public static void main(String[] args) throws InterruptedException, IOException {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bs = new Bootstrap();
            bs.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());

            Channel ch = bs.connect("127.0.0.1", 8888).sync().channel();
            ChannelFuture lastWriteFuture;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (null == line) {
                    break;
                }

                lastWriteFuture = ch.writeAndFlush(line + "\r\n");
                if ("bye".equalsIgnoreCase(line)) {
                    ch.closeFuture().sync();
                    break;
                }

                if (null != lastWriteFuture) {
                    lastWriteFuture.sync();
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
