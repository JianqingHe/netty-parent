package netty.iot.sevice;

/**
 * 生产报文id接口
 *
 * @author hejq
 * @date 2019/7/18 17:05
 */
public interface MessageIdService {

    /**
     * 获取下一个报文标识符
     *
     * @return 下一个报文标识符
     */
    int getNextMessageId();
}
