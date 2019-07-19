package netty.iot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 内部消息
 *
 * @author hejq
 * @date 2019/7/19 15:21
 */
@Data
@NoArgsConstructor
public class InternalMessage implements Serializable {
    public static final long serialVersionUID = -1L;

    private String clientId;

    private String topic;

    private int mqttQoS;

    private byte[] messageBytes;

    private boolean retain;

    private boolean dup;

    public InternalMessage setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public InternalMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public InternalMessage setMqttQoS(int mqttQoS) {
        this.mqttQoS = mqttQoS;
        return this;
    }

    public InternalMessage setMessageBytes(byte[] messageBytes) {
        this.messageBytes = messageBytes;
        return this;
    }

    public InternalMessage setRetain(boolean retain) {
        this.retain = retain;
        return this;
    }

    public InternalMessage setDup(boolean dup) {
        this.dup = dup;
        return this;
    }
}
