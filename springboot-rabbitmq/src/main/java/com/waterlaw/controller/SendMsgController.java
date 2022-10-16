package com.waterlaw.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg/{message}")
    public void testSendMsg(@PathVariable String message) {
        log.info("当前时间：{}, 发送一条信息：{}", new Date().toString(), message);
        // 交换机 X， 路由 XA
        rabbitTemplate.convertAndSend("X", "XA", "消息来自于ttl=10s的队列:" + message);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自于ttl=40s的队列:" + message);
    }

    @GetMapping("/expiredMsg/{message}/{ttlTime}")
    public void testExpiredMsg(@PathVariable String message, @PathVariable String ttlTime) {
        log.info("当前时间：{}, 发送一条TTL={}的信息{}到QC队列", new Date().toString(), ttlTime, message);
        rabbitTemplate.convertAndSend("X", "XC", message, message1 -> {
            // 设置延迟时长
            message1.getMessageProperties().setExpiration(ttlTime);
            return message1;
        });
    }

    // 基于插件的延迟消息
    @GetMapping("/delayedMsg/{message}/{delayedTime}")
    public void testDelayeddMsg(@PathVariable String message, @PathVariable Integer delayedTime) {
        log.info("当前时间：{}, 发送一条时长={}的信息{}到延迟队列", new Date().toString(), delayedTime, message);
        rabbitTemplate.convertAndSend("delayed.exchange", "delayed.routing", message, message1 -> {
            // 设置延迟时长
            message1.getMessageProperties().setDelay(delayedTime);
            return message1;
        });
    }
}
