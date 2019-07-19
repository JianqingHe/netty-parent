package netty.iot.sevice;

import netty.iot.entity.RetainMessageStore;

/**
 * 消息存储服务接口
 *
 * @author hejq
 * @date 2019/7/18 17:05
 */
public interface RetainMessageStoreService {

    /**
     * 删除retain标志消息
     *
     * @param topic 主题
     */
    void remove(String topic);

    /**
     * 存储retain标志消息
     *
     * @param topic 主题
     * @param retainMessageStore 内部消息
     */
    void put(String topic, RetainMessageStore retainMessageStore);

}
