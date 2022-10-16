package top.waterlaw;

import com.rabbitmq.client.*;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMqConsumer {
    public static final String QUEUE_NAME = "test";


    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        // 接收消息时回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(new String(message.getBody()));
        };
        // 消息取消消费的回调
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息取消消费");
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
