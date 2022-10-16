package top.waterlaw.priority;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Product {

    public static final String QUEUE_NAME = "priority";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();
        Map<String, Object> map = new HashMap<>();
        // 队列优先级设置为 10
        map.put("x-max-priority", 10);
        channel.queueDeclare(QUEUE_NAME, true, false, false, map);

        for (int i = 0; i < 10; i++) {
            String message = "info" + i;
            if(i == 5) {
                // 设置消息优先级
                AMQP.BasicProperties properties = new AMQP.BasicProperties()
                        .builder()
                        .priority(5)
                        .build();
                channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());

            } else {
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            }

        }

        System.out.println("success");
    }
}
