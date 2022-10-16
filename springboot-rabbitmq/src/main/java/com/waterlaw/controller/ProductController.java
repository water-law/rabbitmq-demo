package com.waterlaw.controller;

import com.waterlaw.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/confirm")
public class ProductController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 确认交换机
    @GetMapping("/sendMsg/{message}")
    public void testConfirmMsg(@PathVariable String message) {
        log.info("当前时间：{}, 发送一条信息{}到confirm.queue队列", new Date().toString(), message);
        CorrelationData correlationData = new CorrelationData("1");

        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE, ConfirmConfig.CONFIRM_ROUTE, "消息来自于confirm.quque的队列:" + message, correlationData);
    }

}
