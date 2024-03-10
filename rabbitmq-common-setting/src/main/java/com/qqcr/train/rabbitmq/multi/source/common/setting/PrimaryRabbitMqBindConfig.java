package com.qqcr.train.rabbitmq.multi.source.common.setting;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 在此队列中声明队列、交换机、binding等信息
 */
@Configuration
public class PrimaryRabbitMqBindConfig {
    public static final String PRIMARY_QUEUE_NAME = "primary_queue_name";

    /**
     * 声明一个Queue队列，队列名字为PRIMARY_QUEUE_NAME
     */
    @Bean(PRIMARY_QUEUE_NAME)
    public Queue bootQueue(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue(PRIMARY_QUEUE_NAME);
        queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }
}
