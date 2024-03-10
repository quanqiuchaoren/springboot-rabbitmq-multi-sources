package com.qqcr.train.rabbitmq.multi.source.common.setting;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 在此队列中声明队列、交换机、binding等信息
 */
@Configuration
public class Business2RabbitMqBindConfig {
    public static final String BUSINESS_2_QUEUE_NAME = "business_2_queue_name";

    /**
     * 声明一个Queue队列，队列名字为BUSINESS_2_QUEUE_NAME
     * 这个队列和business2RabbitAdmin绑定，而business2RabbitAdmin和具体的数据源绑定上了，
     * 这样就知道具体是在哪个数据源上创建这个队列了
     */
    @Bean(BUSINESS_2_QUEUE_NAME)
    public Queue bootQueue(@Qualifier("business2RabbitAdmin") RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue(BUSINESS_2_QUEUE_NAME);
        queue.setAdminsThatShouldDeclare(rabbitAdmin);
        return queue;
    }
}
