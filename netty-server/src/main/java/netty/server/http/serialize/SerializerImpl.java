package netty.server.http.serialize;
import com.alibaba.fastjson.JSON;
/**
 * 序列化实现
 *
 * @author hejq
 * @date 2019/7/16 17:00
 */
public class SerializerImpl implements Serializer {

    /**
     * java 对象转换成二进制
     *
     * @param object
     */
    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    /**
     * 二进制转换成 java 对象
     *
     * @param clazz
     * @param bytes
     */
    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
