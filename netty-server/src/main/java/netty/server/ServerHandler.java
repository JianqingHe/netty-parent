package netty.server;

import io.netty.channel.*;

import java.net.InetAddress;
import java.time.LocalDateTime;

/**
 * 服务拦截
 *
 * @author hejq
 * @date 2019/7/16 14:30
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 建立连接，发送消息通知
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        ctx.write("It is " + LocalDateTime.now() + " now.\r\n");
        ctx.flush();
    }

    /**
     * 根据传入内容做出相应处理
     *
     * @param ctx 信道拦截
     * @param request 传入的内容
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        String response;
        boolean close = false;
        if (request.isEmpty()) {
            response = "please type something. \r\n";
        } else if ("bye".equalsIgnoreCase(request)) {
            response = "goodbye !! \r\n";
            close = true;
        } else {
            response = LocalDateTime.now() + ": Did you say '" + request + "' ? \r\n";
        }
        ChannelFuture cf = ctx.write(response);
        if (close) {
            cf.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 消息读取完成操作
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
    }

    /**
     * 异常处理
     * @param ctx
     * @param e
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        e.printStackTrace();
        ctx.flush();
    }
}
