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
public class ClientDeactivatePublisher {

    @Value("${rabbitmq.deactivate.client.key}")
    private String deactivateKey;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ClientDeactivatePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(@NonNull ClientUUIDMessage clientUUIDMessage) {
        log.debug("Sending client deactivate message message: {} to exchange: {} with key: {}.",
                clientUUIDMessage, exchangeName, deactivateKey);
        rabbitTemplate.convertAndSend(this.exchangeName,
                deactivateKey,
                clientUUIDMessage);
    }
}
