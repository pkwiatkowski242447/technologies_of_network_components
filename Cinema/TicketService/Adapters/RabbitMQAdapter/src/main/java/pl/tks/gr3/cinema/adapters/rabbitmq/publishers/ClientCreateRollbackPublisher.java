package pl.tks.gr3.cinema.adapters.rabbitmq.publishers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.rabbitmq.messages.ClientUUIDMessage;

@Slf4j
@Service
public class ClientCreateRollbackPublisher {

    @Value("${rabbitmq.create-rollback.client.key}")
    private String createKey;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ClientCreateRollbackPublisher(RabbitTemplate rabbitTemplate)  {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(@NonNull ClientUUIDMessage clientUUIDMessage) {
        log.debug("Sending client create rollback message message: {} to exchange: {} with key: {}.",
                clientUUIDMessage, exchangeName, createKey);
        rabbitTemplate.convertAndSend(this.exchangeName,
                createKey,
                clientUUIDMessage);
    }
}
