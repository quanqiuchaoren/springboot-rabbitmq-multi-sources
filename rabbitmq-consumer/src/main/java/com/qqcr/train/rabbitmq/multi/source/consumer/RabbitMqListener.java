package com.qqcr.train.rabbitmq.multi.source.consumer;

import com.qqcr.train.rabbitmq.multi.source.common.setting.Business1RabbitMqBindConfig;
import com.qqcr.train.rabbitmq.multi.source.common.setting.Business2RabbitMqBindConfig;
import com.qqcr.train.rabbitmq.multi.source.common.setting.PrimaryRabbitMqBindConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 多数据源配置监听者，参考：
 * https://codeleading.com/article/71581572669/
 */
@Component
public class RabbitMqListener {
    /**
     * @param message
     */
    @RabbitListener(queues = PrimaryRabbitMqBindConfig.PRIMARY_QUEUE_NAME,
            containerFactory = "primaryListenerFactory")
    public void primaryListener(Message message) {
        System.out.println("--------------------------------------------------------------------");
        System.out.println("primaryListener message:" + new String(message.getBody()));
    }

    /**
     * 消费者与containerFactory绑定，而containerFactory与具体的数据源绑定，这样就知道是监听具体的哪个数据源上的队列了
     */
    @RabbitListener(queues = Business1RabbitMqBindConfig.BUSINESS_1_QUEUE_NAME,
            containerFactory = "business1ListenerFactory")
    public void business1Listener(Message message) {
        System.out.println("--------------------------------------------------------------------");
        System.out.println("business1Listener message:" + new String(message.getBody()));
    }

    @RabbitListener(queues = Business2RabbitMqBindConfig.BUSINESS_2_QUEUE_NAME,
            containerFactory = "business2ListenerFactory")
    public void business2Listener(Message message) {
        System.out.println("--------------------------------------------------------------------");
        System.out.println("business2Listener message:" + new String(message.getBody()));
    }
}
