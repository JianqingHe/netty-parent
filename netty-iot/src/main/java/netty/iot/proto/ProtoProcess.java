package netty.iot.proto;

import netty.iot.sevice.SessionStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * proto进程管理
 *
 * @author hejq
 * @date 2019/7/19 14:07
 */
@Component
public class ProtoProcess {

    @Autowired
    private SessionStoreService sessionStoreService;

    private Connect connect;

    private Publish publish;

    private DisConnect disConnect;

    public Connect connect() {
        if (null == connect) {
            connect = new Connect();
        }
        return connect;
    }

    public Publish publish() {
        if (null == publish) {
            publish = new Publish();
        }
        return publish;
    }

    public DisConnect disConnect() {
        if (null == disConnect) {
            disConnect = new DisConnect();
        }
        return disConnect;
    }

    public SessionStoreService getSessionStoreService() {
        return sessionStoreService;
    }
}
