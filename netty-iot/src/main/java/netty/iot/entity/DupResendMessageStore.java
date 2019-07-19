package netty.iot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 重新发送消息
 *
 * @author hejq
 * @date 2019/7/18 16:46
 */
@Data
@NoArgsConstructor
public class DupResendMessageStore implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 消息id
     */
    private int messageId;

    public DupResendMessageStore setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }

}
