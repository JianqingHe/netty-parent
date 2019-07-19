package netty.iot.auth;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.springframework.util.StringUtils;

import java.security.interfaces.RSAPrivateKey;

/**
 * 账户接口
 *
 * @author hejq
 * @date 2019/7/18 16:19
 */
public class AuthServiceImpl implements AuthService {

    private RSAPrivateKey privateKey;

    /**
     * 检验用户名密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 校验结果
     */
    @Override
    public boolean checkAccount(String userName, String password) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return false;
        }

        RSA rsa = new RSA(privateKey, null);
        String checkValue = rsa.encryptBcd(userName, KeyType.PrivateKey);
        return checkValue.equals(password);
    }
}
