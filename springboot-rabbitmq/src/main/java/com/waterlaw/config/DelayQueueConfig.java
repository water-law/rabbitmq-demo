package com.waterlaw.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayQueueConfig {

    // 延迟交换机
    public static final String DELAY_EXCHANGE = "delayed.exchange";
    // 延迟队列
    public static final String DELAY_QUEUE = "delayed.queue";
    // 路由
    public static final String DELAY_ROUTE = "delayed.routing";

    // 声明exchange
    @Bean("delayedExchange")
    public CustomExchange delayedExchange() {
        Map<String, Object> argument = new HashMap<>();
        argument.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_EXCHANGE, "x-delayed-message", true, false, argument);
    }

    @Bean
    public Queue delayedQueue() {
        return new Queue(DELAY_QUEUE);
    }

    // 绑定 交换机和队列
    @Bean
    public Binding delayedQueueBindingRoutingKey(@Qualifier("delayedQueue") Queue delayedQueue, @Qualifier("delayedExchange") CustomExchange delayedExchange) {
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAY_ROUTE).noargs();
    }


}
