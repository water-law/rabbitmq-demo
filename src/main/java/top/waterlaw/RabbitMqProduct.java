package top.waterlaw;

import com.rabbitmq.client.Channel;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMqProduct {

    public static final String QUEUE_NAME = "test";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = "test";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("success");
    }
}
