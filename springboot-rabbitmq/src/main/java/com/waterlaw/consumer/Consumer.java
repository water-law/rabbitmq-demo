package com.waterlaw.consumer;


import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class Consumer {

    @RabbitListener(queues = "confirm.queue")
    public void receiveD(Message message, Channel channel) {
        String msg = new String(message.getBody());
        log.info("当前时间：{}, 收到一条信息：{}", new Date().toString(), msg);
    }

    @RabbitListener(queues = "warning.queue")
    public void receiveWarning(Message message, Channel channel) {
        String msg = new String(message.getBody());
        log.info("当前时间：{}, 收到一条报警信息：{}", new Date().toString(), msg);
    }
}
