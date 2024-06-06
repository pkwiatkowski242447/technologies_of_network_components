package pl.tks.gr3.cinema.adapters.api;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.GeneralRepositoryException;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryInterface extends AutoCloseable {

    // Create methods

    ClientEnt createClient(UUID clientId, String clientLogin) throws UserRepositoryException;

    // Read methods

    UserEnt findByUUID(UUID userID) throws UserRepositoryException;

    ClientEnt findClientByUUID(UUID clientID) throws UserRepositoryException;

    ClientEnt findClientByLogin(String loginValue) throws UserRepositoryException;

    List<ClientEnt> findAllClientsMatchingLogin(String loginValue) throws UserRepositoryException;

    List<ClientEnt> findAllClients() throws UserRepositoryException;

    // Update methods

    void updateClient(ClientEnt client) throws UserRepositoryException;

    void activate(UserEnt user) throws UserRepositoryException;
    void deactivate(UserEnt user) throws UserRepositoryException;

    // Delete methods

    void delete(UUID userID) throws GeneralRepositoryException;

    // Other required methods

    List<TicketEnt> getListOfTicketsForClient(UUID userID, String name) throws UserRepositoryException;

    @Override
    void close();
}
