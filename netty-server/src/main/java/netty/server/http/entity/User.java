package netty.server.http.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 用户
 *
 * @author hejq
 * @date 2019/7/16 17:10
 */
@Data
public class User {

    /**
     * 姓名
     */
    private String name;

    /**
     * 方法
     */
    private String method;

    /**
     * 时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime = LocalDateTime.now();

    public User(String name) {
        this.name = name;
    }

    public User() {

    }
}
