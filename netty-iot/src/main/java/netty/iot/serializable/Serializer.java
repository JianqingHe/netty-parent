package netty.iot.serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import netty.iot.entity.SessionStore;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 序列化接口
 *
 * @author hejq
 * @date 2019/7/18 17:08
 */
public class Serializer implements RedisSerializer<Object> {

    private static Gson gson = new GsonBuilder().create();

    /**
     * 对象转二进制数据
     *
     * @param object 需要转换的数据
     * @return 转换后的二进制数据
     * @throws SerializationException 序列话转换异常
     */
    @Override
    public byte[] serialize(Object object) throws SerializationException {
        return  gson.toJson(object).getBytes();
    }

    /**
     * 二进制转换成bean数据
     *
     * @param bytes 二进制数据
     * @return bean数据
     * @throws SerializationException 反序列化异常
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return gson.fromJson(new String(bytes), SessionStore.class);
    }
}
