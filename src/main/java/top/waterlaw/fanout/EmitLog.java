package top.waterlaw.fanout;

import com.rabbitmq.client.Channel;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class EmitLog {

    // 交换机名称
    public static final String LOGS_EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        // 声明一个交换机
        channel.exchangeDeclare(LOGS_EXCHANGE_NAME, "fanout");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            System.out.println("生产者发出消息：" + message);
            channel.basicPublish(LOGS_EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
