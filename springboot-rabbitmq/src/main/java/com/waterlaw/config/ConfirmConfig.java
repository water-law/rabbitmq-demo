package com.waterlaw.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ConfirmConfig {


    // 确认交换机
    public static final String CONFIRM_EXCHANGE = "confirm.exchange";
    // 队列
    public static final String CONFIRM_QUEUE = "confirm.queue";
    // 路由
    public static final String CONFIRM_ROUTE = "key1";

    // 备份交换机
    public static final String BACKUP_EXCHANGE = "backup.exchange";
    // 队列
    public static final String BACKUP_QUEUE = "backup.queue";
    // 报警队列
    public static final String WARNING_QUEUE = "warning.queue";


    // 声明exchange
    @Bean("confirmExchange")
    public DirectExchange confirmExchange() {
        // 设置备份交换机
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE).durable(true)
                .withArgument("alternate-exchange", BACKUP_EXCHANGE)
                .build();
    }

    @Bean
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE).build();
    }

    // 绑定 交换机和队列
    @Bean
    public Binding confirmQueueBindingRoutingKey(@Qualifier("confirmQueue") Queue confirmQueue, @Qualifier("confirmExchange") DirectExchange confirmExchange) {
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTE);
    }


    // 声明exchange
    @Bean("backupExchange")
    public FanoutExchange backupExchange() {
        return new FanoutExchange(BACKUP_EXCHANGE);
    }

    @Bean
    public Queue backupQueue() {
        return QueueBuilder.durable(BACKUP_QUEUE).build();
    }

    @Bean
    public Queue warningQueue() {
        return QueueBuilder.durable(WARNING_QUEUE).build();
    }

    // 绑定 交换机和队列
    @Bean
    public Binding backupQueueBindingRoutingKey(@Qualifier("backupQueue") Queue backupQueue, @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(backupQueue).to(backupExchange);
    }

    // 绑定 交换机和队列
    @Bean
    public Binding warningQueueBindingRoutingKey(@Qualifier("warningQueue") Queue warningQueue, @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(warningQueue).to(backupExchange);
    }
}
