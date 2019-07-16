package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 服务端
 *
 * @author hejq
 * @date 2019/7/16 14:12
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        // boss 线程组: 用于服务端接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // worker 线程组： 用于进行客户端的SocketChannel的数据读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sp = new ServerBootstrap();
            sp.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer());

            ChannelFuture cf = sp.bind(8888);
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
