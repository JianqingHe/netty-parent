package netty.iot.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import netty.iot.handler.MqttTransportHandler;
import netty.iot.proto.ProtoProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * mqtt server配置
 *
 * @author hejq
 * @date 2019/7/19 14:45
 */
@Component
@Slf4j
public class MqttServer {

    @Value("${mqtt.bind_address}")
    private String host;

    @Value("${mqtt.bind_port}")
    private Integer port;

    @Value("${mqtt.adaptor}")
    private String adaptorName;

    @Value("${mqtt.netty.leak_detector_level}")
    private String leakDetectorLevel;

    @Value("${mqtt.netty.boss_group_thread_count}")
    private Integer bossGroupThreadCount;

    @Value("${mqtt.netty.worker_group_thread_count}")
    private Integer workerGroupThreadCount;

    @Value("${mqtt.netty.max_payload_size}")
    private Integer maxPayloadSize;

    @Autowired
    private ProtoProcess protoProcess;

    private Channel serverChannel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    /**
     * 初始化netty服务器
     */
    @PostConstruct
    public void init() {
        log.info("Setting resource leak detector level to {}", leakDetectorLevel);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel.toUpperCase()));
        log.info("Starting MQTT transport...");
        bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
        workerGroup = new NioEventLoopGroup(workerGroupThreadCount);
        ServerBootstrap sp = new ServerBootstrap();
        sp.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("decoder", new MqttDecoder(maxPayloadSize));
                        pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                        MqttTransportHandler handler = new MqttTransportHandler(protoProcess);
                        pipeline.addLast(handler);
                    }
                });
    }

}
