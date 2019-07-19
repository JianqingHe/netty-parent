package netty.iot.entity;

import java.io.Serializable;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.netty.channel.Channel;

/**
 * session存储
 *
 * @author hejq
 * @date 2019/7/18 17:12
 */
@Data
@NoArgsConstructor
public class SessionStore implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    private String clientId;

    /**
     * 通讯
     */
    private Channel channel;

    /**
     * 是否清除缓存
     */
    private boolean cleanSession;

    /**
     * mqtt发布消息
     */
    private MqttPublishMessage willMessage;

    public SessionStore(String clientId, Channel channel, boolean cleanSession, MqttPublishMessage willMessage) {
        this.clientId = clientId;
        this.channel = channel;
        this.cleanSession = cleanSession;
        this.willMessage = willMessage;
    }

    public SessionStore setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public SessionStore setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public SessionStore setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
        return this;
    }

    public MqttPublishMessage getWillMessage() {
        return willMessage;
    }

    public SessionStore setWillMessage(MqttPublishMessage willMessage) {
        this.willMessage = willMessage;
        return this;
    }
}
