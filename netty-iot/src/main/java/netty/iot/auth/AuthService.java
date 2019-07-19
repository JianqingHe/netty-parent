package netty.iot.auth;

/**
 * 账户接口
 *
 * @author hejq
 * @date 2019/7/18 16:19
 */
public interface AuthService {

    /**
     * 检验用户名密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 校验结果
     */
    boolean checkAccount(String userName, String password);
}
