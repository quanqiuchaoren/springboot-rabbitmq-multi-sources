package com.qqcr.train.rabbitmq.multi.source.common.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 此类可以参考：
 * SpringBoot配置多个RabbitMQ源：
 * https://blog.csdn.net/Yu_Mariam/article/details/123938940
 * https://blog.csdn.net/tmr1016/article/details/108623855
 */
@Configuration
public class Business2RabbitMqConfig {
    @Autowired
    @Qualifier(value = "business2RabbitmqProperties")
    private Business2RabbitMqConfigProperties business2RabbitMqConfigProperties;

    /**
     * 配置数据源连接
     *
     * @param resourceLoader
     * @return
     * @throws Exception
     */
    @Bean("business2RabbitMqDataSourceFactory")
    public ConnectionFactory rabbitMqDataSource1Factory(ResourceLoader resourceLoader) throws Exception {
        RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigure =
                new RabbitConnectionFactoryBeanConfigurer(resourceLoader, business2RabbitMqConfigProperties.getRabbitmq());
        RabbitConnectionFactoryBean factoryBean = new RabbitConnectionFactoryBean();
        rabbitConnectionFactoryBeanConfigure.configure(factoryBean);
        factoryBean.afterPropertiesSet();

        CachingConnectionFactory factory = new CachingConnectionFactory(Objects.requireNonNull(factoryBean.getObject()));
        new CachingConnectionFactoryConfigurer(business2RabbitMqConfigProperties.getRabbitmq()).configure(factory);
        return factory;
    }

    /**
     * RabbitAdmin和ConnectionFactory绑定，定义的队列、交换机、Bind就和RabbitAdmin绑定，
     * 这样程序就知道队列、交换机、Bind在具体的哪个数据源上创建了。
     * 当有多个数据源的时候，如果只有一个数据源配置了RabbitAdmin，则所有的queue、exchange、bind都会在该RabbitAdmin绑定的数据源上创建。
     *
     * @param connectionFactory
     * @return
     */
    @Bean("business2RabbitAdmin")
    public RabbitAdmin rabbitAdmin(@Qualifier("business2RabbitMqDataSourceFactory") ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * 配置生产者的rabbit template(无生产者可删除此类)
     *
     * @param connectionFactory
     * @return
     */
    @Bean(name = "business2RabbitTemplate")
    public RabbitTemplate innerRabbitTemplate(@Qualifier("business2RabbitMqDataSourceFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    /**
     * 获取消费者通道监听类(无消费者可删除此类)
     */
    @Bean(name = "business2ListenerFactory")
    public SimpleRabbitListenerContainerFactory innerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                             @Qualifier("business2RabbitMqDataSourceFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }


    /**
     * 从配置文件中读取business2这个rabbit mq数据源的配置类，然后装配到类中的rabbitmq属性中。
     * 注：RabbitProperties这个类属于org.springframework.boot.autoconfigure.amqp包，里面有以下属性，不需要我们再自己写一个类来承载相关的属性：
     * host、port、username、password、virtualHost、ssl。
     * 要将yml文件中的business2这个rabbit mq相关的连接信息注入到RabbitProperties类中，需要将host、port、username、password、virtualHost、ssl的前缀配置成如下：
     * app.rabbitmq-datasource-business2.rabbitmq，而具体的host、port、username、password、virtualHost、ssl配置成如下格式：
     * app.rabbitmq-datasource-business2.rabbitmq.host=122.9.140.213
     * app.rabbitmq-datasource-business2.rabbitmq.port=5672
     * app.rabbitmq-datasource-business2.rabbitmq.username=guest
     * app.rabbitmq-datasource-business2.rabbitmq.password=guest
     * app.rabbitmq-datasource-business2.rabbitmq.virtual-host=/
     * ssl是一个对象，则ssl相关的配置就继续使用.进行配置，如：
     * app.rabbitmq-datasource-business2.rabbitmq.ssl.enabled=true
     * <p>
     * 创建这个工程的核心就是为了使用这个类，将配置文件中的rabbit mq数据源读到配置类中，再通过自动装配相关的代码，连接rabbit mq数据源。
     * 2022年末在做一个项目时，当时要连接两个rabbit mq，当时老项目的rabbitmq很轻松地连上了，新项目专用的rabbit mq配置了ssl连接，多数据源配置研究了很久，
     * 都不知道怎么配置ssl连接，另一个同时就使用了这种配置方式，快速地解决了这个问题，我当时惊为天人，还能这样操作。
     */
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "app.rabbitmq-datasource-business2")
    @Component(value = "business2RabbitmqProperties")
    static class Business2RabbitMqConfigProperties {
        private RabbitProperties rabbitmq;
    }
}
