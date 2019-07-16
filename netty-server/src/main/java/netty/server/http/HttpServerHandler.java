package netty.server.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import lombok.extern.slf4j.Slf4j;
import netty.server.http.entity.User;
import netty.server.http.serialize.SerializerImpl;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.springframework.http.MediaType;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * http拦截
 *
 * @author hejq
 * @date 2019/7/16 16:56
 */
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private HttpHeaders headers;
    private FullHttpRequest fullRequest;

    private static final String FAVICON_ICO = "/favicon.ico";
    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    /**
     * 建立连接，发送消息通知
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write("It is " + LocalDateTime.now() + " now.\r\n");
        log.info("[netty-process] connection success!!\r\n");
        ctx.flush();
    }

    /**
     * <strong>Please keep in mind that this method will be renamed to
     * {@code messageReceived(ChannelHandlerContext, I)} in 5.0.</strong>
     * <p>
     * Is called for each message of type {@link }.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        User user = new User("hejq");

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            headers = request.headers();
            String uri = request.uri();
            log.info("[http uri] {}", uri);
            if (FAVICON_ICO.equals(uri)) {
                return;
            }
            HttpMethod method = request.method();
            if (HttpMethod.GET.equals(method)) {
                QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
                Map<String, List<String>> uriAttributes = queryDecoder.parameters();
                //此处仅打印请求参数（你可以根据业务需求自定义处理）
                for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                    for (String attrVal : attr.getValue()) {
                        log.info(attr.getKey() + "=" + attrVal);
                    }
                }
                user.setMethod("get");
            } else if (HttpMethod.POST.equals(method)) {
                //POST请求,由于你需要从消息体中获取数据,因此有必要把msg转换成FullHttpRequest
                fullRequest = (FullHttpRequest)msg;
                //根据不同的Content_Type处理body数据
                dealWithContentType();
                user.setMethod("post");
            }

            SerializerImpl serializer = new SerializerImpl();
            byte[] content = serializer.serialize(user);

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }

    /**
     * 处理content-type
     */
    private void dealWithContentType() {
        String contentType = getContentType();
        //可以使用HttpJsonDecoder
        switch (contentType) {
            case MediaType.APPLICATION_JSON_VALUE: {
                String jsonStr = fullRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
                JSONObject obj = JSON.parseObject(jsonStr);
                for (Map.Entry<String, Object> item : obj.entrySet()) {
                    log.info("[msg] -> " + item.getKey() + "=" + item.getValue().toString());
                }
                break;
            }
            case MediaType.APPLICATION_FORM_URLENCODED_VALUE: {
                //方式一：使用 QueryStringDecoder
                String jsonStr = fullRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
                QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
                Map<String, List<String>> uriAttributes = queryDecoder.parameters();
                for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                    for (String attrVal : attr.getValue()) {
                        log.info("[msg] -> " + attr.getKey() + "=" + attrVal);
                    }
                }
                break;
            }
            case MediaType.MULTIPART_FORM_DATA_VALUE:
                log.info("文件上传");
                break;
            default:
                log.info("其他");
                break;
        }
    }

    /**
     * 捕获异常
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 完成消息读取
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("[netty-process] complete!!\r\n");
        ctx.flush();
    }

    /**
     * 获取contentType
     *
     * @return 参数content-type
     */
    private String getContentType(){
        String typeStr = headers.get("Content-Type");
        String[] list = typeStr.split(";");
        return list[0];
    }

}
