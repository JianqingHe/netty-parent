package netty.iot.sevice;


import netty.iot.entity.DupResendMessageStore;

import java.util.List;

/**
 * 重新发送消息接口
 *
 * @author hejq
 * @date 2019/7/18 17:03
 */
public interface DupResendMessageStoreService {

    /**
     * 删除订阅
     *
     * @param clientId 客户端id
     */
    void removeByClient(String clientId);

    /**
     * 通过客户端id获取重新发布消息
     *
     * @param clientId 客户端id
     * @return 需重新发布的消息
     */
    List<DupResendMessageStore> getByClientId(String clientId);
}
