package netty.iot.sevice;

import netty.iot.entity.InternalMessage;

/**
 * kafka接口
 *
 * @author hejq
 * @date 2019/7/18 17:04
 */
public interface KafkaService {

    /**
     * kafka发送消息
     *
     * @param internalMessage 内部消息
     */
    void send(InternalMessage internalMessage);
}
