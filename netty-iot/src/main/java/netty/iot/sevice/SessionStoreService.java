package netty.iot.sevice;
import netty.iot.entity.SessionStore;


/**
 * session 消息存储接口
 *
 * @author hejq
 * @date 2019/7/18 17:21
 */
public interface SessionStoreService {

    /**
     * 存储消息
     *
     * @param clientId     客户id
     * @param sessionStore session消息
     */
    void put(String clientId, SessionStore sessionStore);

    /**
     * 通过clientId查询session消息
     *
     * @param clientId clientId
     * @return session消息
     */
    SessionStore getByClientId(String clientId);


    /**
     * clientId的会话是否存在
     *
     * @param clientId clientId
     * @return 是否存在
     */
    boolean containsKey(String clientId);

    /**
     * 删除会话
     *
     * @param clientId clientId
     */
    void removeByClientId(String clientId);
}
