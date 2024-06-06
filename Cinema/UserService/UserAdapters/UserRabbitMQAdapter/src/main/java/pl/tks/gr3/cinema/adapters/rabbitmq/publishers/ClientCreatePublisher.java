package pl.tks.gr3.cinema.adapters.rabbitmq.publishers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.rabbitmq.messages.ClientCreateMessage;

@Slf4j
@Service
public class ClientCreatePublisher {

    @Value("${rabbitmq.create.client.key}")
    private String createKey;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ClientCreatePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(@NonNull ClientCreateMessage createMessage) {
        log.debug("Sending client create message message: {} to exchange: {} with key: {}.",
                createMessage, exchangeName, createKey);
        rabbitTemplate.convertAndSend(this.exchangeName,
                createKey,
                createMessage);
    }
}
