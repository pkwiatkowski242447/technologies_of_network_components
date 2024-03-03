package pl.tks.gr3.application_services.services.interfaces;

import pl.tks.gr3.cinema.exceptions.services.GeneralServiceException;
import pl.tks.gr3.cinema.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface UserServiceInterface<Type> extends ServiceInterface<Type> {

    // Create methods

    Type create(String login, String password) throws GeneralServiceException;

    // Read methods

    Type findByLogin(String login) throws GeneralServiceException;
    List<Type> findAllMatchingLogin(String loginToBeMatched) throws GeneralServiceException;

    // Update methods

    void update(Type element) throws GeneralServiceException;

    // Activate method

    void activate(UUID elementID) throws GeneralServiceException;

    // Deactivate method

    void deactivate(UUID elementID) throws GeneralServiceException;

    // Delete methods

    void delete(UUID userID) throws GeneralServiceException;

    // Other methods

    List<Ticket> getTicketsForClient(UUID userID) throws GeneralServiceException;

}
