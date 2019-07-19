package netty.iot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Retain标志消息存储
 *
 * @author hejq
 * @date 2019/7/18 17:05
 */
@Data
@NoArgsConstructor
public class RetainMessageStore implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主题
     */
    private String topic;

    /**
     * 报文 二进制
     */
    private byte[] messageBytes;

    /**
     * qos
     */
    private int mqttQos;

    public RetainMessageStore setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public RetainMessageStore setMessageBytes(byte[] messageBytes) {
        this.messageBytes = messageBytes;
        return this;
    }

    public RetainMessageStore setMqttQoS(int mqttQos) {
        this.mqttQos = mqttQos;
        return this;
    }
}
