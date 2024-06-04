package pl.tks.gr3.cinema.adapters.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.rabbitmq.messages.ClientCreateMessage;
import pl.tks.gr3.cinema.adapters.rabbitmq.messages.ClientUUIDMessage;
import pl.tks.gr3.cinema.application_services.services.ClientService;

@Slf4j
@Service
public class ClientRollbackMessageListener {

    private final ClientService clientService;

    @Autowired
    public ClientRollbackMessageListener(ClientService clientService) {
        this.clientService = clientService;
    }

    @RabbitListener(queues = "${rabbitmq.create-rollback.client.queue.name}")
    public void createClient(@Payload ClientCreateMessage createMessage) {
        log.debug("Received client create message: {}.", createMessage);
        clientService.delete(createMessage.getClientId());
    }

    @RabbitListener(queues = "${rabbitmq.activate-rollback.client.queue.name}")
    public void activateClientRollback(@Payload ClientUUIDMessage uuidMessage) {
        log.debug("Received client activate message: {}.", uuidMessage);
        clientService.deactivate(uuidMessage.getClientId());
    }

    @RabbitListener(queues = "${rabbitmq.deactivate-rollback.client.queue.name}")
    public void deactivateClientRollback(@Payload ClientUUIDMessage uuidMessage) {
        log.debug("Received client deactivate message: {}.", uuidMessage);
        clientService.activate(uuidMessage.getClientId());
    }
}
