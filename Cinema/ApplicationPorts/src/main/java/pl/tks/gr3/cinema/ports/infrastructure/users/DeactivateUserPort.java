package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.domain_model.model.users.User;

public interface DeactivateUserPort {
    void deactivate(User user, String name) throws UserRepositoryException;
}
