package top.waterlaw.deadqueue;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Consumer01 {
    // 普通交换机名称
    public static final String NORMAL_EXCHANGE_NAME = "normal_exchange";
    // 死信交换机名称
    public static final String DEAD_EXCHANGE_NAME = "dead_exchange";
    // 普通队列名称
    public static final String NORMAL_QUEUE = "normal_queue";
    // 死信名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        channel.exchangeDeclare(NORMAL_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 死信交换机
        channel.exchangeDeclare(DEAD_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 声明普通队列
        // 过期时间
        Map<String, Object> propMap = new HashMap<>();
        // 设置死信交换机
        propMap.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        // 设置死信routing-key
        propMap.put("x-dead-letter-routing-key", "lisi");
        // 设置正常队列的最大长度
//        propMap.put("x-max-length", 6);
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, propMap);
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);

        // 绑定交换机和队列
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE_NAME, "zhangsan");
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE_NAME, "lisi");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), "UTF-8");
            if(msg.equals("info5")) {
                channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
            } else {
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
            System.out.println(msg);
        };

        // 手动 ACK
        channel.basicConsume(NORMAL_QUEUE, false, deliverCallback, consumerTag -> {
        });

    }

}
