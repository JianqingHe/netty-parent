package netty.iot.proto;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import netty.iot.auth.AuthService;
import netty.iot.entity.DupRepublishMessageStore;
import netty.iot.entity.DupResendMessageStore;
import netty.iot.entity.SessionStore;
import netty.iot.sevice.DupRepublishMessageStoreService;
import netty.iot.sevice.DupResendMessageStoreService;
import netty.iot.sevice.SessionStoreService;
import netty.iot.sevice.SubscribeStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 连接
 *
 * @author hejq
 * @date 2019/7/19 9:08
 */
@Slf4j
public class Connect {

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionStoreService sessionStoreService;

    @Autowired
    private DupRepublishMessageStoreService republishMessageStoreService;

    @Autowired
    private DupResendMessageStoreService resendMessageStoreService;

    @Autowired
    private SubscribeStoreService subscribeStoreService;

    /**
     * 处理连接过程
     *
     * @param channel        信道
     * @param connectMessage 连接信息
     */
    public void processConnect(Channel channel, MqttConnectMessage connectMessage) {
        // 消息解码异常
        if (connectMessage.decoderResult().isFailure()) {
            Throwable cause = connectMessage.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false), null);
                channel.writeAndFlush(connAckMessage);
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的clientId
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
                channel.writeAndFlush(connAckMessage);
            }
            channel.close();
            return;
        }
        // clientId为空或null的情况, 这里要求客户端必须提供clientId, 不管cleanSession是否为1, 此处没有参考标准协议实现
        if (StringUtils.isEmpty(connectMessage.payload().clientIdentifier())) {
            MqttConnAckMessage ackMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
            channel.writeAndFlush(ackMessage);
            return;
        }

        // 账户验证
        String userName = connectMessage.payload().userName();
        String password = connectMessage.payload().passwordInBytes() == null ? null : new String(connectMessage.payload().passwordInBytes(), CharsetUtil.UTF_8);
        if (!authService.checkAccount(userName, password)) {
            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false), null);
            channel.writeAndFlush(connAckMessage);
            channel.close();
            return;
        }
        // 如果会话中已存储这个新连接的clientId, 就关闭之前该clientId的连接
        if (sessionStoreService.containsKey(connectMessage.payload().clientIdentifier())) {
            SessionStore sessionStore = sessionStoreService.getByClientId(connectMessage.payload().clientIdentifier());
            Channel previous = sessionStore.getChannel();
            Boolean cleanSession = sessionStore.isCleanSession();
            if (cleanSession) {
                sessionStoreService.removeByClientId(connectMessage.payload().clientIdentifier());
                subscribeStoreService.removeForClient(connectMessage.payload().clientIdentifier());
                republishMessageStoreService.removeByClient(connectMessage.payload().clientIdentifier());
                resendMessageStoreService.removeByClient(connectMessage.payload().clientIdentifier());
            }
            previous.close();
        }
        //处理遗嘱信息
        SessionStore sessionStore = new SessionStore(connectMessage.payload().clientIdentifier(), channel, connectMessage.variableHeader().isCleanSession(), null);
        if (connectMessage.variableHeader().isWillFlag()) {
            MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(connectMessage.variableHeader().willQos()), connectMessage.variableHeader().isWillRetain(), 0),
                    new MqttPublishVariableHeader(connectMessage.payload().willTopic(), 0),
                    Unpooled.buffer().writeBytes(connectMessage.payload().willMessageInBytes())
            );
            sessionStore.setWillMessage(willMessage);
        }
        //处理连接心跳包
        if (connectMessage.variableHeader().keepAliveTimeSeconds() > 0) {
            if (channel.pipeline().names().contains("idle")) {
                channel.pipeline().remove("idle");
            }
            channel.pipeline().addFirst("idle", new IdleStateHandler(0, 0, Math.round(connectMessage.variableHeader().keepAliveTimeSeconds() * 1.5f)));
        }
        //至此存储会话消息及返回接受客户端连接
        sessionStoreService.put(connectMessage.payload().clientIdentifier(), sessionStore);
        //将clientId存储到channel的map中
        channel.attr(AttributeKey.valueOf("clientId")).set(connectMessage.payload().clientIdentifier());
        Boolean sessionPresent = sessionStoreService.containsKey(connectMessage.payload().clientIdentifier()) && !connectMessage.variableHeader().isCleanSession();
        MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent),
                null
        );
        channel.writeAndFlush(okResp);
        log.info("CONNECT - clientId: {}, cleanSession: {}", connectMessage.payload().clientIdentifier(), connectMessage.variableHeader().isCleanSession());
        // 如果cleanSession为0, 需要重发同一clientId存储的未完成的QoS1和QoS2的DUP消息
        if (!connectMessage.variableHeader().isCleanSession()) {
            List<DupRepublishMessageStore> dupPublishMessageStoreList = republishMessageStoreService.getByClientId(connectMessage.payload().clientIdentifier());
            dupPublishMessageStoreList.forEach(dupPublishMessageStore -> {
                MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBLISH, true, MqttQoS.valueOf(dupPublishMessageStore.getMqttQos()), false, 0),
                        new MqttPublishVariableHeader(dupPublishMessageStore.getTopic(), dupPublishMessageStore.getMessageId()),
                        Unpooled.buffer().writeBytes(dupPublishMessageStore.getMessageBytes())
                );
                channel.writeAndFlush(publishMessage);
            });
            List<DupResendMessageStore> dupPubRelMessageStoreList = resendMessageStoreService.getByClientId(connectMessage.payload().clientIdentifier());
            dupPubRelMessageStoreList.forEach(dupPubRelMessageStore -> {
                MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBREL, true, MqttQoS.AT_MOST_ONCE, false, 0),
                        MqttMessageIdVariableHeader.from(dupPubRelMessageStore.getMessageId()),
                        null
                );
                channel.writeAndFlush(pubRelMessage);
            });
        }
    }
}
