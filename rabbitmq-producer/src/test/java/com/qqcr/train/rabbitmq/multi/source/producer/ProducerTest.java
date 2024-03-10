package com.qqcr.train.rabbitmq.multi.source.producer;


import com.qqcr.train.rabbitmq.multi.source.common.setting.Business1RabbitMqBindConfig;
import com.qqcr.train.rabbitmq.multi.source.common.setting.Business2RabbitMqBindConfig;
import com.qqcr.train.rabbitmq.multi.source.common.setting.PrimaryRabbitMqBindConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 参考博客：
 * https://blog.csdn.net/Cey_Tao/article/details/128098509
 */
@SpringBootTest
public class ProducerTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("business1RabbitTemplate")
    private RabbitTemplate business1RabbitTemplate;

    @Autowired
    @Qualifier("business2RabbitTemplate")
    private RabbitTemplate business2RabbitTemplate;

    /**
     * 如果数据源中没有Business1RabbitMqBindConfig.BUSINESS_1_QUEUE_NAME这个队列，发送消息会发送成功，但是不会报错，我估计可能是进入了死信交换机。
     */
    @Test
    public void testSend2ThreeDataSources() {
        rabbitTemplate.convertAndSend(PrimaryRabbitMqBindConfig.PRIMARY_QUEUE_NAME, "primary message");
        business1RabbitTemplate.convertAndSend(Business1RabbitMqBindConfig.BUSINESS_1_QUEUE_NAME, "business 1 message");
        business2RabbitTemplate.convertAndSend(Business2RabbitMqBindConfig.BUSINESS_2_QUEUE_NAME, "business 2 message");
    }

    @Test
    public void testSend() {
        /*
        方法声明虽然是：
        void convertAndSend(String routingKey, Object message) throws AmqpException;
        方法的第一个参数虽然叫routingKey，但是这里调用的时候，是直接将message发送到队列RabbitMQConfig.QUEUE_NAME中去了
         */
        rabbitTemplate.convertAndSend("queue name", "message");
    }
}
