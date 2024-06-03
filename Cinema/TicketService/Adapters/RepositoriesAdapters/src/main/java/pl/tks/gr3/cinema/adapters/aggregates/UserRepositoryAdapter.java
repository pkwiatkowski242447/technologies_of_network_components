package pl.tks.gr3.cinema.adapters.aggregates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.tks.gr3.cinema.adapters.api.UserRepositoryInterface;
import pl.tks.gr3.cinema.adapters.converters.TicketConverter;
import pl.tks.gr3.cinema.adapters.converters.UserConverter;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.User;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;

import java.util.List;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements CreateUserPort, ReadUserPort, UpdateUserPort, ActivateUserPort, DeactivateUserPort, DeleteUserPort {

    private final UserRepositoryInterface userRepository;

    @Autowired
    public UserRepositoryAdapter(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    // C

    @Override
    public Client createClient(UUID clientId, String clientLogin) throws UserRepositoryException {
        return UserConverter.convertToClient(userRepository.createClient(clientId, clientLogin));
    }

    // R

    @Override
    public User findByUUID(UUID userID) throws UserRepositoryException {
        UserEnt userEnt = this.userRepository.findByUUID(userID);
        return UserConverter.convertToClient(userEnt);
    }

    @Override
    public Client findClientByUUID(UUID clientID) throws UserRepositoryException {
        return UserConverter.convertToClient(userRepository.findClientByUUID(clientID));
    }

    @Override
    public Client findClientByLogin(String loginValue) throws UserRepositoryException {
        return UserConverter.convertToClient(userRepository.findClientByLogin(loginValue));
    }

    @Override
    public List<Ticket> getListOfTickets(UUID userID, String discriminator) throws UserRepositoryException {
        return this.userRepository.getListOfTicketsForClient(userID, discriminator).stream().map(TicketConverter::convertToTicket).toList();
    }

    // U

    @Override
    public void updateClient(Client client) throws UserRepositoryException {
        userRepository.updateClient(UserConverter.convertToClientEnt(client));
    }

    // D

    @Override
    public void delete(UUID userID) throws UserRepositoryException {
        userRepository.delete(userID);
    }

    // Other

    @Override
    public void activate(User user) throws UserRepositoryException {
        userRepository.activate(UserConverter.convertToClientEnt(user));
    }

    @Override
    public void deactivate(User user) throws UserRepositoryException {
        userRepository.deactivate(UserConverter.convertToClientEnt(user));
    }
}
