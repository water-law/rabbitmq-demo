package top.waterlaw;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Work01 {
    public static final String QUEUE_NAME = "test";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        // 接收消息时回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("接收到消息：" + new String(message.getBody()));
            try {
                Thread.sleep(30*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 手动 ACK, 第二个参数表示不批量 ACK 此 channel 上的消息
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        // 消息取消消费的回调
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag+"消息取消消费");
        };
        System.out.println("C2队列等待消费");
        // 不公平分发
        int prefetchCount = 5;
        channel.basicQos(prefetchCount);
        // 手动 ACK
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
