package top.waterlaw.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer02 {

    // 交换机名称
    public static final String LOGS_EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        // 声明一个交换机
        channel.exchangeDeclare(LOGS_EXCHANGE_NAME, "fanout");
        // 声明一个队列
        String queueName = channel.queueDeclare().getQueue();
        // 绑定队列和交换机
        channel.queueBind(queueName, LOGS_EXCHANGE_NAME, "");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("2收到消息：" + new String(message.getBody(), "UTF-8"));
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}
