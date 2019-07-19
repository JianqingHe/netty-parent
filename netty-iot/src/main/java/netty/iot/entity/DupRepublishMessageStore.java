package netty.iot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 重复消息重新发送存储
 *
 * @author hejq
 * @date 2019/7/18 16:27
 */
@Data
@NoArgsConstructor
public class DupRepublishMessageStore implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    private String clientId;

    /**
     * 主题
     */
    private String topic;

    /**
     * 发送qos
     */
    private int mqttQos;

    /**
     * 消息id
     */
    private int messageId;

    /**
     * 消息内容 二进制
     */
    private byte[] messageBytes;

    public DupRepublishMessageStore setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public DupRepublishMessageStore setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public DupRepublishMessageStore setMqttQoS(int mqttQoS) {
        this.mqttQos = mqttQoS;
        return this;
    }

    public DupRepublishMessageStore setMessageBytes(byte[] messageBytes) {
        this.messageBytes = messageBytes;
        return this;
    }

    public DupRepublishMessageStore setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }
}
