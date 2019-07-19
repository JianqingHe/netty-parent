package netty.iot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订阅存储
 *
 * @author hejq
 * @date 2019/7/19 9:10
 */
@Data
@NoArgsConstructor
public class SubscribeStore implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    private String clientId;

    /**
     * 主题过滤条件
     */
    private  String topicFilter;

    /**
     * mqtt qos
     */
    private int mqttQos;

    public SubscribeStore(String clientId, String topicFilter, int mqttQos) {
        this.clientId = clientId;
        this.topicFilter = topicFilter;
        this.mqttQos = mqttQos;
    }
}
