package pl.tks.gr3.cinema.adapters.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Configuration
@EnableRabbit
@PropertySource(value = "classpath:consts.properties")
public class RabbitMQConfig implements RabbitListenerConfigurer {

    // Outbound communication

    // Consts - queue names

    @Value("${rabbitmq.create.client.queue.name}")
    private String createClientQueueName;

    @Value("${rabbitmq.activate.client.queue.name}")
    private String activateClientQueueName;

    @Value("${rabbitmq.deactivate.client.queue.name}")
    private String deactivateClientQueueName;

    @Value("${rabbitmq.create-rollback.client.queue.name}")
    private String createRollbackClientQueueName;

    @Value("${rabbitmq.activate-rollback.client.queue.name}")
    private String activateRollbackClientQueueName;

    @Value("${rabbitmq.deactivate-rollback.client.queue.name}")
    private String deactivateRollbackClientQueueName;

    // Consts - routing keys

    @Value("${rabbitmq.create.client.key}")
    private String createClientKey;

    @Value("${rabbitmq.activate.client.key}")
    private String activateClientKey;

    @Value("${rabbitmq.deactivate.client.key}")
    private String deactivateClientKey;

    @Value("${rabbitmq.create-rollback.client.key}")
    private String createRollbackClientKey;

    @Value("${rabbitmq.activate-rollback.client.key}")
    private String activateRollbackClientKey;

    @Value("${rabbitmq.deactivate-rollback.client.key}")
    private String deactivateRollbackClientKey;

    // Consts - exchange names

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    // Queue config

    @Bean
    Queue createClientQueue() {
        return QueueBuilder.durable(this.createClientQueueName).build();
    }

    @Bean
    Queue activateClientQueue() {
        return QueueBuilder.durable(this.activateClientQueueName).build();
    }

    @Bean
    Queue deactivateClientQueue() {
        return QueueBuilder.durable(this.deactivateClientQueueName).build();
    }

    @Bean
    Queue createRollbackClientQueue() {
        return QueueBuilder.durable(this.createRollbackClientQueueName).build();
    }

    @Bean
    Queue activateRollbackClientQueue() {
        return QueueBuilder.durable(this.activateRollbackClientQueueName).build();
    }

    @Bean
    Queue deactivateRollbackClientQueue() {
        return QueueBuilder.durable(this.deactivateRollbackClientQueueName).build();
    }

    // Exchange config

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(this.exchangeName);
    }

    // Bindings config

    @Bean
    public Binding createBinding() {
        return BindingBuilder.bind(createClientQueue())
                .to(exchange())
                .with(createClientKey);
    }

    @Bean
    public Binding activateBinding() {
        return BindingBuilder.bind(activateClientQueue())
                .to(exchange())
                .with(activateClientKey);
    }

    @Bean
    public Binding deactivateBinding() {
        return BindingBuilder.bind(deactivateClientQueue())
                .to(exchange())
                .with(deactivateClientKey);
    }

    @Bean
    public Binding createRollbackBinding() {
        return BindingBuilder.bind(createRollbackClientQueue())
                .to(exchange())
                .with(createRollbackClientKey);
    }

    @Bean
    public Binding activateRollbackBinding() {
        return BindingBuilder.bind(activateRollbackClientQueue())
                .to(exchange())
                .with(activateRollbackClientKey);
    }

    @Bean
    public Binding deactivateRollbackBinding() {
        return BindingBuilder.bind(deactivateRollbackClientQueue())
                .to(exchange())
                .with(deactivateRollbackClientKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        return rabbitTemplate;
    }

    // Inbound communication

    @Override
    public void configureRabbitListeners(
            RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());
    }

    @Bean
    public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        return factory;
    }
}
