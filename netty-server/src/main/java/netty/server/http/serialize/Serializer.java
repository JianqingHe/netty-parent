package netty.server.http.serialize;

/**
 * 序列化接口
 *
 * @author hejq
 * @date 2019/7/16 17:00
 */
public interface Serializer {

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
