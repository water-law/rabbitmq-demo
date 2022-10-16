package top.waterlaw.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer01 {

    // 交换机名称
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        // 声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 声明一个队列
        channel.queueDeclare("Q1", false, false, false, null);
        // 绑定队列和交换机 路由
        channel.queueBind("Q1", EXCHANGE_NAME, "*.orange.*");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("1收到消息：" + new String(message.getBody(), "UTF-8"));
        };
        channel.basicConsume("Q1", true, deliverCallback, consumerTag -> {});
    }
}
