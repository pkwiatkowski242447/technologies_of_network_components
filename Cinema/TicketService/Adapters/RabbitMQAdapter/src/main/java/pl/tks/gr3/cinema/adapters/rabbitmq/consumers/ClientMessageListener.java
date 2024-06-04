package pl.tks.gr3.cinema.adapters.rabbitmq.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.rabbitmq.messages.ClientCreateMessage;
import pl.tks.gr3.cinema.adapters.rabbitmq.messages.ClientUUIDMessage;
import pl.tks.gr3.cinema.adapters.rabbitmq.publishers.ClientActivateRollbackPublisher;
import pl.tks.gr3.cinema.adapters.rabbitmq.publishers.ClientCreateRollbackPublisher;
import pl.tks.gr3.cinema.adapters.rabbitmq.publishers.ClientDeactivateRollbackPublisher;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceActivationException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceCreateException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceDeactivationException;
import pl.tks.gr3.cinema.application_services.services.ClientService;

@Slf4j
@Service
public class ClientMessageListener {

    private final ClientService clientService;
    private final ClientCreateRollbackPublisher createRollbackPublisher;
    private final ClientActivateRollbackPublisher activateRollbackPublisher;
    private final ClientDeactivateRollbackPublisher deactivateRollbackPublisher;

    @Autowired
    public ClientMessageListener(ClientService clientService,
                                 ClientCreateRollbackPublisher createRollbackPublisher,
                                 ClientActivateRollbackPublisher activateRollbackPublisher,
                                 ClientDeactivateRollbackPublisher deactivateRollbackPublisher) {
        this.clientService = clientService;
        this.createRollbackPublisher = createRollbackPublisher;
        this.activateRollbackPublisher = activateRollbackPublisher;
        this.deactivateRollbackPublisher = deactivateRollbackPublisher;
    }

    @RabbitListener(queues = "${rabbitmq.create.client.queue.name}")
    public void createClient(@Payload ClientCreateMessage createMessage) {
        log.debug("Received client create message: {}.", createMessage);
        try {
            clientService.create(createMessage.getClientId(), createMessage.getClientLogin());
        } catch (ClientServiceCreateException exception) {
            this.createRollbackPublisher.publish(new ClientUUIDMessage(createMessage.getClientId()));
        }
    }

    @RabbitListener(queues = "${rabbitmq.activate.client.queue.name}")
    public void activateClient(@Payload ClientUUIDMessage clientUUIDMessage) {
        log.debug("Received client activate message: {}.", clientUUIDMessage);
        try {
            clientService.activate(clientUUIDMessage.getClientId());
        } catch (ClientServiceActivationException exception) {
            this.activateRollbackPublisher.publish(clientUUIDMessage);
        }
    }

    @RabbitListener(queues = "${rabbitmq.deactivate.client.queue.name}")
    public void deactivateClient(@Payload ClientUUIDMessage clientUUIDMessage) {
        log.debug("Received client deactivate message: {}.", clientUUIDMessage);
        try {
            clientService.deactivate(clientUUIDMessage.getClientId());
        } catch (ClientServiceDeactivationException exception) {
            this.deactivateRollbackPublisher.publish(clientUUIDMessage);
        }
    }
}
