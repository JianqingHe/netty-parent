package netty.iot.proto;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import netty.iot.entity.SessionStore;
import netty.iot.sevice.DupRepublishMessageStoreService;
import netty.iot.sevice.DupResendMessageStoreService;
import netty.iot.sevice.SessionStoreService;
import netty.iot.sevice.SubscribeStoreService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 关闭连接
 *
 * @author hejq
 * @date 2019/7/19 9:59
 */
@Slf4j
public class DisConnect {

    @Autowired
    private SessionStoreService sessionStoreService;

    @Autowired
    private SubscribeStoreService subscribeStoreService;

    @Autowired
    private DupRepublishMessageStoreService republishMessageStoreService;

    @Autowired
    private DupResendMessageStoreService resendMessageStoreService;

    /**
     * 关闭连接
     *
     * @param channel 信道
     */
    public void disConnect(Channel channel) {
        String clientId = channel.attr(AttributeKey.valueOf("clientId")).get().toString();
        SessionStore sessionStore = sessionStoreService.getByClientId(clientId);
        if (null != sessionStore && sessionStore.isCleanSession()) {
            subscribeStoreService.removeForClient(clientId);
            republishMessageStoreService.removeByClient(clientId);
            resendMessageStoreService.removeByClient(clientId);
            log.info("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
        }
        sessionStoreService.removeByClientId(clientId);
        channel.close();
    }

    public void processDisConnect(Channel channel,MqttMessage msg){
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        SessionStore sessionStore = sessionStoreService.getByClientId(clientId);
        if (sessionStore!=null && sessionStore.isCleanSession()){
            subscribeStoreService.removeForClient(clientId);
            republishMessageStoreService.removeByClient(clientId);
            resendMessageStoreService.removeByClient(clientId);
        }
        log.info("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
        sessionStoreService.removeByClientId(clientId);
        channel.close();
    }
}
