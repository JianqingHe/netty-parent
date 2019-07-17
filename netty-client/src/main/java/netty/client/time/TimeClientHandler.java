package netty.client.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * time handler
 *
 * @author hejq
 * @date 2019/7/17 9:48
 */
@Slf4j
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf buf;

    /**
     * add
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(4);
    }

    /**
     * remove
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release();
        buf = null;
    }

    /**
     * 读取消息
     *
     * @param ctx ChannelHandlerContext
     * @param msg 消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;
        buf.writeBytes(m);
        m.release();

        if (buf.readableBytes() >= 4) {
            Long currentTimeMills = (m.readUnsignedInt() - 2208988800L) * 1000L;
            log.info("[time] {}", new Date(currentTimeMills));
            ctx.close();
        }
    }

    /**
     * 捕获异常
     *
     * @param ctx ChannelHandlerContext
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("[error] {}", cause.getMessage());
        ctx.close();
    }
}
