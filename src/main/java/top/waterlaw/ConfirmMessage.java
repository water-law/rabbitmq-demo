package top.waterlaw;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;
import top.waterlaw.utils.RabbitMqUtil;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

public class ConfirmMessage {
    public static final int MAX_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        // publishSingle(); // 1806ms
//        publishBatch();// 113ms
        publishAsync(); // 78ms
    }



    public static void publishSingle() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMqUtil.getChannel();
        // 开启发布确认
        channel.confirmSelect();
        String QUEUE_NAME = String.valueOf(UUID.randomUUID());
        // 开启队列持久化
        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

        System.out.println("生产者启动");
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MAX_COUNT; i++) {
            String message = i + "";
            // 消息持久化 MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            boolean flag = channel.waitForConfirms();
            if(flag) {
                System.out.println("消息发送成功");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("单个确认耗时：" + (end - begin) +"ms");

    }

    public static void publishBatch() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMqUtil.getChannel();
        // 开启发布确认
        channel.confirmSelect();
        String QUEUE_NAME = String.valueOf(UUID.randomUUID());
        // 开启队列持久化
        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

        System.out.println("生产者启动");
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MAX_COUNT; i++) {
            String message = i + "";
            // 消息持久化 MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            if((i + 1) % 100 == 0) {
                boolean flag = channel.waitForConfirms();
                if(flag) {
                    System.out.println("消息发送成功");
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("批量确认耗时：" + (end - begin) +"ms");

    }



    public static void publishAsync() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMqUtil.getChannel();
        // 开启发布确认
        channel.confirmSelect();
        // 存储所有消息-线程安全的数据结构
        ConcurrentSkipListMap<Long, String> outConfirmMap = new ConcurrentSkipListMap<>();
        ConfirmCallback ackCallBack = (messageTag, mutiFlg) -> {
            // 消息确认成功回调函数
            System.out.println("确认的消息" + messageTag);
            // 删除已经确认的消息
            if(mutiFlg) {
                ConcurrentNavigableMap confirmedMap = outConfirmMap.headMap(messageTag);
                confirmedMap.clear();
            } else {
                outConfirmMap.remove(messageTag);
            }
        };
        ConfirmCallback nAckCallBack = (messageTag, mutiFlg) -> {
            // 消息确认失败回调函数
            System.out.println("未确认的消息" + messageTag);
        };
        // 准备监听器
        channel.addConfirmListener(ackCallBack, nAckCallBack);

        String QUEUE_NAME = String.valueOf(UUID.randomUUID());
        // 开启队列持久化
        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

        System.out.println("生产者启动");
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MAX_COUNT; i++) {
            String message = i + "";
            // 消息持久化 MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            outConfirmMap.put(channel.getNextPublishSeqNo(), message);
        }
        long end = System.currentTimeMillis();
        System.out.println("异步确认耗时：" + (end - begin) +"ms");

    }
}
