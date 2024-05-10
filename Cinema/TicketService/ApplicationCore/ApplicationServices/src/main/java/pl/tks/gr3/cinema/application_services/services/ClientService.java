package pl.tks.gr3.cinema.application_services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryUserNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.*;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService implements ReadUserUseCase<Client>, WriteUserUseCase<Client> {

    private final CreateUserPort createUserPort;
    private final ReadUserPort readUserPort;
    private final UpdateUserPort updateUserPort;
    private final ActivateUserPort activateUserPort;
    private final DeactivateUserPort deactivateUserPort;
    private final DeleteUserPort deleteUserPort;

    @Autowired
    public ClientService(CreateUserPort createUserPort,
                         ReadUserPort readUserPort,
                         UpdateUserPort updateUserPort,
                         ActivateUserPort activateUserPort,
                         DeactivateUserPort deactivateUserPort,
                         DeleteUserPort deleteUserPort) {
        this.createUserPort = createUserPort;
        this.readUserPort = readUserPort;
        this.updateUserPort = updateUserPort;
        this.activateUserPort = activateUserPort;
        this.deactivateUserPort = deactivateUserPort;
        this.deleteUserPort = deleteUserPort;
    }

    @Override
    public Client create(String login, String password) throws ClientServiceCreateException {
        try {
            return this.createUserPort.createClient(login, password);
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new ClientServiceCreateClientDuplicateLoginException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Client findByUUID(UUID clientID) throws ClientServiceReadException {
        try {
            return this.readUserPort.findClientByUUID(clientID);
        } catch (UserRepositoryUserNotFoundException exception) {
            throw new ClientServiceClientNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Client findByLogin(String login) throws ClientServiceReadException {
        try {
            return this.readUserPort.findClientByLogin(login);
        } catch (UserRepositoryUserNotFoundException exception) {
            throw new ClientServiceClientNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Client client) throws ClientServiceUpdateException {
        try {
            this.updateUserPort.updateClient(client);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID clientID) throws ClientServiceActivationException {
        try {
            this.activateUserPort.activate(this.readUserPort.findClientByUUID(clientID));
        } catch (UserRepositoryException exception) {
            throw new ClientServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID clientID) throws ClientServiceDeactivationException {
        try {
            this.deactivateUserPort.deactivate(this.readUserPort.findClientByUUID(clientID));
        } catch (UserRepositoryException exception) {
            throw new ClientServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForUser(UUID clientID) throws ClientServiceReadException {
        try {
            return this.readUserPort.getListOfTickets(clientID, UserEntConstants.CLIENT_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws ClientServiceDeleteException {
        try {
            this.deleteUserPort.delete(userID, UserEntConstants.CLIENT_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
