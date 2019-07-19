package netty.iot.sevice;

import netty.iot.entity.SubscribeStore;

import java.util.List;

/**
 * 订阅存储接口
 *
 * @author hejq
 * @date 2019/7/19 9:14
 */
public interface SubscribeStoreService {

    /**
     * 删除订阅
     *
     * @param clientId 客户端id
     */
    void removeForClient(String clientId);


    /**
     * 获取订阅存储集
     *
     * @param topic 主题
     * @return 查询结果
     */
    List<SubscribeStore> search(String topic);
}
