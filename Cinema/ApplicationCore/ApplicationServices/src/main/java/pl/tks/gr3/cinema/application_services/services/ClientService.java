package pl.tks.gr3.cinema.application_services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.consts.model.UserConstants;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryUserNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.*;
import pl.tks.gr3.cinema.domain_model.model.Ticket;
import pl.tks.gr3.cinema.domain_model.model.users.Client;
import pl.tks.gr3.cinema.ports.userinterface.UserServiceInterface;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService implements UserServiceInterface<Client> {

    private UserRepositoryAdapter userRepository;

    public ClientService() {
    }

    @Autowired
    public ClientService(UserRepositoryAdapter userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Client create(String login, String password) throws ClientServiceCreateException {
        try {
            return this.userRepository.createClient(login, password);
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new ClientServiceCreateClientDuplicateLoginException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Client findByUUID(UUID clientID) throws ClientServiceReadException {
        try {
            return this.userRepository.findClientByUUID(clientID);
        } catch (UserRepositoryUserNotFoundException exception) {
            throw new ClientServiceClientNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Client findByLogin(String login) throws ClientServiceReadException {
        try {
            return this.userRepository.findClientByLogin(login);
        } catch (UserRepositoryUserNotFoundException exception) {
            throw new ClientServiceClientNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Client> findAllMatchingLogin(String loginToBeMatched) throws ClientServiceReadException {
        try {
            return this.userRepository.findAllClientsMatchingLogin(loginToBeMatched);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Client> findAll() throws ClientServiceReadException {
        try {
            return this.userRepository.findAllClients();
        } catch (UserRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Client client) throws ClientServiceUpdateException {
        try {
            this.userRepository.updateClient(client);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID clientID) throws ClientServiceActivationException {
        try {
            this.userRepository.activate(this.userRepository.findByUUID(clientID));
        } catch (UserRepositoryException exception) {
            throw new ClientServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID clientID) throws ClientServiceDeactivationException {
        try {
            this.userRepository.deactivate(this.userRepository.findByUUID(clientID));
        } catch (UserRepositoryException exception) {
            throw new ClientServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForUser(UUID clientID) throws ClientServiceReadException {
        try {
            return this.userRepository.getListOfTickets(clientID, UserConstants.CLIENT_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws ClientServiceDeleteException {
        try {
            this.userRepository.delete(userID, UserConstants.CLIENT_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new ClientServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
