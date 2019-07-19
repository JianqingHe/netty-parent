package netty.iot.sevice.impl;

import netty.iot.entity.SessionStore;
import netty.iot.sevice.SessionStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息存储接口
 *
 * @author hejq
 * @date 2019/7/19 15:39
 */
@Service
public class SessionStoreServiceImpl implements SessionStoreService {

    private Map<String, SessionStore> sessionCache = new ConcurrentHashMap<>();
    /**
     * 存储消息
     *
     * @param clientId     客户id
     * @param sessionStore session消息
     */
    @Override
    public void put(String clientId, SessionStore sessionStore) {
        this.sessionCache.put(clientId, sessionStore);
    }

    /**
     * 通过clientId查询session消息
     *
     * @param clientId clientId
     * @return session消息
     */
    @Override
    public SessionStore getByClientId(String clientId) {
        return sessionCache.get(clientId);
    }

    /**
     * clientId的会话是否存在
     *
     * @param clientId clientId
     * @return 是否存在
     */
    @Override
    public boolean containsKey(String clientId) {
        return sessionCache.containsKey(clientId);
    }

    /**
     * 删除会话
     *
     * @param clientId clientId
     */
    @Override
    public void removeByClientId(String clientId) {
        this.sessionCache.remove(clientId);
    }
}
