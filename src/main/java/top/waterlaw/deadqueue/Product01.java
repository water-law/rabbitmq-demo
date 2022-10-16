package top.waterlaw.deadqueue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Product01 {
    // 普通交换机名称
    public static final String NORMAL_EXCHANGE_NAME = "normal_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
//        AMQP.BasicProperties properties = new AMQP.BasicProperties()
//                .builder()
//                .expiration("10000")
//                .build();
        for (int i = 0; i < 10; i++) {
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE_NAME, "zhangsan", null, message.getBytes());
        }
    }
}
