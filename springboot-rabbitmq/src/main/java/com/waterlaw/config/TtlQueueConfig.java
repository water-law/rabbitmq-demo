package com.waterlaw.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TtlQueueConfig {


    // 普通交换机
    public static final String NORMAL_EXCHANGE = "X";
    // 死信交换机
    public static final String DEAD_LETTER_EXCHANGE = "Y";


    // 普通队列
    public static final String NORMAL_QUEUEA = "QA";
    // 普通队列
    public static final String NORMAL_QUEUEB = "QB";
    // 普通队列
    public static final String NORMAL_QUEUEC = "QC";
    // 死信队列
    public static final String DEAD_LETTER_QUEUE = "QD";

    // 声明exchange
    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(NORMAL_EXCHANGE);
    }

    @Bean("yExchange")
    public DirectExchange yExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean("queueA")
    public Queue queueA() {
        // 设置死信交换机、设置死信队列
        Map<String, Object> argumentMap = new HashMap<>(3);
        argumentMap.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        argumentMap.put("x-dead-letter-routing-key", "YD");
        // 设置 ttl
        argumentMap.put("x-message-ttl", 10 * 1000);
        return QueueBuilder.durable(NORMAL_QUEUEA).withArguments(argumentMap).build();
    }

    @Bean("queueB")
    public Queue queueB() {
        // 设置死信交换机、设置死信队列
        Map<String, Object> argumentMap = new HashMap<>(3);
        argumentMap.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        argumentMap.put("x-dead-letter-routing-key", "YD");
        // 设置 ttl
        argumentMap.put("x-message-ttl", 40 * 1000);
        return QueueBuilder.durable(NORMAL_QUEUEB).withArguments(argumentMap).build();
    }

    @Bean("queueC")
    public Queue queueC() {
        // 设置死信交换机、设置死信队列
        Map<String, Object> argumentMap = new HashMap<>(2);
        argumentMap.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        argumentMap.put("x-dead-letter-routing-key", "YD");
        return QueueBuilder.durable(NORMAL_QUEUEC).withArguments(argumentMap).build();
    }

    @Bean("queueD")
    public Queue queueD() {
        // 设置死信交换机、设置死信队列
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    // 绑定 交换机和队列
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queueC, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
    }

    @Bean
    public Binding queueDBindingX(@Qualifier("queueD") Queue queueD, @Qualifier("yExchange") DirectExchange yExchange) {
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }
}
