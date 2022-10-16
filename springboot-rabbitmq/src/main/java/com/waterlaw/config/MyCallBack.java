package com.waterlaw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;


@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 将 MyCallBack 注入 RabbitTemplate
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 交换机回调方法
     * @param correlationData：保存回调消息ID及其信息
     * @param ack： 交换机确认收到消息返回 true
     * @param cause: 交换机没收到消息的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData==null?"":correlationData.getId();
        if(ack) {
            log.info("交换机收到ID：{}的消息", id);
        } else {
            log.info("交换机未收到ID：{}的消息，原因:{}", id, cause);
        }
    }

    /**
     * 消息没有正确路由时的回退方法
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息{}被交换机：{}退回，原因:{}, 路由：{}", new String(message.getBody()), exchange, replyText, routingKey);
    }
}
