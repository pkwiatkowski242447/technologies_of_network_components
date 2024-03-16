package pl.tks.gr3.cinema.ports.userinterface;

import pl.tks.gr3.cinema.domain_model.Ticket;

import java.util.List;
import java.util.UUID;

public interface UserServiceInterface<Type> extends ServiceInterface<Type> {

    // Create methods

    Type create(String login, String password);

    // Read methods

    Type findByLogin(String login);
    List<Type> findAllMatchingLogin(String loginToBeMatched);

    // Update methods

    void update(Type element);

    // Activate method

    void activate(UUID elementID);

    // Deactivate method

    void deactivate(UUID elementID);

    // Delete methods

    void delete(UUID userID);

    // Other methods

    List<Ticket> getTicketsForUser(UUID userID);

}
